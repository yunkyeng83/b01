package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberRepositoryTests {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 테스트를 위한  100명의 회원 데이터 추가
     */
    @Test
    public void insertMembers() {
        IntStream.rangeClosed(1,100).forEach(i -> {
            Member member = Member.builder()
                    .mid("member" + i)
                    .mpw(passwordEncoder.encode("1111"))
                    .email("email" + i + "@aaa.bbb")
                    .build();
            member.addRole(MemberRole.USER);

            if(i >= 90){
                member.addRole(MemberRole.ADMIN);
            }
            memberRepository.save(member);

        });
    }

    /**
     * member100유저의 정보와 권한 리스트 조회
     */
    @Test
    public void testRead() {
        Optional<Member> result = memberRepository.getWithRoles("member100");
//        Member member = result.orElseThrow();

//        log.info(member);
//        log.info(member.getRoleSet());
//
//        member.getRoleSet().forEach(memberRole -> log.info(memberRole.name()));
    }

    @Test
    @Commit
    public void testUpdate() {
        String mid = "raymoñ";
        String mpw = passwordEncoder.encode("lxdk");

//        memberRepository.updatePassword(mpw, mid);
    }
}
