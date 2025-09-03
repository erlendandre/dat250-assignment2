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

import no.hvl.dat250.assignment2.model.VoteOption;
import no.hvl.dat250.assignment2.service.PollManager;


/**
 * REST-controller for VoteOption
 * Handles CRUD-operations via PollManager
 */
@RestController
@RequestMapping("/voteoptions")
public class VoteOptionController {

    private final PollManager pollManager;

    // Injecting PollManager via constructor
    public VoteOptionController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    // Read
    @GetMapping
    public Collection<VoteOption> getAllVoteOptions() {
        return pollManager.getAllVoteOptions();
    }

    @GetMapping("/{id}")
    public VoteOption getVoteOption(@PathVariable Long id) {
        return pollManager.getVoteOption(id);
    }

    // Create
    @PostMapping
    public VoteOption createVoteOption(@RequestBody VoteOption voteOption) {
        return pollManager.addVoteOption(voteOption);
    }

    // Update
    @PutMapping("/{id}")
    public VoteOption updateVoteOption(@PathVariable Long id, @RequestBody VoteOption updatedVoteOption) {
        return pollManager.updateVoteOption(id, updatedVoteOption);
    }

    // Delete
    @DeleteMapping("/{id}")
    public void deleteVoteOption(@PathVariable Long id) {
        pollManager.removeVoteOption(id);
    }
}