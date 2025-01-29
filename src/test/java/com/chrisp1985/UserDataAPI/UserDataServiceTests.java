package com.chrisp1985.UserDataAPI;

import com.chrisp1985.UserDataAPI.data.User;
import com.chrisp1985.UserDataAPI.data.UserR2dbcRepository;
import com.chrisp1985.UserDataAPI.service.UserDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebFluxTest(UserDataService.class)
public class UserDataServiceTests {

    @MockBean
    UserR2dbcRepository userR2DbcRepository;

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private UserDataService userDataService;

    @Test
    void shouldReturnValidUser() {
        var testUser1 = new User(1, "chris", 100);
        when(userR2DbcRepository.findById("1")).thenReturn(Mono.just(testUser1));

        StepVerifier.create(userDataService.getUserById("1"))
                .expectNextMatches(user -> user.getId().equals(1) && user.getName().equals("chris"))
                .verifyComplete();

        verify(userR2DbcRepository, times(1)).findById("1");

    }

    @Test
    void shouldReturnValidUsers() {
        var testUser1 = new User(1, "chris", 100);
        var testUser2 = new User(2, "bert", 400);
        when(userR2DbcRepository.findAll()).thenReturn(Flux.just(testUser1, testUser2));

        StepVerifier.create(userDataService.getAllUsers())
                .expectNext(testUser1)
                .expectNext(testUser2)
                .verifyComplete();

        verify(userR2DbcRepository, times(1)).findAll();

    }
}
