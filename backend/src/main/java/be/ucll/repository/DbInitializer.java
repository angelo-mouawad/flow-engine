package be.ucll.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import be.ucll.controller.dto.FlowData;
import be.ucll.model.Approval;
import be.ucll.model.FlowDefinition;
import be.ucll.model.Notification;
import be.ucll.model.Request;
import be.ucll.model.enums.NotificationTypeEnum;
import be.ucll.model.enums.RequestTypeEnum;
import be.ucll.service.FlowRunnerService;

@Component
@Profile("dev")
public class DbInitializer {

    private static final Logger log = LoggerFactory.getLogger(DbInitializer.class);

    private static final Set<String> EVERYONE = Set.of(
            "adam@glackit.be", "samip@glackit.be", "annie@glackit.be",
            "lais@glackit.be", "mouad@glackit.be", "angelo@glackit.be");
    private static final List<String> MANAGERS = List.of("adam@glackit.be", "samip@glackit.be");

    private final FlowDefinitionRepository flowDefinitionRepository;
    private final FlowRunnerService flowRunnerService;

    public DbInitializer(FlowDefinitionRepository flowDefinitionRepository, FlowRunnerService flowRunnerService) {
        this.flowDefinitionRepository = flowDefinitionRepository;
        this.flowRunnerService = flowRunnerService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        try {
            if (flowDefinitionRepository.count() > 0) {
                log.info("Database already has flow definitions, skipping seed data");
                return;
            }
            seed();
            log.info("Seeded example flow definitions and instances");
        } catch (RuntimeException e) {
            log.warn("Could not seed example data: {}", e.getMessage());
        }
    }

    private void seed() {
        FlowDefinition absenceFlow = flowDefinitionRepository.save(new FlowDefinition(
                "Absence Reporting",
                "Collect an absence request and wait for two managers to approve it",
                EVERYONE,
                List.of(
                        new Request("Absence", RequestTypeEnum.ABSENCE_REQUEST, true, MANAGERS, 2),
                        new Approval("Manager approval", Set.of(0)),
                        new Notification("Notify Adam", NotificationTypeEnum.POPUP_NOTIFICATION,
                                List.of("adam@glackit.be"), 0))));

        FlowDefinition clockInFlow = flowDefinitionRepository.save(new FlowDefinition(
                "Clock-in Review",
                "Auto approve clock-ins inside office hours and escalate the rest",
                EVERYONE,
                List.of(
                        new Request("Clock-in", RequestTypeEnum.CLOCKIN_REQUEST, true, MANAGERS, 1),
                        new Approval("Manager approval", Set.of(0)),
                        new Notification("Notify managers", NotificationTypeEnum.POPUP_NOTIFICATION,
                                List.of(), 0))));

        FlowDefinition combinedFlow = flowDefinitionRepository.save(new FlowDefinition(
                "Absence and Clock-in",
                "Record an absence without approval, then review a clock-in",
                EVERYONE,
                List.of(
                        new Request("Absence without approval", RequestTypeEnum.ABSENCE_REQUEST, false, List.of(), 0),
                        new Notification("Notify Adam", NotificationTypeEnum.POPUP_NOTIFICATION,
                                List.of("adam@glackit.be"), 0),
                        new Request("Clock-in", RequestTypeEnum.CLOCKIN_REQUEST, true, MANAGERS, 2),
                        new Approval("Manager approval", Set.of(2)))));

        Map<String, Object> absence = Map.of(
                "Start_Date", "2025-11-10",
                "End_Date", "2025-11-19",
                "Reason", "sickness",
                "Submitted_By", "lais@glackit.be");

        Map<String, Object> lateClockIn = Map.of(
                "Date", "2025-11-19",
                "Start_Time", "2025-11-19T15:30:00+01:00",
                "End_Time", "2025-11-19T20:30:00+01:00",
                "Submitted_By", "lais@glackit.be");

        flowRunnerService.instantiateFlow(absenceFlow.getId(), new FlowData(
                "Absence for Lais", "lais@glackit.be",
                Map.of(RequestTypeEnum.ABSENCE_REQUEST, absence), null));

        flowRunnerService.instantiateFlow(clockInFlow.getId(), new FlowData(
                "Late clock-in for Lais", "lais@glackit.be",
                Map.of(RequestTypeEnum.CLOCKIN_REQUEST, lateClockIn), null));

        flowRunnerService.instantiateFlow(combinedFlow.getId(), new FlowData(
                "Absence and clock-in for Lais", "lais@glackit.be",
                Map.of(RequestTypeEnum.ABSENCE_REQUEST, absence,
                        RequestTypeEnum.CLOCKIN_REQUEST, lateClockIn), null));
    }
}
