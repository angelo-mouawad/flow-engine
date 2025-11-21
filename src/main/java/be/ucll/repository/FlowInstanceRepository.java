package be.ucll.repository;

import be.ucll.model.FlowInstance;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FlowInstanceRepository extends MongoRepository<FlowInstance, String> {
    public Optional<FlowInstance> findById(String id);
}
