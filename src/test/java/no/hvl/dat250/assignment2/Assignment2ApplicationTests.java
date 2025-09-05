package no.hvl.dat250.assignment2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import no.hvl.dat250.assignment2.model.Poll;
import no.hvl.dat250.assignment2.model.User;
import no.hvl.dat250.assignment2.model.Vote;
import no.hvl.dat250.assignment2.model.VoteOption;;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Assignment2ApplicationTests {

	private RestClient restClient;
		
	@LocalServerPort
	int port;
	String baseUrl;

	@BeforeEach
	void setup() {
		restClient = RestClient.create();
		baseUrl = "http://localhost:" + port;
	}

    // User tests
	@Test
	void createUsers() {
		// Create first user
		User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        User created1 = restClient.post()
				.uri(baseUrl + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user1)
                .retrieve()
                .body(User.class);

		// Confirmation of user
        assertNotNull(created1.getId());
        assertEquals("user1", created1.getUsername());

		List<User> users1 = restClient.get()
			.uri(baseUrl + "/users")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
			.body(new ParameterizedTypeReference<List<User>>() {});

		// Confirm number of users
        assertEquals(1, users1.size());

		// Create second user
		User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        User created2 = restClient.post()
				.uri(baseUrl + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user2)
                .retrieve()
                .body(User.class);

		// Confirmation of user
        assertNotNull(created2.getId());
        assertEquals("user2", created2.getUsername());

		List<User> users2 = restClient.get()
			.uri(baseUrl + "/users")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
			.body(new ParameterizedTypeReference<List<User>>() {});

		// Confirm number of users
        assertEquals(2, users2.size());

		// Create poll
		Poll poll = new Poll();
		poll.setQuestion("Pineapple on pizza?");
		poll.setValidUntil(Instant.parse("2028-01-01T00:00:00Z"));
		poll.setUsername("user1");

		Poll createdPoll = restClient.post()
				.uri(baseUrl + "/polls")
				.contentType(MediaType.APPLICATION_JSON)
				.body(poll)
				.retrieve()
				.body(Poll.class);

		Long pollId = createdPoll.getId();
		assertNotNull(pollId);

		// Create first vote option
		VoteOption yes = new VoteOption();
		yes.setCaption("Yes");
		yes.setPresentationOrder(1);

		VoteOption createdYes = restClient.post()
				.uri(baseUrl + "/polls/{pollId}/options", pollId)
				.contentType(MediaType.APPLICATION_JSON)
				.body(yes)
				.retrieve()
				.body(VoteOption.class);

		// Confirmation of option
		assertNotNull(createdYes.getId());
		assertEquals("Yes", createdYes.getCaption());

		// Create second vote option
		VoteOption no = new VoteOption();
		no.setCaption("No");
		no.setPresentationOrder(2);

		VoteOption createdNo = restClient.post()
				.uri(baseUrl + "/polls/{pollId}/options", pollId)
				.contentType(MediaType.APPLICATION_JSON)
				.body(no)
				.retrieve()
				.body(VoteOption.class);

		// Confirmation of option
		assertNotNull(createdNo.getId());
		assertEquals("No", createdNo.getCaption());

		Vote vote = new Vote();
		vote.setUserId(created2.getId());
		vote.setVoteOptionId(createdYes.getId());

		Vote createdVote = restClient.post()
				.uri(baseUrl + "/polls/{pollId}/votes", pollId)
				.contentType(MediaType.APPLICATION_JSON)
				.body(vote)
				.retrieve()
				.body(Vote.class);

		// Confirm vote by correct user
		assertEquals(pollId, createdVote.getPollId());
		assertEquals(created2.getId(), createdVote.getUserId()); // fix

		// Change vote
		createdVote.setVoteOptionId(createdNo.getId());

		Vote updatedVote = restClient.put()
				.uri(baseUrl + "/polls/{pollId}/votes/{voteId}", pollId, createdVote.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.body(createdVote)
				.retrieve()
				.body(Vote.class);

		assertEquals(createdNo.getId(), updatedVote.getVoteOptionId());
		assertNotNull(updatedVote.getLastUpdatedAt());

		// Get all votes for poll and confirm
		List<Vote> votes = restClient.get()
				.uri(baseUrl + "/polls/{pollId}/votes", pollId)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(new ParameterizedTypeReference<List<Vote>>() {});

		assertEquals(1, votes.size());
		Vote retrievedVote = votes.get(0);
		assertEquals(updatedVote.getId(), retrievedVote.getId());
		assertEquals(createdNo.getId(), retrievedVote.getVoteOptionId());

		// Delete poll
		restClient.delete()
				.uri(baseUrl + "/polls/{pollId}", pollId)
				.retrieve()
				.toBodilessEntity();

		// Confirm no polls
		List<Poll> polls = restClient.get()
				.uri(baseUrl + "/polls")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(new ParameterizedTypeReference<List<Poll>>() {});

		assertEquals(0, polls.size());

		// Confirm no votes for poll
		List<Vote> votesAfterDeletion = restClient.get()
				.uri(baseUrl + "/polls/{pollId}/votes", pollId)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(new ParameterizedTypeReference<List<Vote>>() {});

		assertEquals(0, votesAfterDeletion.size());
	}

}
