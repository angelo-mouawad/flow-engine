package be.ucll.repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import be.ucll.controller.dto.FlowData;
import be.ucll.model.*;
import be.ucll.model.enums.RequestTypeEnum;
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

      Request absence = new Request("Absence", RequestTypeEnum.ABSENCE_REQUEST, true, new String[]{"adam@glackit.be", "stef@gmail.com"}, 2);
      Request absence2 = new Request("Absence",  RequestTypeEnum.ABSENCE_REQUEST, false, new String[]{}, 0);
      Approval approval = new Approval("Approval", Set.of(0));
      Notification notification = new Notification("Notification", "POPUP_NOTIFICATION");

      Set<String> triggers = new HashSet<>(Set.of(
                  "adam@glackit.be",
                  "samip@glackit.be",
                  "annie@glackit.be",
                  "lais@glackit.be",
                  "mouad@glackit.be",
                  "angelo@glackit.be"
                  ));

      Set<String> seniors = new HashSet<>(Set.of(
                  "adam@glackit.be",
                  "samip@glackit.be",
                  "annie@glackit.be"
                  ));

      FlowDefinition fd1 = new FlowDefinition(
              "Absence Reporting", 
              "Receive absence requests for processsing", 
              triggers,
              List.of(absence, approval, notification));

      FlowDefinition fd2 = new FlowDefinition(
              "Absence Reporting alternate", 
              "Receive absence requests for processsing", 
              triggers,
              List.of(notification, absence2));

      List<FlowDefinition> fds = List.of(fd1, fd2);

      flowDefinitionRepository.saveAll(fds);

      /*
       *"ABSENCE_REQUEST": {
       *    "startDate": "2025-11-10",
       *    "endDate": "2025-11-19",
       *    "submittedBy": "lais@glakit.be",
       *    "reason": "sickness",
       *} 
       */
      Map<String, Object> data = new HashMap<>();
      data.put("startDate", "2025-11-10");
      data.put("endDate", "2025-11-19");
      data.put("submittedBy", "lais@glakit.be");
      data.put("reason", "sickness");

      Map<RequestTypeEnum, Map<String, Object>> rqdata = new HashMap<>();
      rqdata.put(absence.getRequestTypeName(), data);

      FlowData flowData = new FlowData("hamid senior absence approval", "lais@glackit.be",rqdata);
      FlowData flowData2 = new FlowData("hamid absence no approval",  "lais@glackit.be",rqdata);

      String url = "https://front-end-team30-wpp-team-30.apps.okd.ucll.cloud/";

      flowRunnerService.instantiateFlow(fd1.getId(), url, flowData);
      flowRunnerService.instantiateFlow(fd2.getId(), url, flowData2);
    }
}
