package be.ucll.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ucll.model.User;
import be.ucll.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User userData) {
        User existingUser = userRepository.findByEmail(userData.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("User already exists.");
        }
        userRepository.save(userData);
        return userData;
    }
}
