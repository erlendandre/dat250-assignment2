package no.hvl.dat250.assignment2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import no.hvl.dat250.assignment2.model.User;;

@SpringBootTest
class Assignment2ApplicationTests {

	private static RestClient restClient;

    @BeforeAll
    static void setupClient() {
        restClient = RestClient.create();
    }

    // User tests
	@Test
	@Order(1)
	void createFirstUser() {
		User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        User created = restClient.post()
                .uri("http://localhost:8080/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user1)
                .retrieve()
                .body(User.class);

        assertNotNull(created.getId());
        assertEquals("user1", created.getUsername());
	}

	@Test
	@Order(2)
	void listUsers1() {
        List<User> users = restClient.get()
            .uri("http://localhost:8080/users")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
			.body(new ParameterizedTypeReference<List<User>>() {});
            // .body(List.class);

        assertEquals(1, users.size());
    }

	@Test
	@Order(3)
	void createSecondUser() {
		User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user1@example.com");

        User created = restClient.post()
                .uri("http://localhost:8080/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user2)
                .retrieve()
                .body(User.class);

        assertNotNull(created.getId());
        assertEquals("user2", created.getUsername());
	}

	// @Test
	void listUsers2() {
        List<User> users = restClient.get()
            .uri("http://localhost:8080/users")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
			.body(new ParameterizedTypeReference<List<User>>() {});
            // .body(List.class);

        assertEquals(2, users.size());
    }

	// // Poll tests
	// @Test
	// @Order(5)
	// void contextLoads() {
	// }

	// @Test
	// @Order(6)
	// void contextLoads() {
	// }

	// // Vote option tests
	// @Test
	// @Order(7)
	// void contextLoads() {
	// }

	// @Test
	// @Order(8)
	// void contextLoads() {
	// }

	// // Vote tests
	// @Test
	// @Order(9)
	// void contextLoads() {
	// }

	// @Test
	// @Order(10)
	// void contextLoads() {
	// }

	// @Test
	// @Order(11)
	// void contextLoads() {
	// }

}
