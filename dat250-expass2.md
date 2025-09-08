# DAT250 Assignment 2 – REST API for Poll App

[GitHub repository](https://github.com/erlendandre/dat250-assignment2)


## 1. Implementation

The domain model is implemented as strictly as possible at this stage.


### User endpoints

 - `POST /users` - create new user
 - `GET /users` - retrieve all users
 - `GET /users/{id}` - retrieve user by ID
 - `PUT /users/{id}` - update existing user by ID
 - `DELETE /users/{id}` - delete user by ID


### Poll endpoints

 - `POST /polls` - create new poll
 - `GET /polls` - retrieve all polls
 - `GET /polls/{id}` - retrieve poll by ID
 - `PUT /polls/{id}` - update existing poll by ID
 - `POST /polls/{pollId}/invite/{userId}` - invite user to a poll by poll id and user id
 - `DELETE /polls/{id}` - delete poll by ID


### Vote Options endpoints
 - `POST /polls/{pollId}/options` - add new vote option to a poll
 - `GET /polls/{pollId}/options` - retrieve all vote options for a poll
 - `PUT /polls/{pollId}/options/{optionId}` - update existing vote option by poll id and vote option id


### Voting endpoints

 - `POST /polls/{pollId}/votes` - new vote in a poll
 - `GET /polls/{pollId}/votes` - retrieve all votes for a poll
 - `PUT /polls/{pollId}/votes/{voteId}` - update existing vote by poll id and vote id



## 2. Test Scenarios

The following scenario is fully implemented and tested:

1. Create a new user
2. Confirmation of user and number of users (1)
3. Create another user
4. Confirmation of user and number of users (2)
5. User 1 creates a new poll (and poll options) and invites user 2 to vote


First a "bad path" was tested:

6. User 1 tries to vote on the poll (only user 2 has been invited)
7. User 2 tries to vote on a non-existing poll
8. Try to invite user to a non-existing poll
9. Try to invite user to public poll


Then a "happy path" was tested:

10. User 2 votes
11. User 2 changes his vote
12. Confirm the most recent vote for User 2
13. Confirm deleted poll returns 404


Implemented in [`Assignment2ApplicationTests.java`](https://github.com/erlendandre/dat250-assignment2/blob/main/src/test/java/no/hvl/dat250/assignment2/Assignment2ApplicationTests.java) and pass successfully.



## 3. Technical Issues Encountered

- **Cross-test dependencies:** Tried to split the test scenarios into multiple test methods, which caused problems related to objects created in one test (e.g. a user or poll) that was not accessible in other tests. Solved by using one big test method that simulates the entire scenario from user creation to poll deletion and everything in between, even though it might not be the best practice. 
- **Edge cases:** Additional testing was added to cover attempts to vote on non-existing polls, invite users to public polls, etc.
- **Minimum two vote options:** Encountered 400 Bad Request error during testing when creating a poll without any vote options. This was caused by a constraint (@Size(min = 2)) on the options list in the Poll entity. To simplify testing and allow poll creation without predefining options, the constraint was removed. To be fixed later..

These issues were resolved or mitigated for the purpose of testing and do not prevent the API from functioning correctly. Using in-memory storage simplifies development for now, but is not persistent across application restarts. I assume a database will be implemented in later weeks.



## 4. Pending issues

- **Validation rules:** @Size(min = 2) constraint on poll options was temporarily removed to simplify testing, but is to be reintroduced to ensure polls always have at least two options.
- **API documentation:** Specification is missing and should be added later for clarity.
- **Test structure:** All test scenarios are currently in a single method due to cross-test dependencies. A cleaner test design with isolated test cases and might improve maintenance and further expansion.
- **Delete-endpoints:** Currently the delete operations for vote options and votes are implemented in the service (`PollManager.java`) and not via the controllers. Might be revisited depending on further functional requirements.
- **Front-end:** The application is currently missing a front-end solution, which I believe is the next logical step for usability testing. Works fine for now, with Java-based testing.



## 5. Conclusion


The assignment demonstrates a functional REST API for a poll application with support for users, polls, vote options, and voting. All core CRUD operations are implemented, and integration tests confirm both “happy path” and “bad path” scenarios. The in-memory storage solution simplifies development while still handling poll invitations and vote restrictions. Gradle is used for building and test automation, including a GitHub Actions workflow.

Some issues remain, but the project provides a foundation for further development, including improvements in validation of entity requirements, API documentation, and test design. 

The application is generally ready for further development, but is especially missing front-end experimentation, which I believe is the next logical step.