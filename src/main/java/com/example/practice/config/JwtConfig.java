package com.example.practice.config;

public abstract class JwtConfig {

    // 토큰 기본 정보
    public static final String secretKeyType = "normal";                    // 토큰 사용 용도
    public static final String secretKey = "springwebpractice";             // 토큰 시크릿 키
    public static final long validTime = 24 * 60 * 60 * 1000L;              // access token 만료시간 : 1일
    public static final long validTimeRefresh = 24 * 60 * 60 * 7 * 1000L;   // refresh token 만료시간 : 7일
    public static final int cookieTime = 60 * 60 * 24 * 7;                  // cookie 만료시간 : 7일
}
