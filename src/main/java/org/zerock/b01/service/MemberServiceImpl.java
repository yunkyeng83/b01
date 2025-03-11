package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.dto.MemberJoinDTO;
import org.zerock.b01.repository.MemberRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    /**
     * 실제 회원가입 로직
     * @param memberJoinDTO
     */
    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
        String mid = memberJoinDTO.getMid();
        boolean exist = memberRepository.existsById(mid);

        if(exist) {
            throw new MidExistException();
        }

        // DB에 회원가입 정보페이지에서 받은 데이터를 jpa로 삽입하는 코드들

        Member member = modelMapper.map(memberJoinDTO, Member.class);  // DTO -> Entity
        // 평문으로 받은 암호를 암호화하기
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
        // 일반회원으로 가입
        member.addRole(MemberRole.USER);

        log.info("===================");
        log.info("member:" + member);
        log.info("memberGetRoleSet: " + member.getRoleSet());

        memberRepository.save(member);
    }
}
