package be.ucll.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import be.ucll.model.Process;

public interface ProcessRepository extends MongoRepository<Process, String>{
    public Optional<Process> findById(String id);
    public List<Process> findAll();
}
