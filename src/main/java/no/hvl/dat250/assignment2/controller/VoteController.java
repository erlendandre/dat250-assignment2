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

import no.hvl.dat250.assignment2.model.Vote;
import no.hvl.dat250.assignment2.service.PollManager;


/**
 * REST-controller for Vote
 * Handles CRUD-operations via PollManager
 */
@RestController
@RequestMapping("/votes")
public class VoteController {

    private final PollManager pollManager;

    // Injecting PollManager via constructor
    public VoteController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    // Read
    @GetMapping
    public Collection<Vote> getAllVotes() {
        return pollManager.getAllVotes();
    }

    @GetMapping("/{id}")
    public Vote getVote(@PathVariable Long id) {
        return pollManager.getVote(id);
    }

    // Create
    @PostMapping
    public Vote createVote(@RequestBody Vote vote) {
        return pollManager.addVote(vote);
    }

    // Update
    @PutMapping("/{id}")
    public Vote updateVote(@PathVariable Long id, @RequestBody Vote updatedVote) {
        return pollManager.updateVote(id, updatedVote);
    }

    // Delete
    @DeleteMapping("/{id}")
    public void deleteVote(@PathVariable Long id) {
        pollManager.removeVote(id);
    }
}