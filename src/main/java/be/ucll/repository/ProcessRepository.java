package be.ucll.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProcessRepository extends MongoRepository<Process, String>{
    public Optional<Process> getById(String id);
}
