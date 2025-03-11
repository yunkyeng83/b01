package org.zerock.b01.mapper;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.domain.Member;

import java.util.UUID;

@SpringBootTest
@Log4j2
public class MemberMapperTests {
    @Autowired
    private MemberMapper memberMapper;

    // spring boot & mybatis insert 기능 테스트
    @Test
    public void mybatisMemberInsert() {
        Member member = Member.builder()
                .mid("1")
                .mpw("1234")
                .mname("홍길동1")
                .uuid(UUID.randomUUID().toString())
                .build();

//        memberMapper.insert(member);
    }
}
