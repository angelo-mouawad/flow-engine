package be.ucll.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.FlowDefinition;

public interface FlowDefinitionRepository extends MongoRepository<FlowDefinition, ObjectId>{

    public Optional<FlowDefinition> findById(ObjectId id);
}
