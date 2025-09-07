# DAT250 Assignment 2 – REST API for Poll App

**GitHub repository:** [https://github.com/erlendandre/dat250-assignment2](https://github.com/erlendandre/dat250-assignment2)

---

## 1. Implementation

### User Management

- **Endpoints:** `POST /users`, `GET /users`, `GET /users/{id}`, `PUT /users/{id}`, `DELETE /users/{id}`
- Users can be created and retrieved dynamically.
- Each user has a unique `id`, `username`, and `email`.

### Poll Management

- **Endpoints:** `POST /polls`, `GET /polls`, `GET /polls/{id}`, `PUT /polls/{id}`, `DELETE /polls/{id}`
- Each poll has a question, expiration date, and the username of the creator.

### Vote Options

- **Endpoints:** `POST /polls/{pollId}/options`, `GET /polls/{pollId}/options`, `PUT /polls/{pollId}/options/{optionId}`
- Multiple options can be added to a poll and ordered with `presentationOrder`.
- Vote options are automatically removed when the poll is deleted.

### Voting

- **Endpoints:** `POST /polls/{pollId}/votes`, `GET /polls/{pollId}/votes`, `PUT /polls/{pollId}/votes/{voteId}`
- Users can vote on a poll, change their vote, and retrieve votes for a poll.
- Votes are automatically removed when the poll is deleted.

---

## 2. Test Scenarios

Following a test-driven approach, the following scenario is fully implemented and tested:

1. Create a new user
2. Confirmation of user and number of users (1)
3. Create another user
4. Confirmation of user and number of users (2)
5. User 1 creates a new poll (and poll options) and invites user 2 to vote

First a "bad path" is tested:

7. User 1 tries to vote on the poll (only user 2 has been invited)
8. User 2 tries to vote on a non-existing poll
9. Try to invite user to a non-existing poll
10. Try to invite user to public poll

Then a "happy path" is tested:
11. User 2 votes
12. User 2 changes his vote
13. Confirm the most recent vote for User 2
14. Confirm deleted poll returns 404

Implemented in `Assignment2ApplicationTests.java` and pass successfully.

---

## 3. Technical Issues Encountered

- **Cross-test dependencies:** Tried to split the test scenarios into multiple test methods, which caused problems related to objects created in just one test (e.g. a user or poll) that was not accessible in the other tests. Solved by using one big test method that simulates the entire scenario from user creation to poll deletion, even though it might not be the best practice. Also added testing for "edge cases".


---

## 5. Notes

- All tests are located in `src/test/java/no/hvl/dat250/assignment2/Assignment2ApplicationTests.java`.  
- The application runs on a random port during tests (`@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`), and URLs are dynamically constructed.  
- Gradle is used for building and test automation, including a GitHub Actions workflow.
