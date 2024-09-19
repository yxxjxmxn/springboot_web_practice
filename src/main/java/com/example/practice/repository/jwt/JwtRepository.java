package com.example.practice.repository.jwt;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public interface JwtRepository {

    /**
     * RefreshToken 검증
     **/
    Integer verifyRefreshToken(HashMap<String, String> tokenMap);
}
