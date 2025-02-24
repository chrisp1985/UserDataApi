package com.chrisp1985.UserDataAPI.controller;

import com.chrisp1985.UserDataAPI.data.User;
import com.chrisp1985.UserDataAPI.service.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserDataService userDataService;

    @Autowired
    public UserController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping(value = "/r2dbc/{entryId}")
    public ResponseEntity<Mono<User>> getUserJpa(@PathVariable String entryId) {
        return ResponseEntity.ok(userDataService.getUserById(entryId));
    }

    @GetMapping(value = "/r2dbc")
    public ResponseEntity<Flux<User>> getUsersR2jdbc() {
        log.info("User request made.");
        return ResponseEntity.ok(userDataService.getAllUsers());
    }

    @PostMapping(path = "/r2dbc",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<User>> addUser(@RequestBody @Valid User user) {
        return ResponseEntity.ok(userDataService.addUser(user));
    }
}
