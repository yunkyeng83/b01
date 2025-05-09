package org.zerock.b01.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User implements OAuth2User {  // 상속받은 User가 UserDetails대신함
    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;
    private Map<String, Object> props;      // 소셜 로그인 정보

    public MemberSecurityDTO(
            String username,
            String password,
            String email,
            boolean del,
            boolean social,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, authorities); // User클래스의 필수 데이터

//        this.mid = mid;
//        this.mpw = mpw;
        this.mid = username;
        this.mpw = password;
        this.email = email;
        this.del = del;
        this.social = social;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.mid;    // == this.getMid()
    }
}
