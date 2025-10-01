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
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import no.hvl.dat250.assignment2.dto.VoteRequest;
import no.hvl.dat250.assignment2.model.Poll;
import no.hvl.dat250.assignment2.model.User;
import no.hvl.dat250.assignment2.model.Vote;
import no.hvl.dat250.assignment2.model.VoteOption;
import no.hvl.dat250.assignment2.service.PollManager;


/**
 * REST-controller for Poll
 * Handles CRUD-operations via PollManager
 */
@CrossOrigin(origins = "*")
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
        Poll poll = pollManager.getPoll(pollId);
        if (poll == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return pollManager.getVotesForPoll(poll);
    }

    @GetMapping("/{pollId}/options")
    public Collection<VoteOption> getOptionsForPoll(@PathVariable Long pollId) {
        Poll poll = pollManager.getPoll(pollId);
        if (poll == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        } 
        return pollManager.getOptionsForPoll(poll);
    }

    @PostMapping
    public Poll createPoll(@Valid @RequestBody Poll poll) {
        if (poll.getCreatedByUser() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creator is required");
        }

        Long creatorId = poll.getCreatedByUser().getId();
        User creator = pollManager.getUser(creatorId);
        if (creator == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with ID " + creatorId + " not found");
        }
        poll.setCreatedByUser(creator);

        if (!poll.isPublic()) {
            for (User user : poll.getInvitedUsers()) {
                if (pollManager.getUser(user.getId()) == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID " + user.getId() + " does not exist");
                }
            }
        }

        return pollManager.addPoll(poll);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getAllErrors()
            .stream()
            .map(err -> err.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @PostMapping("/{pollId}/votes")
    public Vote addVoteToPoll(@PathVariable Long pollId, @RequestBody VoteRequest request) {
        Poll poll = pollManager.getPoll(pollId);
        if (poll == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");

        User user = pollManager.getUser(request.getUserId());
        if (user == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");

        VoteOption option = poll.getOptions().stream()
                .filter(o -> o.getId().equals(request.getVoteOptionId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Option not found"));

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setVotesOn(option);

        try {
            Vote created = pollManager.addVoteToPoll(poll, vote);
            return created;
        } catch (ResponseStatusException e) {
            throw e;
        }
    }

    @PostMapping("/{pollId}/options")
    public VoteOption addOptionToPoll(@PathVariable Long pollId, @RequestBody VoteOption option) {
        Poll poll = pollManager.getPoll(pollId);
        VoteOption created = pollManager.addOptionToPoll(poll, option);
        if (created == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return created;
    }

    @PostMapping("/{pollId}/invite/{userId}")
    public void inviteUser(@PathVariable Long pollId, @PathVariable Long userId) {
        Poll poll = pollManager.getPoll(pollId);
        User user = pollManager.getUser(userId);
        if (poll == null || user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poll or user not found");
        }
        boolean success = pollManager.inviteUserToPoll(poll, user);
        if (!success) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poll is public or invite failed");
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
        Poll poll = pollManager.getPoll(pollId);
        Vote vote = pollManager.updateVoteInPoll(poll, voteId, updatedVote);
        if (vote == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote or Poll not found");
        }
        return vote;
    }

    @PutMapping("/{pollId}/options/{optionId}")
    public VoteOption updateOptionInPoll(@PathVariable Long pollId,
                                        @PathVariable Long optionId,
                                        @RequestBody VoteOption updatedOption) {
        Poll poll = pollManager.getPoll(pollId);
        VoteOption option = pollManager.updateOptionInPoll(poll, optionId, updatedOption);
        if (option == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Option or Poll not found");
        }
        return option;
    }

    @DeleteMapping("/{id}/{userId}")
    public ResponseEntity<Void> deletePoll(@PathVariable Long id, @PathVariable Long userId) {
        Poll poll = pollManager.getPoll(id);
        User user = pollManager.getUser(userId);
        boolean removed = pollManager.removePoll(poll, user);
        if (removed) return ResponseEntity.noContent().build();
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}