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

import no.hvl.dat250.assignment2.model.Poll;
import no.hvl.dat250.assignment2.service.PollManager;


/**
 * REST-controller for Poll
 * Handles CRUD-operations via PollManager
 */
@RestController
@RequestMapping("/polls")
public class PollController {

    private final PollManager pollManager;

    // Injecting PollManager via constructor
    public PollController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    // Read
    @GetMapping
    public Collection<Poll> getAllPolls() {
        return pollManager.getAllPolls();
    }

    @GetMapping("/{id}")
    public Poll getPoll(@PathVariable Long id) {
        return pollManager.getPoll(id);
    }

    // Create
    @PostMapping
    public Poll createPoll(@RequestBody Poll poll) {
        return pollManager.addPoll(poll);
    }

    // Update
    @PutMapping("/{id}")
    public Poll updatePoll(@PathVariable Long id, @RequestBody Poll updatedPoll) {
        return pollManager.updatePoll(id, updatedPoll);
    }

    // Delete
    @DeleteMapping("/{id}")
    public void deletePoll(@PathVariable Long id) {
        pollManager.removePoll(id);
    }
}