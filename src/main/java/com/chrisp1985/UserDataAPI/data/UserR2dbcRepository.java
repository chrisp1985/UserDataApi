package com.chrisp1985.UserDataAPI.data;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserR2dbcRepository extends R2dbcRepository<User, String> {
}
