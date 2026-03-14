package com.revworkforce.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.revworkforce.entity.Employee;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

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

    public static UserPrincipal create(Employee employee) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + employee.getRole().name());
        return new UserPrincipal(
                employee.getId(),
                employee.getEmployeeId(),
                employee.getEmail(),
                employee.getPassword(),
                Collections.singletonList(authority)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username; // Email is used as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
