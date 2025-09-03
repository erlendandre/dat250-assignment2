package no.hvl.dat250.assignment2.service;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import no.hvl.dat250.assignment2.model.Poll;
import no.hvl.dat250.assignment2.model.User;
import no.hvl.dat250.assignment2.model.Vote;
import no.hvl.dat250.assignment2.model.VoteOption;

/**
 * PollManager works as in-memory storage for all entities in PollApp
 * Offers CRUD-operations for User, Poll, VoteOption and Vote
 * 
 * @Component to enable injection in REST-controllers
 */
@Component
public class PollManager {
    
    // In-memory storage for CRUD
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Poll> polls = new HashMap<>();
    private final Map<Long, VoteOption> voteOptions = new HashMap<>();
    private final Map<Long, Vote> votes = new HashMap<>();

    // Generates unique IDs for each entity when adding new objects
    private final AtomicLong usersIdSeq = new AtomicLong();
    private final AtomicLong pollsIdSeq = new AtomicLong();
    private final AtomicLong voteOptionsIdSeq = new AtomicLong();
    private final AtomicLong votesIdSeq = new AtomicLong();

    // User CRUD
    public Collection<User> getAllUsers() { return users.values(); }

    public User getUser(Long id) { return users.get(id); }
    
    public User addUser(User user) {
        long id = usersIdSeq.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public User updateUser(Long id, User updatedUser) {
        updatedUser.setId(id);
        users.put(id, updatedUser);
        return updatedUser;
    }

    public void removeUser(Long id) { users.remove(id); }

    // Poll CRUD
    public Collection<Poll> getAllPolls() {return polls.values();}

    public Poll getPoll(Long id) {return polls.get(id);}
    
    public Poll addPoll(Poll poll) {
        long id = pollsIdSeq.incrementAndGet();
        poll.setId(id);
        Instant now = Instant.now();
        poll.setPublishedAt(now);
        poll.setLastUpdatedAt(now);        
        polls.put(id, poll);
        return poll;
    }

    public Poll updatePoll(Long id, Poll updatedPoll) {
        updatedPoll.setId(id);
        updatedPoll.setLastUpdatedAt(Instant.now());
        polls.put(id, updatedPoll);
        return updatedPoll;
    }

    public void removePoll(Long id) {polls.remove(id);}

    // VoteOption CRUD
    public Collection<VoteOption> getAllVoteOptions() {return voteOptions.values();}

    public VoteOption getVoteOption(Long id) {return voteOptions.get(id);}
    
    public VoteOption addVoteOption(VoteOption voteOption) {
        long id = voteOptionsIdSeq.incrementAndGet();
        voteOption.setId(id);
        voteOptions.put(id, voteOption);
        return voteOption;
    }

    public VoteOption updateVoteOption(Long id, VoteOption updatedVoteOption) {
        updatedVoteOption.setId(id);
        voteOptions.put(id, updatedVoteOption);
        return updatedVoteOption;
    }

    public void removeVoteOption(Long id) {voteOptions.remove(id);}
    
    // Vote CRUD
    public Collection<Vote> getAllVotes() {return votes.values();}

    public Vote getVote(Long id) {return votes.get(id);}
    
    public Vote addVote(Vote vote) {
        long id = votesIdSeq.incrementAndGet();
        vote.setId(id);
        Instant now = Instant.now();
        vote.setPublishedAt(now);
        vote.setLastUpdatedAt(now);        
        votes.put(id, vote);
        return vote;
    }

    // public Vote updateVote(Long id, Vote updatedVote) {
    //     updatedVote.setId(id);
    //     updatedVote.setLastUpdatedAt(Instant.now());
    //     votes.put(id, updatedVote);
    //     return updatedVote;
    // }

    public Vote updateVote(Long id, Vote updatedVote) {
        Vote existingVote = votes.get(id);
        if (existingVote != null) {
            updatedVote.setId(id);
            updatedVote.setPublishedAt(existingVote.getPublishedAt()); // Keeps existing publishedAt
            updatedVote.setLastUpdatedAt(Instant.now());
            votes.put(id, updatedVote);
        }
        return updatedVote;
    }

    public void removeVote(Long id) {votes.remove(id);}
}
