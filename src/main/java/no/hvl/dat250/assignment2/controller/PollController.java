package no.hvl.dat250.assignment2.controller;

import java.util.Collection;

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
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import no.hvl.dat250.assignment2.model.Poll;
import no.hvl.dat250.assignment2.model.Vote;
import no.hvl.dat250.assignment2.model.VoteOption;
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
        Poll poll = pollManager.getPoll(id);
        if (poll == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return poll;
    }

    @GetMapping("/{pollId}/votes")
    public Collection<Vote> getVotesForPoll(@PathVariable Long pollId) {
        Collection<Vote> votes = pollManager.getVotesForPoll(pollId);
        if (votes == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return votes;
    }

    @GetMapping("/{pollId}/options")
    public Collection<VoteOption> getOptionsForPoll(@PathVariable Long pollId) {
        Collection<VoteOption> options = pollManager.getOptionsForPoll(pollId);
        if (options == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return options;
    }

    // Create
    @PostMapping
    public Poll createPoll(@Valid @RequestBody Poll poll) {
        return pollManager.addPoll(poll);
    }

    @PostMapping("/{pollId}/votes")
    public Vote addVoteToPoll(@PathVariable Long pollId, @RequestBody Vote vote) {
        Vote created = pollManager.addVoteToPoll(pollId, vote);
        if (created == null) {
            // forskjell på poll ikke funnet (404) og ikke tillatt (403)
            if (pollManager.getPoll(pollId) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vote not allowed");
        }
        return created;
    }

    @PostMapping("/{pollId}/options")
    public VoteOption addOptionToPoll(@PathVariable Long pollId, @RequestBody VoteOption option) {
        VoteOption created = pollManager.addOptionToPoll(pollId, option);
        if (created == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return created;
    }

    @PostMapping("/{pollId}/invite/{userId}")
    public void inviteUser(@PathVariable Long pollId, @PathVariable Long userId) {
        boolean success = pollManager.inviteUserToPoll(pollId, userId);
        if (!success) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poll not found or is public");
        }
    }

    // Update
    @PutMapping("/{id}")
    public Poll updatePoll(@PathVariable Long id, @RequestBody Poll updatedPoll) {
        return pollManager.updatePoll(id, updatedPoll);
    }

    @PutMapping("/{pollId}/votes/{voteId}")
    public Vote updateVoteInPoll(@PathVariable Long pollId,
                                @PathVariable Long voteId,
                                @RequestBody Vote updatedVote) {
        Vote vote = pollManager.updateVoteInPoll(pollId, voteId, updatedVote);
        if (vote == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote or Poll not found");
        }
        return vote;
    }

    @PutMapping("/{pollId}/options/{optionId}")
    public VoteOption updateOptionInPoll(@PathVariable Long pollId,
                                        @PathVariable Long optionId,
                                        @RequestBody VoteOption updatedOption) {
        VoteOption option = pollManager.updateOptionInPoll(pollId, optionId, updatedOption);
        if (option == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Option or Poll not found");
        }
        return option;
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoll(@PathVariable Long id) {
        boolean removed = pollManager.removePoll(id);
        if (removed) return ResponseEntity.noContent().build();
        else return ResponseEntity.notFound().build();
    }
}