package com.example.practice.repository.member;

import com.example.practice.entity.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    /** 회원 등록 **/
    Member save(Member member);

    /** 회원 아이디로 조회 **/
    Optional<Member> findById(Long id);

    /** 회원 이름으로 조회 **/
    Optional<Member> findByName(String name);

    /** 회원 목록 조회 **/
    List<Member> findAll();
}
