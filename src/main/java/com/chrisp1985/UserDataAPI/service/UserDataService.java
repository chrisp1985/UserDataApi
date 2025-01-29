package com.chrisp1985.UserDataAPI.service;

import com.chrisp1985.UserDataAPI.data.User;
import com.chrisp1985.UserDataAPI.data.UserR2dbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserDataService {

    private final UserR2dbcRepository userR2DbcRepository;

    @Autowired
    public UserDataService(UserR2dbcRepository userR2DbcRepository) {
        this.userR2DbcRepository = userR2DbcRepository;
    }

    @Cacheable(value="users")
    public Flux<User> getAllUsers() {
        return userR2DbcRepository.findAll();
    }

    public Mono<User> getUserById(String id) {
        return userR2DbcRepository.findById(id);
    }

    @CacheEvict("users")
    public Mono<User> addUser(User user) {
        return userR2DbcRepository.save(user);
    }
}
