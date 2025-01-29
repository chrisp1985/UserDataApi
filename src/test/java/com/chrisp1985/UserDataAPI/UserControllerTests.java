package com.chrisp1985.UserDataAPI;

import com.chrisp1985.UserDataAPI.controller.UserController;
import com.chrisp1985.UserDataAPI.data.User;
import com.chrisp1985.UserDataAPI.data.UserR2dbcRepository;
import com.chrisp1985.UserDataAPI.service.UserDataService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(UserController.class)
class UserControllerTests {

	@MockBean
	UserR2dbcRepository userR2DbcRepository;

	@MockBean
	private UserDataService userDataService;

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	CacheManager cacheManager;

	private Optional<User> getCachedUser(String name) {
		return Optional.of(cacheManager.getCache("allUsers")).map(c -> c.get(name, User.class));
	}

	@Test
	void shouldReturnValidUser() throws Exception {
		var testUser1 = new User(1, "chris", 100);

		when(userDataService.getUserById(anyString())).thenReturn(Mono.just(testUser1));

		webTestClient.get().uri("/api/v1/user/r2dbc/1")
            	.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo("1")
				.jsonPath("$.name").isEqualTo("chris")
				.jsonPath("$.value").isEqualTo("100");
	}

	@Test
	void shouldReturnNoUser() {
		when(userDataService.getUserById(anyString())).thenReturn(Mono.empty());

		webTestClient.get().uri("/api/v1/user/r2dbc/1")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").doesNotExist();
	}

	@Test
	void shouldReturnAllUsers() {
		var testUser1 = new User(1, "chris", 100);
		var testUser2 = new User(2, "bert", 400);

		when(userDataService.getAllUsers()).thenReturn(Flux.just(testUser1, testUser2));

		webTestClient.get().uri("/api/v1/user/r2dbc")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$[0].id").isEqualTo("1")
				.jsonPath("$[0].name").isEqualTo("chris")
				.jsonPath("$[0].value").isEqualTo("100")
				.jsonPath("$[1].id").isEqualTo("2")
				.jsonPath("$[1].name").isEqualTo("bert")
				.jsonPath("$[1].value").isEqualTo("400");
	}

	@Test
	void shouldReturnNoUsers() {

		when(userDataService.getAllUsers()).thenReturn(Flux.empty());

		webTestClient.get().uri("/api/v1/user/r2dbc")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$[0].id").doesNotExist();
	}

	@Test
	void shouldReturnUser() {

		var testUser1 = new User(1, "chris", 100);
		Mono<User> monoUser = Mono.just(testUser1);
		when(userDataService.addUser(testUser1)).thenReturn(monoUser);

		webTestClient.post().uri("/api/v1/user/r2dbc")
				.body(monoUser, User.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.name").isEqualTo("chris");
	}

	@Test
	void userWithNullsIsBadRequest() {

		var testUser1 = new User(null, "chris", null);
		Mono<User> monoUser = Mono.just(testUser1);
		when(userDataService.addUser(testUser1)).thenReturn(Mono.empty());

		webTestClient.post().uri("/api/v1/user/r2dbc")
				.body(monoUser, User.class)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void userWithBlanksIsBadRequest() {

		var testUser1 = new User(1, "", 300);
		Mono<User> monoUser = Mono.just(testUser1);
		when(userDataService.addUser(testUser1)).thenReturn(Mono.empty());

		webTestClient.post().uri("/api/v1/user/r2dbc")
				.body(monoUser, User.class)
				.exchange()
				.expectStatus().isBadRequest();
	}
}
