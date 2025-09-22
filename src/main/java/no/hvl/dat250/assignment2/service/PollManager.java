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

    public boolean inviteUserToPoll(Poll poll, User user) {

        if (poll == null || poll.isPublic() || user == null) {
            return false;
        }

        poll.getInvitedUsers().add(user);
        poll.setLastUpdatedAt(Instant.now());
        return true;
    }


    // Poll CRUD
    public Collection<Poll> getAllPolls() {return polls.values();}

    public Poll getPoll(Long id) {return polls.get(id);}

    public Collection<Poll> getPollsForUser(Long userId) {
        User user = users.get(userId);
        if (user == null) return List.of();

        return polls.values().stream()
            .filter(p -> p.isPublic() || (p.getInvitedUsers() != null && p.getInvitedUsers().contains(user)))
            .toList();
    }

    public Poll addPoll(Poll poll) {
        long id = pollsIdSeq.incrementAndGet();
        poll.setId(id);

        Instant now = Instant.now();
        poll.setPublishedAt(now);
        poll.setLastUpdatedAt(now);

        if (poll.getInvitedUsers() == null) {
            poll.setInvitedUsers(new HashSet<>());
        }
        
        if (!poll.isPublic() && poll.getCreatedByUser() != null) {
            User creator = poll.getCreatedByUser();
            if (creator != null) {
                if (poll.getInvitedUsers() == null) {
                    poll.setInvitedUsers(new HashSet<>());
                }
                poll.getInvitedUsers().add(creator);
            }
        }

        if (poll.getOptions() != null) {
            for (VoteOption option : poll.getOptions()) {
                long optionId = voteOptionsIdSeq.incrementAndGet();
                option.setId(optionId);
                option.setPoll(poll);
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

    public boolean removePoll(Poll poll, User user) {
        if (poll == null || user == null) return false;

        if (!poll.getCreatedByUser().equals(user)) {
            return false;
        }

        polls.remove(poll.getId());
        return true;
    }


    // Vote CRUD
    public Collection<Vote> getVotesForPoll(Poll poll) {
        if (poll == null) return List.of();
        return poll.getVotes();
    }

    public Vote addVoteToPoll(Poll poll, Vote vote) {
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
            User voteUser = vote.getUser();
            if (voteUser == null || !poll.getInvitedUsers().contains(voteUser)) {
                return null;
            }

            for (Vote existingVote : poll.getVotes()) {
                if (voteUser.equals(existingVote.getUser())) {
                    return null;
                }
            }
        }

        long id = votesIdSeq.incrementAndGet();
        vote.setId(id);
        vote.setPoll(poll);
        vote.setPublishedAt(now);
        vote.setLastUpdatedAt(now);

        poll.getVotes().add(vote);
        return vote;
    }

    public void removeVoteFromPoll(Poll poll, Long voteId) {
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
	
    public Vote updateVoteInPoll(Poll poll, Long voteId, Vote updatedVote) {
        if (poll == null) return null;

        for (int i = 0; i < poll.getVotes().size(); i++) {
            Vote v = poll.getVotes().get(i);
            if (v.getId().equals(voteId)) {
                updatedVote.setId(voteId);
                updatedVote.setPoll(poll);
                updatedVote.setPublishedAt(v.getPublishedAt());
                updatedVote.setLastUpdatedAt(Instant.now());

                poll.getVotes().set(i, updatedVote);
                return updatedVote;
            }
        }
        return null;
    }
	

    // VoteOption CRUD
    public Collection<VoteOption> getOptionsForPoll(Poll poll) {
        return (poll != null) ? poll.getOptions() : null;
    }

    public VoteOption addOptionToPoll(Poll poll, VoteOption option) {
        if (poll == null) return null;

        long id = voteOptionsIdSeq.incrementAndGet();
        option.setId(id);
        option.setPoll(poll);

        poll.getOptions().add(option);
        return option;
    }

    public VoteOption updateOptionInPoll(Poll poll, Long optionId, VoteOption updatedOption) {
        if (poll == null) return null;

        for (int i = 0; i < poll.getOptions().size(); i++) {
            VoteOption opt = poll.getOptions().get(i);
            if (opt.getId().equals(optionId)) {
                updatedOption.setId(optionId);
                updatedOption.setPoll(poll);
                poll.getOptions().set(i, updatedOption);
                return updatedOption;
            }
        }
        return null;
    }

    public void removeOptionFromPoll(Poll poll, Long optionId) {
        if (poll != null) {
            poll.getOptions().removeIf(opt -> opt.getId().equals(optionId));
        }
    }
}
