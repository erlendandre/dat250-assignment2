# DAT250 – JPA

[Full repository](https://github.com/erlendandre/dat250-assignment2)

## Technical problems
Trouble with `build.gradle.kts` regarding correct versions of `jboss.logging`, but found the right versions.

## Link to your code for experiment 2. The included test case passes
[Test](https://github.com/erlendandre/dat250-assignment2/blob/main/src/test/java/no/hvl/dat250/jpa/polls/PollsTest.java)

## Inspection of database tables
I tried to inspect with h2-console, but found it dificult last minute. Realized that the H2 database is only created for testing purposes in memory, so it does not persist outside the test process. So trying to inspect via the H2 Console did not work.

Ended up inspecting what Hibernate actually does in the database during tests, by adding some properties in the test setup:
```bash
.property("hibernate.show_sql", "true")
.property("hibernate.format_sql", "true")
.createEntityManagerFactory();
```

## Pending issues
- Unable to delete created polls

- Unable to create private polls
  - When creating private polls, no users has access to any poll (private or public..)