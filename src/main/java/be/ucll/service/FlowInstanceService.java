package be.ucll.service;

import be.ucll.exception.ServiceException;
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

    public FlowInstance findFlowInstanceById(String id) {
        return flowInstanceRepository.findById(id)
                .orElseThrow(() -> new ServiceException("No id found"));
    }

    public void deleteFlowInstanceById(String id) {
        if (id == null) {
            throw new ServiceException("id must now be null");
        }

        if (!flowInstanceRepository.existsById(id)) {
            throw new ServiceException(id);
        }

        flowInstanceRepository.deleteById(id);
    }
}
