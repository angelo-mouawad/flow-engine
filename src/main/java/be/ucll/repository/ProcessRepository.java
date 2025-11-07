package be.ucll.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.Process;

public interface ProcessRepository extends MongoRepository<Process, ObjectId>{
    public Optional<Process> findById(ObjectId id);
}
