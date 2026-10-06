package be.ucll.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.FlowDefinition;

public interface FlowDefinitionRepository extends MongoRepository<FlowDefinition, String> {
}
