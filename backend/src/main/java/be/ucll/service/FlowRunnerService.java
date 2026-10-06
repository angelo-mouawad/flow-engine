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

        run(instance);
        return new TriggerResponse(instance.getId(), instance.getFlowStatus());
    }

    public void resumeFlow(String flowInstanceId) {
        FlowInstance instance = flowInstanceRepository.findById(flowInstanceId)
                .orElseThrow(() -> new NotFoundException("Flow instance " + flowInstanceId + " does not exist"));

        if (instance.getFlowStatus() != FlowStatus.PENDING) {
            log.info("Flow instance {} is {}, nothing to resume", flowInstanceId, instance.getFlowStatus());
            return;
        }

        instance.setFlowStatus(FlowStatus.ACTIVE);
        flowInstanceRepository.save(instance);
        run(instance);
    }

    private void run(FlowInstance instance) {
        try {
            Process current;
            while ((current = instance.getCurrentProcess()) != null) {
                if (mustWaitAt(instance, current)) {
                    instance.setFlowStatus(FlowStatus.PENDING);
                    flowInstanceRepository.save(instance);
                    callbackService.send(instance.getCallbackUrl(), instance.getId(), CallbackService.PENDING);
                    return;
                }
                instance.nextProcess();
                flowInstanceRepository.save(instance);
            }

            instance.setFlowStatus(FlowStatus.SUCCESS);
            flowInstanceRepository.save(instance);
            callbackService.send(instance.getCallbackUrl(), instance.getId(), CallbackService.COMPLETED);
        } catch (RuntimeException e) {
            markFailed(instance, e);
            throw e;
        }
    }

    private boolean mustWaitAt(FlowInstance instance, Process process) {
        return switch (process) {
            case Request request -> {
                Map<String, Object> data = instance.getDataFor(request.getRequestTypeName());
                RequestSubmission submission = requestService.processRequest(request, data, instance.getId());
                log.debug("Request {} in flow {} submitted as {}", request.getLabel(), instance.getId(), submission.getStatus());
                yield false;
            }
            case Approval approval -> requestService.hasPendingSubmission(instance.getId(), approval.getRequestSteps());
            case Notification notification -> {
                Request request = null;
                if (notification.getRequestStep() != null
                        && instance.getProcess(notification.getRequestStep()) instanceof Request target) {
                    request = target;
                }
                notificationService.send(notification, request, instance.getTitle());
                yield false;
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
