package com.chrisp1985.UserDataAPI.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CacheAspect {

    @Pointcut("@annotation(org.springframework.cache.annotation.CacheEvict)")
    public void onCacheEviction() {}

    @After("onCacheEviction()")
    public void logCacheEvictions(JoinPoint joinPoint) {
        System.out.println("Cache Eviction triggered in method " + joinPoint.toString());
    }
}
