package org.zerock.b01.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.b01.security.dto.MemberSecurityDTO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("----------------------------------------------------------");
        log.info("CustomLoginSuccessHandler onAuthenticationSuccess ..........");
        log.info(authentication.getPrincipal());

        MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal();
        String plainPw = memberSecurityDTO.getMpw();

        if(memberSecurityDTO.isSocial() &&
                // passwordEncoder.matches메소드는 평문과 암호문이 같은지 비교하여 같으면 true 다르면 false
                ("-".equals(memberSecurityDTO.getMpw()) || passwordEncoder.matches("-", memberSecurityDTO.getMpw()))
        ) {         // 처음 SNS으로 로그인 한 경우
            log.info("Should Change Password");

            log.info("Redirect toMember Modify");
            response.sendRedirect("/member/modify");

            return;
        } else {    // 2번째 이후 SNS으로 로그인한 경우
            response.sendRedirect("/board/list");
        }
    }
}
