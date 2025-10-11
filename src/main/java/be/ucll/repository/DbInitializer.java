package be.ucll.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DbInitializer {

    private FlowInstanceRepository flowInstanceRepository;
    private FlowDefinitionRepository flowDefinitionRepository;
    
    @Autowired
    public DbInitializer(FlowDefinitionRepository flowDefinitionRepository, FlowInstanceRepository flowInstanceRepository){
        this.flowInstanceRepository = flowInstanceRepository; 
        this.flowDefinitionRepository = flowDefinitionRepository;
    }

    @PostConstruct
    public void initialize(){
    }
}
