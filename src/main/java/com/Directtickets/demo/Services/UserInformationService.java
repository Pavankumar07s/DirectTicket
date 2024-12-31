package com.Directtickets.demo.Services;

import com.Directtickets.demo.Repo.UserRepository;
import com.Directtickets.demo.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInformationService {

    private final UserRepository userInformationRepository;

    public UserInformationService(UserRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public List<User> getAllUsers() {
        return userInformationRepository.findAll();
    }

    public User getUserById(String id) {
        return userInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userInformationRepository.findByEmail(email);
    }

    public User createUser(User user) {
        return userInformationRepository.save(user);
    }

    public User updateUser(String id, User updatedUser) {
        User existingUser = userInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setImage(updatedUser.getImage());
        existingUser.setUpcomingTravelPlans(updatedUser.getUpcomingTravelPlans());
        existingUser.setPastTravelEvents(updatedUser.getPastTravelEvents());

        return userInformationRepository.save(existingUser);
    }

    public void deleteUser(String id) {
        userInformationRepository.deleteById(id);
    }
}
