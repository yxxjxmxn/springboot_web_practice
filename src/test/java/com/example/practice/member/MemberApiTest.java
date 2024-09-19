package com.example.practice.member;

import com.example.practice.ApiTest;
import com.example.practice.entity.member.Member;
import com.example.practice.request.member.AddMemberRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberApiTest extends ApiTest {

    @Test
    void 회원가입_테스트() {

        // 요청 생성
        final AddMemberRequest addMemberRequest = new AddMemberRequest("김철수", "password1234");

        // API 요청
        final var response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(addMemberRequest)
                .when()
                .post("/v1/members/join")
                .then()
                .log().all().extract();

        // 응답 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 로그인_테스트() {

        // 요청 생성
        final Member member = new Member("김철수", "password1234");

        // API 요청
        final var response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(member)
                .when()
                .post("/v1/members/login")
                .then()
                .log().all().extract();

        // 응답 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
