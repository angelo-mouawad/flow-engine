package be.ucll.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.FlowInstance;
import be.ucll.model.enums.FlowStatus;

public interface FlowInstanceRepository extends MongoRepository<FlowInstance, String>, FlowInstanceClaims {

    List<FlowInstance> findByFlowStatus(FlowStatus flowStatus);
}
