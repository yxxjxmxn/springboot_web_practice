package com.example.practice.member;

import com.example.practice.entity.member.Member;
import com.example.practice.repository.member.JpaMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    JpaMemberRepository jpaMemberRepository;

    @Test
    public void JpaRepository_메소드_테스트() {

        // member 객체 생성
        Member member1 = new Member("user1", "password1234");
        Member member2 = new Member("user2", "password1234");
        Member member3 = new Member("user3", "password1234");

        // MemberRepository 메소드로 회원 등록
        jpaMemberRepository.save(member1);
        jpaMemberRepository.save(member2);
        jpaMemberRepository.save(member3);

        // 단건 조회로 검증
        Member findMember1 = jpaMemberRepository.findById(member1.getId()).get();
        Member findMember2 = jpaMemberRepository.findById(member2.getId()).get();
        Member findMember3 = jpaMemberRepository.findById(member3.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
        assertThat(findMember3).isEqualTo(member3);

        // 목록 조회로 검증
        List<Member> members = jpaMemberRepository.findAll();
        assertThat(members.size()).isEqualTo(3);

        // 개수 조회로 검증
        long saveCnt = jpaMemberRepository.count();
        assertThat(saveCnt).isEqualTo(3);

        // MemberRepository 메소드로 회원 삭제
        jpaMemberRepository.delete(member3);

        // 개수 조회로 검증
        long deleteCnt = jpaMemberRepository.count();
        assertThat(deleteCnt).isEqualTo(2);
    }
}
