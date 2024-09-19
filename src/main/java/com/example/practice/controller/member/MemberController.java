package com.example.practice.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    /** 회원가입 & 로그인 페이지 **/
    @GetMapping()
    public String hello() {
        return "hello";
    }
}
