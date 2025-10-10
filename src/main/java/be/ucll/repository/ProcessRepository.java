package be.ucll.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import be.ucll.model.Process;

public interface ProcessRepository extends MongoRepository<Process, String>{
    public Optional<Process> getById(String id);
}
