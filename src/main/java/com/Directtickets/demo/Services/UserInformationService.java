package com.Directtickets.demo.Services;

import com.Directtickets.demo.Repo.UserInformationRepository;
import com.Directtickets.demo.entity.UserInformation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInformationService {

    private final UserInformationRepository userInformationRepository;

    public UserInformationService(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public List<UserInformation> getAllUsers() {
        return userInformationRepository.findAll();
    }

    public UserInformation getUserById(String id) {
        return userInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public UserInformation getUserByEmail(String email) {
        return userInformationRepository.findByEmail(email);
    }

    public UserInformation createUser(UserInformation user) {
        return userInformationRepository.save(user);
    }

    public UserInformation updateUser(String id, UserInformation updatedUser) {
        UserInformation existingUser = userInformationRepository.findById(id)
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
