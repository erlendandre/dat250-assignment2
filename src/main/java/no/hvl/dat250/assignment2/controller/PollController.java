package no.hvl.dat250.assignment2.controller;

import java.util.Collection;
import java.util.Map;
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


    // Create
    @PostMapping
    public Poll createPoll(@Valid @RequestBody Poll poll) {
        if (!poll.isPublic()) {
            for (User user : poll.getInvitedUsers()) {
                if (pollManager.getUser(user.getId()) == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID " + user.getId() + " does not exist");
                }
            }
        }

        if (poll.getCreatedByUser() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creator is required");
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
    public Vote addVoteToPoll(@PathVariable Long pollId, @RequestBody Vote vote) {
        Poll poll = pollManager.getPoll(pollId);
        Vote created = pollManager.addVoteToPoll(poll, vote);
        if (created == null) {
            if (pollManager.getPoll(pollId) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vote not allowed");
        }
        return created;
    }

    // @PostMapping("/{pollId}/votes")
    // public Vote addVoteToPoll(@PathVariable Long pollId, @RequestBody Map<String, Long> body) {
    //     Long userId = body.get("userId");
    //     Long voteOptionId = body.get("voteOptionId");

    //     Poll poll = pollManager.getPoll(pollId);
    //     Vote created = pollManager.addVoteToPoll(poll, userId, voteOptionId);

    //     if (created == null) {
    //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user or option");
    //     }

    //     return created;
    // }

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