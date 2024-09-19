package com.example.practice.config;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtDto {

    private String grantType;

    private String secretKeyType;

    private String accessToken;

    private String refreshToken;

    private Long refreshTokenId;

    private String key;

    private String keyEmail;

    // 유효 시간(1일)
    private long validTime = 24 * 60 * 60 * 1000L;

    // 유효 시간(1일)
    private long validTimeRefresh = 24 * 60 * 60 * 1000L;

    // JWT 토큰 쿠키 유효시간(1일)
    private int cookieTime = 60 * 60 * 24;
}
