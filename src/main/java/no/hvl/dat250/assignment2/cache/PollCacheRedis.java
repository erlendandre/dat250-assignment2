package no.hvl.dat250.assignment2.cache;

import java.util.Collections;
import java.util.Map;

import redis.clients.jedis.UnifiedJedis;

public class PollCacheRedis {

    private final UnifiedJedis jedis;
    private final int ttlSeconds;

    public PollCacheRedis(UnifiedJedis jedis) {
        this(jedis, 300);
    }

    public PollCacheRedis(UnifiedJedis jedis, int ttlSeconds) {
        this.jedis = jedis;
        this.ttlSeconds = ttlSeconds;
    }

    // Get votes for a poll from cache
    public Map<String, String> getVotesForPoll(Long pollId) {
        String key = "poll:" + pollId;
        if (jedis.exists(key)) {
            return jedis.hgetAll(key);
        }
        return Collections.emptyMap();
    }

    // Add votes to cache with ttl
    public void cacheVotes(Long pollId, Map<String, String> optionVotes) {
        if (optionVotes == null || optionVotes.isEmpty()) return;

        String key = "poll:" + pollId;
        jedis.hset(key, optionVotes);
        jedis.expire(key, ttlSeconds);
    }

    // Increment votes for an option
    public void incrementVote(Long pollId, String optionCaption) {
        String key = "poll:" + pollId;
        if (jedis.exists(key)) {
            jedis.hincrBy(key, optionCaption, 1);
            jedis.expire(key, ttlSeconds);
        }
    }

    // Invalidate cache manually by poll-change/deletetion
    public void invalidatePoll(Long pollId) {
        String key = "poll:" + pollId;
        jedis.del(key);
    }

    public void close() {
        if (jedis != null) jedis.close();
    }
}