package be.ucll.repository;

import be.ucll.model.FlowInstance;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface FlowInstanceRepository extends MongoRepository<FlowInstance, ObjectId> {
    public Optional<FlowInstance> getById(ObjectId id);
}
