package com.revworkforce.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Getter;

import java.util.Collection;

@Getter
public class UserPrincipal implements UserDetails {
    private Long id;
    private String employeeId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String employeeId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
