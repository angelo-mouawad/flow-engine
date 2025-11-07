package be.ucll.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import be.ucll.model.*;
import be.ucll.model.Process;
import be.ucll.model.strategies.request.AbsenceRequestType;
import be.ucll.service.FlowRunnerService;
import jakarta.annotation.PostConstruct;

@Component
@Profile("dev")
public class DbInitializer {

    @Autowired
    private FlowInstanceRepository flowInstanceRepository;
    @Autowired
    private FlowDefinitionRepository flowDefinitionRepository;
    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private RequestSubmissionRepository requestSubmissionRepository;
    @Autowired
    private FlowRunnerService flowRunnerService;

    @PostConstruct
    public void initialize(){

      flowDefinitionRepository.deleteAll();
      flowInstanceRepository.deleteAll();
      processRepository.deleteAll();
      requestSubmissionRepository.deleteAll();

      RequestData data = new RequestData();
      data.setField("startDate", "2025-11-10");
      data.setField("endDate", "2025-11-19");
      data.setField("submittedBy", "Hamid");
      data.setField("reason", "sickness");

      Process absence = new Request("Absence", new AbsenceRequestType());
      Process notification = new Notification("Absence", NotificationType.POPUP);

      List<Process> processes = new ArrayList<>(List.of(
                  absence,
                  notification
            ));

      processRepository.saveAll(processes);

      FlowDefinition fd1 = new FlowDefinition(
              "Absence Reporting", 
              "Receive absence requests for processsing", 
              List.of(absence, notification), 
              Trigger.ALL);

      FlowDefinition fd2 = new FlowDefinition(
              "Absence Reporting alternate", 
              "Receive absence requests for processsing", 
              List.of(notification, absence, notification), 
              Trigger.POST);

      List<FlowDefinition> fds = List.of(fd1, fd2);

      flowDefinitionRepository.saveAll(fds);

      flowRunnerService.instantiateFlow(fd1.getTitle(), data.getAllFields());
    }
}
