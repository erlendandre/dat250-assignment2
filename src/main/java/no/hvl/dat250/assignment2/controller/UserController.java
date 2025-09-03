package no.hvl.dat250.assignment2.controller;

import java.util.Collection;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat250.assignment2.model.User;
import no.hvl.dat250.assignment2.service.PollManager;

/**
 * REST-controller for User
 * Handles CRUD-operations via PollManager
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final PollManager pollManager;

    // Injecting PollManager via constructor
    public UserController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    // Read
    @GetMapping
    public Collection<User> getAllUsers() {
        return pollManager.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return pollManager.getUser(id);
    }

    // Create
    @PostMapping
    public User createUser(@RequestBody User user) {
        return pollManager.addUser(user);
    }

    // Update
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return pollManager.updateUser(id, updatedUser);
    }

    // Delete
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        pollManager.removeUser(id);
    }
}