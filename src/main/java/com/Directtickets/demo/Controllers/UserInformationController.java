package com.Directtickets.demo.Controllers;

import com.Directtickets.demo.Services.UserInformationService;
import com.Directtickets.demo.entity.UserInformation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserInformationController {

    private final UserInformationService userInformationService;

    public UserInformationController(UserInformationService userInformationService) {
        this.userInformationService = userInformationService;
    }

    @GetMapping
    public ResponseEntity<List<UserInformation>> getAllUsers() {
        return ResponseEntity.ok(userInformationService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInformation> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userInformationService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserInformation> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userInformationService.getUserByEmail(email));
    }

    @PostMapping
    public ResponseEntity<UserInformation> createUser(@RequestBody UserInformation user) {
        return ResponseEntity.ok(userInformationService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserInformation> updateUser(
            @PathVariable String id, @RequestBody UserInformation updatedUser) {
        return ResponseEntity.ok(userInformationService.updateUser(id, updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userInformationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
