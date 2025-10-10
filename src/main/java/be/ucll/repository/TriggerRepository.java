package be.ucll.repository;

import be.ucll.model.Trigger;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TriggerRepository extends MongoRepository<Trigger, String> {
    public Optional<Trigger> getById(String id);
}
