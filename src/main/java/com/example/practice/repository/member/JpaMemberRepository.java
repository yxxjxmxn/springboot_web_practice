package com.example.practice.repository.member;

import com.example.practice.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA가 제공하는 JpaRepository 공통 인터페이스를 상속 받으면 스프링 빈에 자동 등록됨
 * 별도의 구현체가 없어도 상속 받은 공통 메소드들을 통해 CRUD 가능
 **/
public interface JpaMemberRepository extends JpaRepository<Member, Long> {

    // 커스텀 메소드
    Optional<Member> findByName(String name);
}
