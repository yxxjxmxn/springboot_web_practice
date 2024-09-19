package com.example.practice.member;

import com.example.practice.entity.member.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void 회원등록_테스트() {

        Member member1 = new Member("user1", "password1234");
        Member member2 = new Member("user2", "password1234");
        Member member3 = new Member("user3", "password1234");
        Member member4 = new Member("user4", "password1234");

        // 영속성 컨텍스트에 저장
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // DB에 저장
        em.flush();

        // 초기화
        em.clear();

        // 확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();
        
        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }
}