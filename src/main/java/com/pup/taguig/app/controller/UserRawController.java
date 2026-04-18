package com.pup.taguig.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pup.taguig.app.model.User;
import com.pup.taguig.app.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserRawController {

    @Autowired
    private UserRepository userRepository;

    static class UserRequest {
        private String firstName;
        private String lastName;
        private int age;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    // Create new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest request) {
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("First name is required.");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Last name is required.");
        }
        
        User user = new User(
            request.getFirstName().trim(),
            request.getLastName().trim(),
            request.getAge()
        );
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> ResponseEntity.ok((Object) user))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + id + " not found."));
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("First name is required.");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Last name is required.");
        }
        
        return userRepository.findById(id)
            .map(user -> {
                user.setFirstName(request.getFirstName().trim());
                user.setLastName(request.getLastName().trim());
                user.setAge(request.getAge());
                User updatedUser = userRepository.save(user);
                return ResponseEntity.ok((Object) updatedUser);
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + id + " not found."));
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User with ID " + id + " deleted successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User with ID " + id + " not found.");
    }
}
