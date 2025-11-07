package be.ucll.service;

import be.ucll.model.FlowInstance;
import be.ucll.repository.FlowInstanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowInstanceService {

    @Autowired
    private FlowInstanceRepository flowInstanceRepository;

    public List<FlowInstance> findAllFlowInstances() {
        return flowInstanceRepository.findAll();
    }
}
