package com.example.practice.request.member;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddMemberRequest {

    /************************************************
     * 회원가입 정책
     *
     * 1. 이름
     *      - 최소 4자 이상, 10자 이하
     *      - 알파벳 소문자(a~z), 숫자(0~9)로 구성
     *      - 중복 불가
     *
     * 2. 비밀번호
     *      - 최소 8자 이상, 15자 이하
     *      - 알파벳 대소문자(a~z, A~Z), 숫자(0~9)로 구성
     ************************************************/

    @NotNull(message = "이름은 필수입니다.")
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min=4, max=10, message = "최소 4자 이상, 10자 이하여야 합니다.")
    @Pattern(regexp = "^[a-z0-9]*$", message = "알파벳 소문자(a~z), 숫자(0~9)만 입력 가능합니다.")
    private String name;

    @NotNull(message = "비밀번호는 필수입니다.")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min=8, max=15, message = "최소 8자 이상, 15자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "알파벳 대소문자(a~z, A~Z), 숫자(0~9)만 입력 가능합니다.")
    private String password;

    // 빈 생성자
    public AddMemberRequest(String name, String password) {
    }
}
