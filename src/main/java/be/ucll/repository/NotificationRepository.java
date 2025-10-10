package be.ucll.repository;

import be.ucll.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    public Optional<Notification> findById(String id);
}