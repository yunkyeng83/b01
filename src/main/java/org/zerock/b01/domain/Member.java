package org.zerock.b01.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {
    @Id
    private String mid;         // 내부 회원번호

    @Column(length = 300)
    private String mpw;         // 패스워드
    private String email;       // 이메일
    private boolean del;        // 탈퇴여부

    private boolean social;     // 소셜 로그인 자동 회원 가입 여부

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>(); // 등록 및 부여된 권한 리스트

    //@Column(length = 100, nullable = false)
    @Column(length = 100)
    private String mname;
    @Column(length = 50)
    private String uuid;

    public void addRole(MemberRole role) {
        this.roleSet.add(role);
    }

    public void changePassword(String mpw) {
        this.mpw = mpw;
    }
}
