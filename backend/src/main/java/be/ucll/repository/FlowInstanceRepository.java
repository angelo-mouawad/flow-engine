package be.ucll.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.FlowInstance;

public interface FlowInstanceRepository extends MongoRepository<FlowInstance, String> {
}
