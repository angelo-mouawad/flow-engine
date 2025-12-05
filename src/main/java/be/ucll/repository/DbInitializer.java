package be.ucll.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import be.ucll.model.*;
import be.ucll.model.strategies.notification.EmailNotificationType;
import be.ucll.service.FlowRunnerService;
import jakarta.annotation.PostConstruct;

@Component
@Profile("dev")
public class DbInitializer {

    private FlowInstanceRepository flowInstanceRepository;
    private FlowDefinitionRepository flowDefinitionRepository;
    private RequestSubmissionRepository requestSubmissionRepository;
    private FlowRunnerService flowRunnerService;

    @Autowired
    public DbInitializer(FlowRunnerService flowRunnerService, 
            RequestSubmissionRepository requestSubmissionRepository, 
            FlowDefinitionRepository flowDefinitionRepository, 
            FlowInstanceRepository flowInstanceRepository){
        this.flowRunnerService = flowRunnerService;
        this.flowInstanceRepository = flowInstanceRepository;
        this.flowDefinitionRepository = flowDefinitionRepository;
        this.requestSubmissionRepository = requestSubmissionRepository;
    }

    @PostConstruct
    public void initialize(){

      flowDefinitionRepository.deleteAll();
      flowInstanceRepository.deleteAll();
      requestSubmissionRepository.deleteAll();

      Request absence = new Request("Absence", "ABSENCE_REQUEST", true, new String[]{"adam@glackit.be", "stef@gmail.com"}, 2);
      Request absence2 = new Request("Absence",  "ABSENCE_REQUEST", false, new String[]{}, 0);
      Approval approval = new Approval("Approval");
      Notification notification = new Notification("Notification", new EmailNotificationType());

      FlowDefinition fd1 = new FlowDefinition(
              "Absence Reporting", 
              "Receive absence requests for processsing", 
              List.of(absence, approval, notification));

      FlowDefinition fd2 = new FlowDefinition(
              "Absence Reporting alternate", 
              "Receive absence requests for processsing", 
              List.of(notification, absence2));

      List<FlowDefinition> fds = List.of(fd1, fd2);

      flowDefinitionRepository.saveAll(fds);

      RequestData data = new RequestData();
      data.setField("startDate", "2025-11-10");
      data.setField("endDate", "2025-11-19");
      data.setField("submittedBy", "Hamid");
      data.setField("reason", "sickness");

      Map<String, Map<String, Object>> rqdata = new HashMap<>();
      rqdata.put(absence.getRequestTypeName(), data.getAllFields());

      flowRunnerService.instantiateFlow(fd1.getId(), rqdata);
      flowRunnerService.instantiateFlow(fd2.getId(), rqdata);
    }
}
