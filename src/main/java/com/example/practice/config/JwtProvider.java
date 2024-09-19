package com.example.practice.config;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    // secretKey
    private String secretKey = JwtConfig.secretKey;

    // 객체 초기화, secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * JWT 토큰 생성
     **/
    public JwtDto createToken(String secretKeyType, HashMap<String, Object> tokenMap) {

        Claims claims = Jwts.claims().setSubject(secretKeyType); // JWT payload 에 저장되는 정보 단위
        claims.put("tokenMap", tokenMap); // 정보는 key / value 쌍으로 저장
        Date now = new Date();

        // Access Token
        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JwtConfig.validTime)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘, signature에 들어갈 secret값
                .compact(); // 위 설정대로 JWT 토큰 생성

        // Refresh Token
        String refreshToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JwtConfig.validTimeRefresh)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘, signature에 들어갈 secret값 세팅
                .compact(); // 위 설정대로 JWT 토큰 생성

        return JwtDto.builder().accessToken(accessToken).refreshToken(refreshToken).key(secretKeyType).build();
    }

    /**
     * access token 검증
     **/
    public Integer validateAccessToken(String secretKeyType, JwtDto accessTokenObj) {

        // access 객체에서 accessToken 추출
        String accessToken = accessTokenObj.getAccessToken();

        try {
            // 토큰 자체가 유효한지 검증
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);

            // 유효하며 만료 시간이 지나지 않았을 경우
            if (!claims.getBody().getExpiration().before(new Date())) {
                return 1;

                // 유효하나 기간이 만료된 경우
            } else if (claims.getBody().getExpiration().before(new Date())) {
                return 2;
            }

            // 기간 만료
        } catch (ExpiredJwtException e) {
            return 2;

            // signature 인증 실패
        } catch (SignatureException e) {
            return 0;

            // access 토큰 에러(검증실패)
        } catch (Exception e) {
            return 0;
        }

        // 검증 완료 - 도달할 경우 없음
        return 0;
    }

    /**
     * refresh token 검증
     **/
    public Integer validateRefreshToken(String secretKeyType, JwtDto refreshTokenObj) {

        // refresh 객체에서 refreshToken 추출
        String refreshToken = refreshTokenObj.getRefreshToken();

        try {
            // 토큰 자체가 유효한지 검증
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);

            // 유효하며 만료 시간이 지나지 않았을 경우
            if (!claims.getBody().getExpiration().before(new Date())) {
                return 1;
            }

            // 기간 만료
        } catch (ExpiredJwtException e) {
            return 2;

            // signature 인증 실패
        } catch (SignatureException e) {
            return 0;

            // refresh토큰 검증 오류(실패)
        } catch (Exception e) {
            // refresh 토큰이 만료되었을 경우, 토큰 완전 재발급이 필요합니다.
            return 0;
        }
        // 검증 완료 - 도달할 경우 없음
        return 0;
    }

    /**
     * access token 재발급 (refresh token이 유효한 경우 호출됨)
     **/
    public String recreationAccessToken(String secretKeyType, Object tokenMap) {

        Claims claims = Jwts.claims().setSubject(secretKeyType); // JWT payload 에 저장되는 정보 단위
        claims.put("tokenMap", tokenMap); // 정보는 key / value 쌍으로 저장
        Date now = new Date();

        // Access Token
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JwtConfig.validTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();
    }

    /**
     * refreshToken 재발급 (refresh token이 유효시간 갱신이 필요한 경우 호출됨)
     **/
    public String recreationRefreshToken(String secretKeyType, Object tokenMap) {

        Claims claims = Jwts.claims().setSubject(secretKeyType); // JWT payload 에 저장되는 정보단위
        claims.put("tokenMap", tokenMap); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();

        // Access Token
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JwtConfig.validTimeRefresh)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    /**
     * 토큰의 Claim 디코딩
     **/
    public Claims getAllClaims(String secretKeyType, String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Claim에서 ip 가져오기
     **/
    public String getIpFromToken(String secretKeyType, String token) {
        Map tokenMap = (Map) getAllClaims(secretKeyType, token).get("tokenMap");
        return (String) tokenMap.get("ip");
    }

    /**
     * Claim에서 토큰만료시간 가져오기
     **/
    public int getExpDateFromToken(String secretKeyType, String token) {
        return (int) getAllClaims(secretKeyType, token).get("exp");
    }

    /**
     * Claim에서 토큰등록시간 가져오기
     **/
    public int getRegDateFromToken(String secretKeyType, String token) {
        return (int) getAllClaims(secretKeyType, token).get("iat");
    }
}
