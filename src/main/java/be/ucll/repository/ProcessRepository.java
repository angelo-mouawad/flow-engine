package be.ucll.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import be.ucll.model.Process;

public interface ProcessRepository extends MongoRepository<Process, ObjectId>{
    public Optional<Process> getById(ObjectId id);
}
