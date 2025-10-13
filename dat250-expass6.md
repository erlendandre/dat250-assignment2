# DAT250 – Message broker integration

[Full repository](https://github.com/erlendandre/dat250-assignment2)

The updated Poll Application now supports event sourcing via redis pub/sub.
Two primary event types are published:
- NEW_POLL when a new poll is created
- NEW_VOTE whenever a user votes

The redis-based message broker setup allows the application to publish events when polls or votes are added and allows the application to subscribe to events.

## Implementation

### RedisConfig
- connection factory using LettuceConnectionFactory connects to redis on localhost:6379
- RedisTemplate is configured to serialize keys and values as JSON
- ChannelTopic (poll.events) defines the redis topic all messages are sent through
- RedisMessageListenerContainer binds the subscriber (RedisSubscriber) to this topic

### RedisPublisher
- uses redisTemplate.convertAndSend() to send JSON-serialized messages to the "poll.events" topic
- prints debug output when publishing

### RedisSubscriber
- deserializes the JSON payload into a Map<String, Object>
- depending on the "type" field, it delegates to handleNewPoll() or handleNewVote()

### PollManager
- event publishing was integrated into addPoll() and addVoteToPoll()

## Testing

Functional tests via REST API
- created a poll using POST /polls
- voted on the poll using POST /polls/{id}/votes
- Observed the console output confirming published NEW_POLL and NEW_VOTE event and also received redis message on the subscriber side

For direct redis verification I used

```
redis-cli
SUBSCRIBE poll.events
```

which outputs the following when creating a poll and voting: 

```
1) "message"
2) "poll.events"
3) "{\"question\":\"Pineapple on pizza?\",\"pollId\":1,\"type\":\"NEW_POLL\"}"
1) "message"
2) "poll.events"
3) "{\"pollId\":1,\"type\":\"NEW_VOTE\",\"userId\":1,\"option\":\"Yes\"}"
```

## Technical problems encountered

### RabbitMQ integration problems
Initially tried to use RabbitMQ. I had correct exchange and queue bindings, but published messages never reached the queue. Debugging showed no activity, suggesting a mismatch between exchange configuration and queue routing.

### Transition to redis pub/sub
To avoid RabbitMQ delivery issues, I switched to redis pub/sub, which simplified configuration.

### Serialization and Type Handling
Redis pub/sub sends raw byte messages. The ObjectMapper and GenericJackson2JsonRedisSerializer ensures JSON serialization and deserialization.