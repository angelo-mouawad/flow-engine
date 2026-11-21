package be.ucll.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Saving a decision and resuming its flow are two separate writes. If the server stops between them,
 * the flow keeps waiting on an approval that is already decided. On startup this finds those flows
 * and continues them.
 */
@Component
public class StalledFlowRecovery {

    private static final Logger log = LoggerFactory.getLogger(StalledFlowRecovery.class);

    private final FlowRunnerService flowRunnerService;

    public StalledFlowRecovery(FlowRunnerService flowRunnerService) {
        this.flowRunnerService = flowRunnerService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void recover() {
        try {
            int resumed = flowRunnerService.recoverStalledFlows();
            if (resumed > 0) {
                log.info("Resumed {} flows that were waiting on approvals that were already decided", resumed);
            }
        } catch (RuntimeException e) {
            log.warn("Could not check for stalled flows: {}", e.getMessage());
        }
    }
}
