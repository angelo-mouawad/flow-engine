package be.ucll.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import be.ucll.model.Approval;
import be.ucll.model.FlowDefinition;
import be.ucll.model.FlowInstance;
import be.ucll.model.Notification;
import be.ucll.model.Process;
import be.ucll.model.strategies.NotifyByPopUp;
import jakarta.annotation.PostConstruct;

@Component
@Profile("dev")
public class DbInitializer {

    private FlowInstanceRepository flowInstanceRepository;
    private FlowDefinitionRepository flowDefinitionRepository;
    private ProcessRepository processRepository;
    
    @Autowired
    public DbInitializer(
        FlowDefinitionRepository flowDefinitionRepository, 
        FlowInstanceRepository flowInstanceRepository,
        ProcessRepository processRepository)
    {
        this.flowInstanceRepository = flowInstanceRepository; 
        this.flowDefinitionRepository = flowDefinitionRepository;
        this.processRepository = processRepository;
    }

    @PostConstruct
    public void initialize(){

      flowDefinitionRepository.deleteAll();
      flowInstanceRepository.deleteAll();
      processRepository.deleteAll();

      List<Process> processes = new ArrayList<>(List.of(
            new Notification(new NotifyByPopUp()),
            new Notification(new NotifyByPopUp()),
            new Notification(new NotifyByPopUp()),
            new Approval(),
            new Approval()
            ));
      processRepository.saveAll(processes);

      List<FlowDefinition> fds = List.of(
          new FlowDefinition("Absence 1", "Absence registration with employee email notification", List.of(processes.get(0))), 
          new FlowDefinition("Absence 2", "Absence registration with no notification", List.of(processes.get(2))), 
          new FlowDefinition("Clock In Evening", "Clocking in for evening shifts", List.of(processes.get(1))), 
          new FlowDefinition("Clock In Late", "Clocked in late", List.of(processes.get(3))), 
          new FlowDefinition("Clock In Morning", "Clocking in for morning shifts", List.of(processes.get(0))) 
      );
      flowDefinitionRepository.saveAll(fds);

      List<FlowInstance> fis = List.of(
          new FlowInstance(fds.get(0), "something 1"),
          new FlowInstance(fds.get(1), "something 7"),
          new FlowInstance(fds.get(2), "something 4"),
          new FlowInstance(fds.get(3), "something 3"),
          new FlowInstance(fds.get(4), "something 2")
      );
      flowInstanceRepository.saveAll(fis);

    }
}
