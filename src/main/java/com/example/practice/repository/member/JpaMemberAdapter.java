package com.example.practice.repository.member;

import com.example.practice.entity.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaMemberAdapter implements MemberRepository {

    private final JpaMemberRepository jpaMemberRepository;

    /** 회원 등록 **/
    @Override
    public Member save(Member member) {
        return jpaMemberRepository.save(member);
    }

    /** 회원 아이디로 조회 **/
    @Override
    public Optional<Member> findById(Long id) {
        return jpaMemberRepository.findById(id);
    }

    /** 회원 이름으로 조회 **/
    @Override
    public Optional<Member> findByName(String name) {
        return jpaMemberRepository.findByName(name);
    }

    /** 회원 목록 조회 **/
    @Override
    public List<Member> findAll() {
        return jpaMemberRepository.findAll();
    }
}
