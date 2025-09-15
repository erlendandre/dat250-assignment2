package no.hvl.dat250.assignment2.controller;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import no.hvl.dat250.assignment2.model.Login;
import no.hvl.dat250.assignment2.model.User;
import no.hvl.dat250.assignment2.service.PollManager;

/**
 * REST-controller for User
 * Handles CRUD-operations via PollManager
 */
@CrossOrigin(origins = "*")
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
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // Sjekk om username eller email allerede finnes
        if (pollManager.userExists(user.getUsername(), user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User createdUser = pollManager.addUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getAllErrors()
            .stream()
            .map(err -> err.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Log in
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody Login loginRequest) {
        User foundUser = pollManager.getAllUsers().stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(loginRequest.getEmail()))
            .findFirst()
            .orElse(null);

        if (foundUser == null || !foundUser.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(foundUser);
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