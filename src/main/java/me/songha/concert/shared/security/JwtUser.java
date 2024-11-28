package me.songha.concert.shared.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
public class JwtUser {
    private final Long userId;
    private final String username;
    private final String role;
    private final Set<GrantedAuthority> authorities;

    public JwtUser(Long userId, String username, String role, Set<GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.authorities = authorities;
    }

}