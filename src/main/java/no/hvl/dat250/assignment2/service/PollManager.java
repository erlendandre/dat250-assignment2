package no.hvl.dat250.assignment2.service;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    // Generates unique IDs for each entity when adding new objects
    private final AtomicLong usersIdSeq = new AtomicLong();
    private final AtomicLong pollsIdSeq = new AtomicLong();
    private final AtomicLong votesIdSeq = new AtomicLong();
    private final AtomicLong voteOptionsIdSeq = new AtomicLong();


    // User CRUD
    public Collection<User> getAllUsers() {return users.values();}

    public User getUser(Long id) {return users.get(id);}

    public boolean userExists(String username, String email) {
        return users.values().stream()
            .anyMatch(u -> u.getUsername().equalsIgnoreCase(username) || u.getEmail().equalsIgnoreCase(email));
    }
    
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

    public void removeUser(Long id) {users.remove(id);}

    public boolean inviteUserToPoll(Long pollId, Long userId) {
        Poll poll = polls.get(pollId);
        if (poll == null || poll.isPublic()) {return false;}
        if (!users.containsKey(userId)) {return false;}
        poll.getInvitedUserIds().add(userId);
        poll.setLastUpdatedAt(Instant.now());
        return true;
    }


    // Poll CRUD
    public Collection<Poll> getAllPolls() {return polls.values();}

    public Poll getPoll(Long id) {return polls.get(id);}

    public Collection<Poll> getPollsForUser(Long userId) {
        return polls.values().stream()
            .filter(p -> p.isPublic() || (p.getInvitedUserIds() != null && p.getInvitedUserIds().contains(userId)))
            .toList();
    }

    public Poll addPoll(Poll poll) {
        long id = pollsIdSeq.incrementAndGet();
        poll.setId(id);

        Instant now = Instant.now();
        poll.setPublishedAt(now);
        poll.setLastUpdatedAt(now);

        if (poll.getInvitedUserIds() == null) {
            poll.setInvitedUserIds(new HashSet<>());
        }

        if (!poll.isPublic() && poll.getUsername() != null) {
            User creator = users.values().stream()
                                .filter(u -> u.getUsername().equals(poll.getUsername()))
                                .findFirst()
                                .orElse(null);
            if (creator != null) {
                poll.getInvitedUserIds().add(creator.getId());
            }
        }

        if (poll.getOptions() != null) {
            for (VoteOption option : poll.getOptions()) {
                long optionId = voteOptionsIdSeq.incrementAndGet();
                option.setId(optionId);
                option.setPollId(poll.getId());
            }
        }

        polls.put(id, poll);
        return poll;
    }

    public Poll updatePoll(Long id, Poll updatedPoll) {
        updatedPoll.setId(id);
        updatedPoll.setLastUpdatedAt(Instant.now());
        polls.put(id, updatedPoll);
        return updatedPoll;
    }

    public boolean removePoll(Long pollId, Long userId) {
        Poll poll = polls.get(pollId);
        if (poll == null) return false;

        if (!poll.getUsername().equals(getUser(userId).getUsername())) {
            return false;
        }

        polls.remove(pollId);
        return true;
    }


    // Vote CRUD
    public Collection<Vote> getVotesForPoll(Long pollId) {
        Poll poll = polls.get(pollId);
        return (poll != null) ? poll.getVotes() : null;
    }

    public Vote addVoteToPoll(Long pollId, Vote vote) {
        Poll poll = polls.get(pollId);
        if (poll == null) {
            return null;
        }

        Instant now = Instant.now();

        if (poll.getPublishedAt() != null && now.isBefore(poll.getPublishedAt())) {
            return null;
        }

        if (poll.getValidUntil() != null && now.isAfter(poll.getValidUntil())) {
            return null;
        }

        if (!poll.isPublic()) {
            if (vote.getUserId() == null || !poll.getInvitedUserIds().contains(vote.getUserId())) {
                return null;
            }

            for (Vote existingVote : poll.getVotes()) {
                if (vote.getUserId().equals(existingVote.getUserId())) {
                    return null;
                }
            }
        }

        long id = votesIdSeq.incrementAndGet();
        vote.setId(id);
        vote.setPollId(pollId);
        vote.setPublishedAt(now);
        vote.setLastUpdatedAt(now);

        poll.getVotes().add(vote);
        return vote;
    }
	
    public void removeVoteFromPoll(Long pollId, Long voteId) {
        Poll poll = polls.get(pollId);
            if (poll != null) {
                List<Vote> votes = poll.getVotes();
                Vote toRemove = null;

                for (Vote v : votes) {
                    if (v.getId().equals(voteId)) {
                        toRemove = v;
                        break;
                    }
                }

                if (toRemove != null) {
                    votes.remove(toRemove);
                }
            }
    }
	
    public Vote updateVoteInPoll(Long pollId, Long voteId, Vote updatedVote) {
        Poll poll = polls.get(pollId);
        if (poll == null) return null;

        for (int i = 0; i < poll.getVotes().size(); i++) {
            Vote v = poll.getVotes().get(i);
            if (v.getId().equals(voteId)) {
                updatedVote.setId(voteId);
                updatedVote.setPollId(pollId);
                updatedVote.setPublishedAt(v.getPublishedAt());
                updatedVote.setLastUpdatedAt(Instant.now());

                poll.getVotes().set(i, updatedVote);
                return updatedVote;
            }
        }
        return null;
    }
	

    // VoteOption CRUD
    public Collection<VoteOption> getOptionsForPoll(Long pollId) {
        Poll poll = polls.get(pollId);
        return (poll != null) ? poll.getOptions() : null;
    }

    public VoteOption addOptionToPoll(Long pollId, VoteOption option) {
        Poll poll = polls.get(pollId);
        if (poll == null) return null;

        long id = voteOptionsIdSeq.incrementAndGet();
        option.setId(id);
        option.setPollId(pollId);

        poll.getOptions().add(option);
        return option;
    }

    public VoteOption updateOptionInPoll(Long pollId, Long optionId, VoteOption updatedOption) {
        Poll poll = polls.get(pollId);
        if (poll == null) return null;

        for (int i = 0; i < poll.getOptions().size(); i++) {
            VoteOption opt = poll.getOptions().get(i);
            if (opt.getId().equals(optionId)) {
                updatedOption.setId(optionId);
                updatedOption.setPollId(pollId);
                poll.getOptions().set(i, updatedOption);
                return updatedOption;
            }
        }
        return null;
    }

    public void removeOptionFromPoll(Long pollId, Long optionId) {
        Poll poll = polls.get(pollId);
        if (poll != null) {
            poll.getOptions().removeIf(opt -> opt.getId().equals(optionId));
        }
    }
}
