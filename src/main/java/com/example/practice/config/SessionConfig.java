package com.example.practice.config;

public abstract class SessionConfig {

    // 회원 기본 정보
    public static final String LOGIN_NAME = "name";
    public static final String MEMBER_INFO = "memberInfo";

    // 만료 시간 정보
    public static final Integer EXPIRED_TIME = 60 * 60 * 24; // 1일
}
