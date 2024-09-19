package com.example.practice.entity.member;

import com.example.practice.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("아이디")
    private Long id;

    @Column(nullable = false)
    @Comment("이름")
    private String name;

    @Column(nullable = false)
    @Comment("비밀번호")
    private String password;

    // 생성자
    public Member(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
