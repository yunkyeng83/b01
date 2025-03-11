package org.zerock.b01.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.b01.security.CustomUserDetailsService;
import org.zerock.b01.security.handler.Custom403Handler;
import org.zerock.b01.security.handler.CustomSocialLoginSuccessHandler;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 권한을 사용하기 위한 어노테이션(prePostEnabled는 @PreAuthorize 혹은 @PostAuthorize 어노테이션을 사용하기 위한 속성)
public class CustomSecurityConfig {
    private final DataSource dataSource;                                // MariaDB와 JAVA의 연결소스(고리)
    private final CustomUserDetailsService customUserDetailsService;    // Spring Security 로그인 관련 서비스

    @Bean
    public PasswordEncoder passwordEncoder() {  // 패스워드 암호화를 위한 spring bean
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("-----------------configure-----------------");

        http.formLogin()                            // 폼 로그인 인증 페이지 접근(/login)
                .loginPage("/member/login")             // 사용자 로그인 페이지 설정
                .defaultSuccessUrl("/")               // 기본 로그인 이후 이동할 페이지("/"이면 생략가능)
                .successHandler((request, response, auth) -> {
                    String refererUrl = (String)request.getSession().getAttribute("previousUrl");
                    if(Strings.isNotBlank(refererUrl)) {
                        request.getSession().removeAttribute("previousUrl");

                        response.sendRedirect(refererUrl);
                    } else {
                        response.sendRedirect("/board/list");
                    }
                })
        ;
        http.logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .logoutSuccessHandler((request, response, auth) -> {
                    String refererUrl = request.getHeader("Referer");  // 로그아웃 전 페이지 정보
                    if(Strings.isNotBlank(refererUrl)) {
                        request.getSession().setAttribute("previousUrl", refererUrl);
                    }
                    response.sendRedirect("/member/login");  // 로그아웃 후 로그인 페이지로 이동
                })
        ;
        http.csrf().disable();                      // CSRF(Cross-Site Request Forgery(크로스 사이트 간 요청 위조)) 토큰 비활성화
        // 인증 받을 url 매핑

        http.rememberMe()
                .key("12345678")                                // 쿠키값을 인코딩하기 위한 key
                .tokenRepository(persistentTokenRepository())   // token을 처리할 데이터 엑세스 관련
                .userDetailsService(customUserDetailsService)   // Spring Security에서 관리하는 서비스 주입
                .tokenValiditySeconds(60 * 60 * 24 * 30);       // 30일동안 보관(단위는 초)

        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());    // Custom403Handler 구현한 것을 spring security에 연결

        http.oauth2Login()
                .loginPage("/member/login")  // oauth2 sns로그인
//                .successHandler((request, response, auth) -> {
//                    response.sendRedirect("/board/list");
//                })
                .successHandler(authenticationSuccessHandler());
        ;

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);

        return repo;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("-----------------web configure-----------------");
        return (web) -> web.ignoring().requestMatchers(
                PathRequest.toStaticResources().atCommonLocations()     // resources/static폴더의 모든 파일들은 인증에서 제외
        );
    }
}
