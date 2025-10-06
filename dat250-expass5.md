# DAT250 – Poll cache implementation

## Implementation

### Backend
`PollCacheRedis` class to manage caching in redis, which stores vote counts in a redis hash (HSET)
The class includes the following methods:
  - `getVotesForPoll(pollId)` to fetch votes from cache
  - `cacheVotes(pollId, optionVotes)` to populate cache
  - `incrementVote(pollId, optionCaption)` to increment counts and refresh TTL
  - `invalidatePoll(pollId)` to delete a poll from cache

Updated PollManager to:
  - check redis cache when retrieving votes
  - populate cache from in-memory data if cache is empty
  - update cache on vote creation or changes to vote options

### Frontend
Refactored vote display in Vote.svelte:
- removed local vote counting
- fetch vote counts for each poll from backend via `/polls/{pollId}/votes`

### Cache strategy
- frontend never calculates vote totals locally
- cache is invalidated if a poll is updated or a vote option is modified
- redis key TTL

## Challenges
At first, frontend calculated votes by counting selectedPoll.votes, which bypassed the cache. This was altered by implementing a DTO for vote counts and modifying frontend to fetch vote counts from backend, which uses redis cache.

Added System.out.println statements in `PollCacheRedis` to confirm writes and increments.

Verification in terminal using `redis-cli`:
- `keys *`
- `HGETALL poll:<pollId>` 
