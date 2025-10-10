package be.ucll.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.FlowDefinition;

public interface FlowDefinitionRepository extends MongoRepository<FlowDefinition, String>{

    public Optional<FlowDefinition> findById(String id);
}
