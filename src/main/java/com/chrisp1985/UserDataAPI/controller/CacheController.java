package com.chrisp1985.UserDataAPI.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {

    @PostMapping(value = "")
    @CacheEvict("users")
    public void clearUserCacheManual() {}
}
