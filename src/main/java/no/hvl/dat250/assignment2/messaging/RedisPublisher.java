package no.hvl.dat250.assignment2.messaging;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public RedisPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(Object message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        System.out.println("Topic: " + topic.getTopic());
        System.out.println("Published the following message to redis: " + message);
    }
}