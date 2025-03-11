package org.zerock.b01.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.zerock.b01.domain.Member;

@Mapper
public interface MemberMapper {
    void insert(Member member);  // 회원 등록
}
