package com.example.practice.service.member;

import com.example.practice.config.JwtInterceptor;
import com.example.practice.config.SessionConfig;
import com.example.practice.entity.member.Member;
import com.example.practice.repository.member.JpaMemberAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final JpaMemberAdapter jpaMemberAdapter;
    private final HttpSession session;
    private final JwtInterceptor jwtInterceptor;

    /**
     * 회원 가입
     **/
    @Transactional
    public Member join(Member member) {

        // 중복 회원 검증
        validateDupliName(member);

        // 검증 통과 시 회원 등록
        jpaMemberAdapter.save(member);

        return member;
    }

    /**
     * 로그인
     **/
    @SneakyThrows
    @Transactional
    public Optional<Member> login(Member member,
                                  HttpServletRequest httpRequest,
                                  HttpServletResponse httpResponse) {

        // 가입한 회원인지 DB 조회
        Optional<Member> findMember = validateJoinMember(member);

        // 세션 생성 - 회원 정보 및 만료 시간 세팅
        ObjectMapper objectMapper = new ObjectMapper();
        session.setAttribute(SessionConfig.LOGIN_NAME, findMember.get().getName());
        session.setAttribute(SessionConfig.MEMBER_INFO, objectMapper.writeValueAsString(findMember));
        session.setMaxInactiveInterval(SessionConfig.EXPIRED_TIME);

        // jwt 토큰 발행 및 쿠키 세팅
        jwtInterceptor.setJwtToken(httpRequest, httpResponse);

        return findMember;
    }

    /**
     * 회원 아이디로 개별 회원 조회
     * @param memberId
     * @return Optional<Member>
     */
    @Transactional(readOnly = true)
    public Optional<Member> findOne(Long memberId) {
        return jpaMemberAdapter.findById(memberId);
    }

    /**
     * 전체 회원 조회
     * @param
     * @return List<Member>
     */
    @Transactional(readOnly = true)
    public List<Member> findMembers() {
        return jpaMemberAdapter.findAll();
    }


    /********************************
     * Validation
     *******************************/

    /**
     * 중복 회원 검증
     * 동일한 이름 중복 가입 불가
     * @param member
     */
    private void validateDupliName(Member member) {
        jpaMemberAdapter.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다!");
                });
    }

    /**
     * 가입 회원 검증
     * 가입한 회원인지 체크
     *
     * @param member
     */
    private Optional<Member> validateJoinMember(Member member) {
        return jpaMemberAdapter.findByName(member.getName())
                .stream().findAny();
    }
}
