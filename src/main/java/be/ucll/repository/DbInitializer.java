package be.ucll.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import be.ucll.model.User;
import jakarta.annotation.PostConstruct;

@Component
public class DbInitializer {

    private UserRepository userRepository;

    @Autowired
    public DbInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initialize() {

        User user1 = new User("Tom Boon", 25, "tom.boon@ucll.be", "tom123");
        User user2 = new User("Loick Luypaert", 30, "loick.luypaert@ucll.be", "loick123");

        userRepository.save(user1);
        userRepository.save(user2);
    
    }
}
