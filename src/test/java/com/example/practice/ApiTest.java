package com.example.practice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @LocalServerPort
    private int port;

    // 테스트 시작 전마다 실행
    // 포트를 찾을 수 없을 때 포트 등록 + Table Names 세팅
    // 데이터베이스 클린업
    @BeforeEach
    void setUp() throws Exception {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanUp.afterPropertiesSet();
        }
        databaseCleanUp.execute();
    }
}
