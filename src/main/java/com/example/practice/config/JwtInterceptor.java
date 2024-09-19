package com.example.practice.config;

import com.example.practice.repository.jwt.JwtRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.sql.Timestamp;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtInterceptor implements HandlerInterceptor {

    protected final HttpSession session;
    private final JwtRepository jwtRepository;
    private final JwtProvider jwtProvider;
    private final JwtDto jwtDto = new JwtDto();

    /**
     * JWT access/refresh 토큰 생성 및 쿠키 저장
     **/
    public void setJwtToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        // 토큰에 담을 회원 이름과 ip 정보 조회
        String name = (String) session.getAttribute(SessionConfig.LOGIN_NAME);
        String ip = getClientIP(httpRequest);

        // 토큰에 담을 정보 생성
        HashMap<String,Object> tokenMap = new HashMap<>();
        tokenMap.put("name", name);
        tokenMap.put("ip", ip);

        // 토큰 생성
        JwtDto jwt = jwtProvider.createToken(JwtConfig.secretKeyType, tokenMap);

        // 생성한 토큰으로 쿠키 굽기 - 쿠키 이름 지정하여 생성( key, value 개념)
        Cookie accessToken = new Cookie("accessToken", jwt.getAccessToken());
        Cookie refreshToken = new Cookie("refreshToken", jwt.getRefreshToken());

        // 쿠키 유효 기간
        accessToken.setMaxAge(jwtDto.getCookieTime());
        refreshToken.setMaxAge(jwtDto.getCookieTime());

        // 모든 경로에서 접근 가능
        accessToken.setPath("/");
        refreshToken.setPath("/");

        // 자바스크립트 XSS & CSRF 공격 방지
        accessToken.setSecure(true);
        refreshToken.setSecure(true);
        accessToken.setHttpOnly(true);
        refreshToken.setHttpOnly(true);

        // 세팅 완료한 쿠키 추가
        httpResponse.addCookie(accessToken);
        httpResponse.addCookie(refreshToken);
    }

    @Override
    public boolean preHandle(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Object handler) throws Exception {
        String requestURI = httpRequest.getRequestURI();
        String ip = getClientIP(httpRequest);

        //  preflight 통신 시 제외 처리
        if(httpRequest.getMethod().equals("OPTIONS")){
            return true;
        }

        try {
            if (isJwtCheckPath(requestURI)) { // jwt 토큰 검증
                JwtDto jwtDto = new JwtDto();
                Cookie[] cookies = httpRequest.getCookies(); // 모든 쿠키 가져오기
                Integer accessChk = 0;

                if (cookies != null) {
                    for (Cookie cKey : cookies) {
                        String name = cKey.getName(); // 쿠키 이름 가져오기
                        String value = cKey.getValue(); // 쿠키 값 가져오기
                        if (name.equals("refreshToken")) {
                            jwtDto.setRefreshToken(value);
                        }
                        if (name.equals("accessToken")) {
                            jwtDto.setAccessToken(value);
                        }
                    }
                }

                if (jwtDto.getAccessToken() != null) {
                    // access Token 검증 : 1이 리턴되면 정상, 2가 리턴되면 기간 만료 -> refresh 확인 절차
                    accessChk = jwtProvider.validateAccessToken(JwtConfig.secretKeyType, jwtDto);
                }

                // access token 유효하지 않을 경우 -> 신규 발급 요청
                if (accessChk == null || accessChk == 0) {
                    httpResponse.sendRedirect("/");
                    return false;
                }

                // access token 유효하나 기간이 만료된 경우 -> refresh token 확인
                if (accessChk == 2) {
                    Integer refreshChk = 0;
                    // Refresh Token 검증 : 1일 경우 에만 새 access token 가져옴
                    refreshChk = jwtProvider.validateRefreshToken(JwtConfig.secretKeyType, jwtDto);

                    if (refreshChk == 1) { // refresh가 유효하며 db와 일치하고 기간도 살아있을경우 access만 새로 쿠키에 등록

                        // ip 확인
                        String tokenIp = jwtProvider.getIpFromToken(JwtConfig.secretKeyType, jwtDto.getRefreshToken());
                        if (!ip.equals(tokenIp)) {
                            httpResponse.sendRedirect("/");
                            return false;
                        }

                        // db 확인절차 (db 데이터와 일치되면 재발급)
                        HashMap<String, String> tokenMap = (HashMap) jwtProvider.getAllClaims(JwtConfig.secretKeyType, jwtDto.getRefreshToken()).get("tokenMap");
                        String id = tokenMap.get("id");

                        // db 조회하여 기존 refreshToken 및 id가 동일한지 확인
                        tokenMap.put("refreshToken" , jwtDto.getRefreshToken());
                        tokenMap.put("id" , id);
                        Integer verifyRefreshToken = jwtRepository.verifyRefreshToken(tokenMap); // 일치:1, 불일치:0

                        // 불일치한 경우 블락 처리
                        if (verifyRefreshToken != 1) {
                            httpResponse.sendRedirect("/");
                            return false;
                        }

                        // RefreshToken 등록 시간
                        int tokenReg = jwtProvider.getRegDateFromToken(JwtConfig.secretKeyType, jwtDto.getRefreshToken());
                        Timestamp time = new Timestamp(System.currentTimeMillis());

                        // RefreshToken 재 발급 기준 시간 (1일)
                        long refreshTime = (time.getTime() / 1000L) - (60 * 60 * 24);

                        // Token 재발급
                        if (tokenReg < refreshTime) {
                            // RefreshToken 발급 시간이 하루 이상 지난 경우 모두 재발급
                            setJwtToken(httpRequest, httpResponse);

                        } else {
                            // accessToken 재발급
                            Claims refreshClaim = jwtProvider.getAllClaims(JwtConfig.secretKeyType, jwtDto.getRefreshToken());
                            String newAccessToken = jwtProvider.recreationAccessToken(JwtConfig.secretKeyType, refreshClaim.get("tokenMap"));
                            Cookie accessToken = new Cookie("accessToken", newAccessToken); // 쿠키 이름 지정하여 생성(key, value 개념)
                            accessToken.setMaxAge(JwtConfig.cookieTime); // 쿠키 유효 기간
                            accessToken.setPath("/"); // 모든 경로에서 접근 가능하도록 설정
                            accessToken.setSecure(true);
                            httpResponse.addCookie(accessToken);
                        }

                    }
                    else { // refresh token 유효하지 않을 경우 -> 신규 발급 요청
                        httpResponse.sendRedirect("/" );
                        return false;
                    }
                }
                // access, refresh 둘 다 살아있을 경우 패스
            }

        } catch (Exception e) {
            log.info("error {}", requestURI);
            throw e;

        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }

        return true;
    }

    // 해당 리스트에 포함된 경로 처리
    private static final String[] blacklist = {
            // api jwt 필터 적용 리스트
            "/v1/*"
    };

    /**
     * blackList에 등록된 경로일 경우 토큰 검증
     */
    private boolean isJwtCheckPath(String requestURI) {
        return PatternMatchUtils.simpleMatch(blacklist, requestURI);
    }

    /**
     * 클라이언트 IP 조회
     */
    private static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        log.info("> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            log.info("> Proxy-Client-IP : " + ip);
        }

        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.info(">  WL-Proxy-Client-IP : " + ip);
        }

        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info("> HTTP_CLIENT_IP : " + ip);
        }

        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("> HTTP_X_FORWARDED_FOR : " + ip);
        }

        if (ip == null) {
            ip = request.getRemoteAddr();
            log.info("> getRemoteAddr : " + ip);
        }

        log.info("> Result : IP Address : " + ip);

        return ip;
    }
}

