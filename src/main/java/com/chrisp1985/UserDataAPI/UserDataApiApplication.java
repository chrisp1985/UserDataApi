package com.chrisp1985.UserDataAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UserDataApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserDataApiApplication.class, args);
	}

}
