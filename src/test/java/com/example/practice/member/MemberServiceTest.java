package com.example.practice.member;

import com.example.practice.entity.member.Member;
import com.example.practice.repository.member.MemberRepository;
import com.example.practice.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @SpringBootTest + @Transactional(Service) 사용 시 각 테스트가 완료되면 자동 롤백됨
 * -> 테스트 완료 시 JPA가 flush & commit을 수행하지 않아 DB에 반영되지 않음
 *
 * @Rollback(false) 어노테이션을 활용하여 테스트 완료 후 롤백되지 않도록 설정
 * -> 테스트 완료 시 JPA가 롤백하지 않고 flush & commit을 수행하여 DB에 반영됨
 * -> 주의) 실무에서는 테스트 후 DB에 변동사항이 없어야 하므로 롤백되도록 설정해야 함!
 **/
@SpringBootTest
@Rollback(false)
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입_테스트() {

        // given - member 객체 생성
        Member member = new Member("user", "password1234");

        // when - 회원 가입 및 가입한 회원 아이디 조회
        Member joinMember = memberService.join(member);

        // then - repository에 정상적으로 저장됐는지 확인
        Member findMember = memberService.findOne(joinMember.getId()).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    void 중복회원가입불가_테스트() {

        // given - member 객체 생성
        Member member1 = new Member("홍길동", "password1234");

        Member member2 = new Member("홍길동", "password1234");

        // when - 첫번째 회원 가입
        memberService.join(member1);

        // then - 중복 회원 가입 시 기대하는 예외로 처리되는지 확인
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다!");
    }

    @Test
    void 개별회원조회_테스트() {
    }

    @Test
    void 전체회원조회_테스트() {
    }
}