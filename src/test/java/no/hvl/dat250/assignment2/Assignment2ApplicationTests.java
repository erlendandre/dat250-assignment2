package no.hvl.dat250.assignment2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import no.hvl.dat250.assignment2.model.Poll;
import no.hvl.dat250.assignment2.model.User;
import no.hvl.dat250.assignment2.model.Vote;
import no.hvl.dat250.assignment2.model.VoteOption;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Assignment2ApplicationTests {

	private final RestClient restClient = RestClient.create();
		
	@LocalServerPort
	int port;

	private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    // Happy path tests
	@Test
	void happyPathTests() {

		String baseUrl = getBaseUrl();

		// Create first user
		User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
		user1.setPassword("password1");

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
		user2.setPassword("password2");

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

		// Create private poll by user1, invite only user2
		Poll poll = new Poll();
		poll.setQuestion("Pineapple on pizza?");
		poll.setValidUntil(Instant.parse("2028-01-01T00:00:00Z"));
		poll.setUsername("user1");
		poll.setPublic(false);
		poll.setInvitedUserIds(Set.of(created2.getId()));

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


		// Bad path tests
		// User not invited tries to vote
		Vote invalidVote = new Vote();
		invalidVote.setUserId(created1.getId());
		invalidVote.setVoteOptionId(createdYes.getId());

		try {
			restClient.post()
					.uri(baseUrl + "/polls/{pollId}/votes", pollId)
					.contentType(MediaType.APPLICATION_JSON)
					.body(invalidVote)
					.retrieve()
					.body(Vote.class);
			assert false : "Expected FORBIDDEN for non-invited user";
		} catch (org.springframework.web.client.RestClientResponseException e) {
			int statusCode = e.getStatusCode().value();
			assertEquals(HttpStatus.FORBIDDEN.value(), statusCode);
		}

		// Voting on non-existing poll
		Vote voteNonExistingPoll = new Vote();
		voteNonExistingPoll.setUserId(created2.getId());
		voteNonExistingPoll.setVoteOptionId(createdYes.getId());
		Long nonExistingPollId = 9999L;

		try {
			restClient.post()
					.uri(baseUrl + "/polls/{pollId}/votes", nonExistingPollId)
					.contentType(MediaType.APPLICATION_JSON)
					.body(voteNonExistingPoll)
					.retrieve()
					.body(Vote.class);
			assert false : "Expected NOT_FOUND for non-existing poll";
		} catch (org.springframework.web.client.RestClientResponseException e) {
			int statusCode = e.getStatusCode().value();
			assertEquals(HttpStatus.NOT_FOUND.value(), statusCode);
		}

		// Try to invite user to non-existing poll
		try {
			restClient.post()
					.uri(baseUrl + "/polls/{pollId}/invite/{userId}", nonExistingPollId, created2.getId())
					.retrieve()
					.toBodilessEntity();
			assert false : "Expected BAD_REQUEST for inviting to non-existing poll";
		} catch (org.springframework.web.client.RestClientResponseException e) {
			int statusCode = e.getStatusCode().value();
			assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
		}

		// Try to invite user to public poll
		Poll publicPoll = new Poll();
		publicPoll.setQuestion("Public poll?");
		publicPoll.setValidUntil(Instant.parse("2028-01-01T00:00:00Z"));
		publicPoll.setUsername("user1");
		publicPoll.setPublic(true);

		Poll createdPublicPoll = restClient.post()
				.uri(baseUrl + "/polls")
				.contentType(MediaType.APPLICATION_JSON)
				.body(publicPoll)
				.retrieve()
				.body(Poll.class);

		try {
			restClient.post()
					.uri(baseUrl + "/polls/{pollId}/invite/{userId}", createdPublicPoll.getId(), created2.getId())
					.retrieve()
					.toBodilessEntity();
			assert false : "Expected BAD_REQUEST for inviting to public poll";
		} catch (org.springframework.web.client.RestClientResponseException e) {
			int statusCode = e.getStatusCode().value();
			assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
		}

		// Happy path tests
		// user2 votes
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
		assertEquals(created2.getId(), createdVote.getUserId());
		assertEquals(createdYes.getId(), createdVote.getVoteOptionId());

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

		// Confirm deleted poll returns 404
		try {
			restClient.get()
					.uri(baseUrl + "/polls/{pollId}", pollId)
					.retrieve()
					.body(Poll.class);
			assert false : "Expected NOT_FOUND for deleted poll";
		} catch (org.springframework.web.client.RestClientResponseException e) {
			assertEquals(HttpStatus.NOT_FOUND.value(), e.getStatusCode().value());
		}

	}
}
