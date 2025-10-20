package no.hvl.dat250.assignment2.service;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import no.hvl.dat250.assignment2.cache.PollCacheRedis;
import no.hvl.dat250.assignment2.dto.VoteCount;
import no.hvl.dat250.assignment2.messaging.RedisPublisher;
import no.hvl.dat250.assignment2.model.Poll;
import no.hvl.dat250.assignment2.model.User;
import no.hvl.dat250.assignment2.model.Vote;
import no.hvl.dat250.assignment2.model.VoteOption;
import redis.clients.jedis.UnifiedJedis;


@Component
public class PollManager {

    // In-memory storage
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Poll> polls = new HashMap<>();

    private final AtomicLong usersIdSeq = new AtomicLong();
    private final AtomicLong pollsIdSeq = new AtomicLong();
    private final AtomicLong votesIdSeq = new AtomicLong();
    private final AtomicLong voteOptionsIdSeq = new AtomicLong();

    private final PollCacheRedis pollCache;
    private final RedisPublisher redisPublisher;

    @Autowired
    public PollManager(RedisPublisher redisPublisher) {
        String host = System.getenv().getOrDefault("SPRING_REDIS_HOST",
                    System.getenv().getOrDefault("SPRING_DATA_REDIS_HOST", "localhost"));
        int port = Integer.parseInt(System.getenv().getOrDefault("SPRING_REDIS_PORT",
                    System.getenv().getOrDefault("SPRING_DATA_REDIS_PORT", "6379")));

        UnifiedJedis jedis = new UnifiedJedis("redis://" + host + ":" + port);

        this.pollCache = new PollCacheRedis(jedis);
        this.redisPublisher = redisPublisher;
    }

    // User CRUD
    public Collection<User> getAllUsers() { return users.values(); }
    public User getUser(Long id) { return users.get(id); }
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
    public void removeUser(Long id) { users.remove(id); }

    public boolean inviteUserToPoll(Poll poll, User user) {
        if (poll == null || poll.isPublic() || user == null) return false;
        poll.getInvitedUsers().add(user);
        poll.setLastUpdatedAt(Instant.now());
        pollCache.invalidatePoll(poll.getId());
        return true;
    }

    // Poll CRUD
    public Collection<Poll> getAllPolls() { return polls.values(); }
    public Poll getPoll(Long id) { return polls.get(id); }
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

        if (poll.getInvitedUsers() == null) poll.setInvitedUsers(new HashSet<>());
        if (!poll.isPublic() && poll.getCreatedByUser() != null)
            poll.getInvitedUsers().add(poll.getCreatedByUser());

        if (poll.getOptions() != null) {
            for (VoteOption option : poll.getOptions()) {
                long optionId = voteOptionsIdSeq.incrementAndGet();
                option.setId(optionId);
                option.setPoll(poll);
            }
        }

        polls.put(id, poll);
        pollCache.invalidatePoll(id);

        // Publiser event til Redis
        Map<String, Object> event = new HashMap<>();
        event.put("type", "NEW_POLL");
        event.put("pollId", poll.getId());
        event.put("question", poll.getQuestion());
        redisPublisher.publish(event);

        System.out.println("Published NEW_POLL event to redis: " + event);

        return poll;
    }

    public Poll updatePoll(Long id, Poll updatedPoll) {
        updatedPoll.setId(id);
        updatedPoll.setLastUpdatedAt(Instant.now());
        polls.put(id, updatedPoll);
        pollCache.invalidatePoll(id);
        return updatedPoll;
    }

    public boolean removePoll(Poll poll, User user) {
        if (poll == null || user == null) return false;
        if (!poll.getCreatedByUser().equals(user)) return false;
        polls.remove(poll.getId());
        pollCache.invalidatePoll(poll.getId());
        return true;
    }

    // Vote CRUD with redis
    public List<VoteCount> getVoteCountsForPoll(Poll poll) {
        if (poll == null) return List.of();

        Map<String, String> cached = pollCache.getVotesForPoll(poll.getId());
        Map<String, Integer> counts = new HashMap<>();

        if (!cached.isEmpty()) {
            cached.forEach((k,v) -> counts.put(k, Integer.parseInt(v)));
        } else {
            for (VoteOption option : poll.getOptions()) {
                counts.put(option.getCaption(), option.getVotes().size());
            }
            Map<String, String> cacheMap = counts.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
            pollCache.cacheVotes(poll.getId(), cacheMap);
        }

        return counts.entrySet().stream()
            .map(e -> new VoteCount(e.getKey(), e.getValue()))
            .toList();
    }

    public Vote addVoteToPoll(Poll poll, Vote vote) {
        Vote created = null;
        if (poll != null && vote != null && vote.getUser() != null && vote.getVotesOn() != null) {
            created = addVoteToPollInternal(poll, vote);
            pollCache.incrementVote(poll.getId(), vote.getVotesOn().getCaption());

            // Publiser event til Redis
            Map<String, Object> event = new HashMap<>();
            event.put("type", "NEW_VOTE");
            event.put("pollId", poll.getId());
            event.put("option", vote.getVotesOn().getCaption());
            event.put("userId", vote.getUser().getId());
            redisPublisher.publish(event);

            System.out.println("Published NEW_VOTE event to redis: " + event);
        }
        return created;
    }

    private Vote addVoteToPollInternal(Poll poll, Vote vote) {
        Instant now = Instant.now();
        if (poll.getPublishedAt() != null && now.isBefore(poll.getPublishedAt())) return null;
        if (poll.getValidUntil() != null && now.isAfter(poll.getValidUntil())) return null;

        Long voteUserId = vote.getUser().getId();

        if (!poll.isPublic()) {
            boolean invited = poll.getInvitedUsers().stream()
                .anyMatch(u -> u.getId().equals(voteUserId));
            if (!invited) return null;

            Vote existingVote = poll.getVotes().stream()
                .filter(v -> v.getUser() != null && v.getUser().getId().equals(voteUserId))
                .findFirst().orElse(null);

            if (existingVote != null) {
                if (existingVote.getVotesOn().getId().equals(vote.getVotesOn().getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already voted on this option");
                } else {
                    existingVote.getVotesOn().getVotes().remove(existingVote);
                    existingVote.setVotesOn(vote.getVotesOn());
                    existingVote.getVotesOn().getVotes().add(existingVote);
                    existingVote.setLastUpdatedAt(now);
                    return existingVote;
                }
            }
        }

        vote.setId(votesIdSeq.incrementAndGet());
        vote.setPoll(poll);
        vote.setPublishedAt(now);
        vote.setLastUpdatedAt(now);

        poll.getVotes().add(vote);
        vote.getVotesOn().getVotes().add(vote);

        return vote;
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

                pollCache.invalidatePoll(poll.getId());
                return updatedVote;
            }
        }
        return null;
    }

    // VoteOption CRUD with cache-invalidation
    public VoteOption addOptionToPoll(Poll poll, VoteOption option) {
        if (poll == null) return null;
        long id = voteOptionsIdSeq.incrementAndGet();
        option.setId(id);
        option.setPoll(poll);
        poll.getOptions().add(option);
        pollCache.invalidatePoll(poll.getId());
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
                pollCache.invalidatePoll(poll.getId());
                return updatedOption;
            }
        }
        return null;
    }

    public void removeOptionFromPoll(Poll poll, Long optionId) {
        if (poll != null) {
            poll.getOptions().removeIf(opt -> opt.getId().equals(optionId));
            pollCache.invalidatePoll(poll.getId());
        }
    }

    public void removeVoteFromPoll(Poll poll, Long voteId) {
        if (poll != null) {
            poll.getVotes().removeIf(v -> v.getId().equals(voteId));
            pollCache.invalidatePoll(poll.getId());
        }
    }

    public Collection<VoteOption> getOptionsForPoll(Poll poll) {
        return (poll != null) ? poll.getOptions() : null;
    }
}