package be.ucll.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import be.ucll.controller.dto.FlowData;
import be.ucll.controller.dto.TriggerResponse;
import be.ucll.exception.DomainException;
import be.ucll.exception.NotFoundException;
import be.ucll.exception.ServiceException;
import be.ucll.model.Approval;
import be.ucll.model.FlowDefinition;
import be.ucll.model.FlowInstance;
import be.ucll.model.Notification;
import be.ucll.model.Process;
import be.ucll.model.Request;
import be.ucll.model.RequestSubmission;
import be.ucll.model.enums.FlowStatus;
import be.ucll.repository.FlowDefinitionRepository;
import be.ucll.repository.FlowInstanceRepository;

@Service
public class FlowRunnerService {

    private static final Logger log = LoggerFactory.getLogger(FlowRunnerService.class);

    /** What the runner does after looking at a step. */
    private enum Outcome { CONTINUE, WAIT, DECLINED }

    private final FlowInstanceRepository flowInstanceRepository;
    private final FlowDefinitionRepository flowDefinitionRepository;
    private final RequestService requestService;
    private final NotificationService notificationService;
    private final CallbackService callbackService;

    public FlowRunnerService(FlowInstanceRepository flowInstanceRepository,
                             FlowDefinitionRepository flowDefinitionRepository,
                             RequestService requestService,
                             NotificationService notificationService,
                             CallbackService callbackService) {
        this.flowInstanceRepository = flowInstanceRepository;
        this.flowDefinitionRepository = flowDefinitionRepository;
        this.requestService = requestService;
        this.notificationService = notificationService;
        this.callbackService = callbackService;
    }

    public TriggerResponse instantiateFlow(String definitionId, FlowData flowData) {
        FlowDefinition definition = flowDefinitionRepository.findById(definitionId)
                .orElseThrow(() -> new NotFoundException("Flow definition " + definitionId + " does not exist"));

        if (!definition.canBeTriggeredBy(flowData.triggeredBy())) {
            throw new ServiceException("Flow is not triggerable by " + flowData.triggeredBy());
        }

        FlowInstance instance = flowInstanceRepository.save(new FlowInstance(
                definition,
                flowData.title(),
                flowData.triggeredBy(),
                flowData.data(),
                flowData.callbackUrl()));

        instance = run(instance);
        return new TriggerResponse(instance.getId(), instance.getFlowStatus());
    }

    /**
     * Picks a waiting flow back up. The PENDING to ACTIVE switch is one atomic update, so when two
     * approvals land at the same moment only one of them continues the flow and the other stops here.
     */
    public void resumeFlow(String flowInstanceId) {
        if (!flowInstanceRepository.existsById(flowInstanceId)) {
            throw new NotFoundException("Flow instance " + flowInstanceId + " does not exist");
        }
        if (!flowInstanceRepository.claimPending(flowInstanceId)) {
            log.info("Flow instance {} is not waiting, or another request is already resuming it", flowInstanceId);
            return;
        }
        // Reload after the claim so the copy we run carries the version the claim just wrote.
        FlowInstance instance = flowInstanceRepository.findById(flowInstanceId)
                .orElseThrow(() -> new NotFoundException("Flow instance " + flowInstanceId + " does not exist"));
        run(instance);
    }

    /**
     * Finds flows left waiting on approvals that were already decided, for example because the server
     * stopped between saving a decision and resuming the flow, and continues them.
     */
    public int recoverStalledFlows() {
        int resumed = 0;
        for (FlowInstance instance : flowInstanceRepository.findByFlowStatus(FlowStatus.PENDING)) {
            if (instance.getCurrentProcess() instanceof Approval approval
                    && !requestService.hasPendingSubmission(instance.getId(), approval.getRequestSteps())) {
                try {
                    resumeFlow(instance.getId());
                    resumed++;
                } catch (RuntimeException e) {
                    log.warn("Could not recover flow instance {}: {}", instance.getId(), e.getMessage());
                }
            }
        }
        return resumed;
    }

    private FlowInstance run(FlowInstance instance) {
        try {
            Process current;
            while ((current = instance.getCurrentProcess()) != null) {
                switch (handle(instance, current)) {
                    case WAIT -> {
                        instance.setFlowStatus(FlowStatus.PENDING);
                        instance = flowInstanceRepository.save(instance);
                        callbackService.send(instance.getCallbackUrl(), instance.getId(), CallbackService.PENDING);
                        return instance;
                    }
                    case DECLINED -> {
                        instance.setFlowStatus(FlowStatus.DECLINED);
                        instance = flowInstanceRepository.save(instance);
                        log.info("Flow instance {} stopped at step {}: a request it waited on was declined",
                                instance.getId(), instance.getStep());
                        callbackService.send(instance.getCallbackUrl(), instance.getId(), CallbackService.DECLINED);
                        return instance;
                    }
                    case CONTINUE -> {
                        instance.nextProcess();
                        instance = flowInstanceRepository.save(instance);
                    }
                }
            }

            instance.setFlowStatus(FlowStatus.SUCCESS);
            instance = flowInstanceRepository.save(instance);
            callbackService.send(instance.getCallbackUrl(), instance.getId(), CallbackService.COMPLETED);
            return instance;
        } catch (RuntimeException e) {
            markFailed(instance, e);
            throw e;
        }
    }

    private Outcome handle(FlowInstance instance, Process process) {
        return switch (process) {
            case Request request -> {
                Map<String, Object> data = instance.getDataFor(request.getRequestTypeName());
                RequestSubmission submission = requestService.processRequest(request, data, instance.getId());
                log.debug("Request {} in flow {} submitted as {}", request.getLabel(), instance.getId(), submission.getStatus());
                yield Outcome.CONTINUE;
            }
            case Approval approval -> {
                // A declined request ends the flow; it must never be treated as if it were approved.
                if (requestService.hasDeclinedSubmission(instance.getId(), approval.getRequestSteps())) {
                    yield Outcome.DECLINED;
                }
                yield requestService.hasPendingSubmission(instance.getId(), approval.getRequestSteps())
                        ? Outcome.WAIT
                        : Outcome.CONTINUE;
            }
            case Notification notification -> {
                Request request = null;
                if (notification.getRequestStep() != null
                        && instance.getProcess(notification.getRequestStep()) instanceof Request target) {
                    request = target;
                }
                notificationService.send(notification, request, instance.getTitle());
                yield Outcome.CONTINUE;
            }
            default -> throw new DomainException("Unsupported process at step " + process.getStep());
        };
    }

    private void markFailed(FlowInstance instance, RuntimeException cause) {
        log.warn("Flow instance {} failed: {}", instance.getId(), cause.getMessage());
        try {
            instance.setFlowStatus(FlowStatus.FAILURE);
            flowInstanceRepository.save(instance);
        } catch (RuntimeException saveError) {
            log.error("Could not mark flow instance {} as failed", instance.getId(), saveError);
        }
        callbackService.send(instance.getCallbackUrl(), instance.getId(), CallbackService.FAILED);
    }
}
