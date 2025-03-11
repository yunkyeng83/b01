package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.repository.MemberRepository;
import org.zerock.b01.security.dto.MemberSecurityDTO;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * SNS 로그인(카카오, 네이버, 구글 등)시에 항상 실행되는 메소드
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("userRequest...");
        log.info(userRequest);

        log.info("oauth2 user.......................................................");

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();

        log.info("clientName: " + clientName);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();   // OAuth2User의 각 속성의 정보 확인
        paramMap.forEach((k,v) -> {
            log.info("-------------------------------");
            log.info(k + " : " + v);
        });
        String nickname = null;
        switch(clientName) {
            case "kakao":
                nickname = getKakaoNickname(paramMap);
                break;
            case "naver":
                break;
        }
        log.info("=============================");
        log.info(nickname);
        log.info("=============================");

        return generateDTO(nickname, paramMap);
    }

    private MemberSecurityDTO generateDTO(String nickname, Map<String, Object> params) {
        Optional<Member> result = memberRepository.getWithRolesIsSocial(nickname);

        // 기존 회원정보의 테이블에 카카오 아이디와 동일한 ID가 없는 사용자일 경우
        if(result.isEmpty()) {
            // 회원테이블에 카카오 로그인 관련 정보 추가
            Member member = Member.builder()
                    .mid(nickname)
                    .mpw("-")
                    .email("")
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);

            memberRepository.save(member);  // 실제 DB에 저장

            // 로그인 이후에 사용할 MemberSecurityDTO 객체 생성하여 전달
            MemberSecurityDTO memberSecurityDTO
                    = new MemberSecurityDTO(
                    nickname,
                    "-",
                    "",
                    false,
                    true,
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            memberSecurityDTO.setProps(params);

            return memberSecurityDTO;
        } else {            // 기존의 가입 혹은 카카오 로그인이 되어서 데이터가 있는 경우
            Member member = result.get();

            // 로그인 이후에 사용할 MemberSecurityDTO 객체 생성하여 전달
            MemberSecurityDTO memberSecurityDTO
                    = new MemberSecurityDTO(
                    member.getMid(),
                    member.getMpw(),
                    member.getEmail(),
                    member.isDel(),
                    member.isSocial(),
                    member.getRoleSet().stream().map(memberRole
                            -> new SimpleGrantedAuthority("ROLE_" + memberRole.name())
                    ).collect(Collectors.toList())
            );

            return memberSecurityDTO;
        }
    }

    private String getKakaoNickname(Map<String, Object> paramMap) {
        log.info("kakao getKakaoNickname-------------------------");

        Object value = paramMap.get("kakao_account");
        log.info(value);

        LinkedHashMap accountMap = (LinkedHashMap)value;
        //String nickname = (String)accountMap.get("nickname");
        LinkedHashMap profileMap = (LinkedHashMap)accountMap.get("profile");
        String nickname = (String)profileMap.get("nickname");

        log.info("kakao nickname... " + nickname);

        return nickname;
    }
}
