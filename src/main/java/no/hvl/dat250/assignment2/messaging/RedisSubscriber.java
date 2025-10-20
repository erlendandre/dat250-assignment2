package no.hvl.dat250.assignment2.messaging;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.hvl.dat250.assignment2.model.Poll;
import no.hvl.dat250.assignment2.model.Vote;
import no.hvl.dat250.assignment2.model.VoteOption;
import no.hvl.dat250.assignment2.service.PollManager;

@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final PollManager pollManager;

    @Autowired
    public RedisSubscriber(ObjectMapper objectMapper, @Lazy PollManager pollManager) {
        this.objectMapper = objectMapper;
        this.pollManager = pollManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            Map<String, Object> data = objectMapper.readValue(
                json, new TypeReference<Map<String, Object>>() {}
            );

            System.out.println("Received redis message: " + data);

            String type = (String) data.get("type");

            switch (type) {
                case "NEW_VOTE" -> handleNewVote(data);
                case "NEW_POLL" -> handleNewPoll(data);
                default -> System.out.println("Unknown event type: " + type);
            }

        } catch (Exception e) {
            System.err.println("Error processing redis message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleNewPoll(Map<String, Object> data) {
        System.out.println("Received NEW_POLL event for pollId = " + data.get("pollId"));
    }

    private void handleNewVote(Map<String, Object> data) {
        try {
            Long pollId = ((Number) data.get("pollId")).longValue();
            String optionCaption = (String) data.get("option");

            Poll poll = pollManager.getPoll(pollId);
            if (poll == null) {
                System.err.println("Poll not found for id: " + pollId);
                return;
            }

            VoteOption option = poll.getOptions().stream()
                    .filter(o -> o.getCaption().equals(optionCaption))
                    .findFirst()
                    .orElse(null);

            if (option == null) {
                System.err.println("Option not found: " + optionCaption);
                return;
            }

            Vote anonymousVote = new Vote();
            anonymousVote.setVotesOn(option);
            anonymousVote.setPoll(poll);

            pollManager.addVoteToPoll(poll, anonymousVote);

            System.out.println("Applied NEW_VOTE to poll " + pollId + " for option " + optionCaption);

        } catch (Exception e) {
            System.err.println("Failed to process NEW_VOTE event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}