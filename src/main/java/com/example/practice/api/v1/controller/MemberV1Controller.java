package com.example.practice.api.v1.controller;

import com.example.practice.entity.member.Member;
import com.example.practice.request.member.AddMemberRequest;
import com.example.practice.request.member.GetMemberRequest;
import com.example.practice.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
public class MemberV1Controller {

    private final MemberService memberService;

    /**
     * 회원가입
     **/
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody @Valid final AddMemberRequest addMemberRequest,
                               BindingResult bindingResult) {

        // AddMemberRequest 객체 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 회원가입
        final Member member = new Member(addMemberRequest.getName(), addMemberRequest.getPassword());
        memberService.join(member);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 로그인
     **/
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid final GetMemberRequest getMemberRequest,
                                BindingResult bindingResult,
                                HttpServletRequest httpRequest,
                                HttpServletResponse httpResponse) {

        // GetMemberRequest 객체 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 로그인
        final Member member = new Member(getMemberRequest.getName(), getMemberRequest.getPassword());
        memberService.login(member, httpRequest, httpResponse);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
