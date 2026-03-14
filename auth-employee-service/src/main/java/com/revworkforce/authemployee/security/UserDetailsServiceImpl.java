package com.revworkforce.authemployee.security;

import com.revworkforce.authemployee.entity.Employee;
import com.revworkforce.authemployee.repository.EmployeeRepository;
import com.revworkforce.common.security.UserDetailsLoader;
import com.revworkforce.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService, UserDetailsLoader {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(username)
                .orElseGet(() -> employeeRepository.findByEmployeeId(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier : " + username)));
        if (!employee.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }
        return createUserPrincipal(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
        return createUserPrincipal(employee);
    }

    private UserPrincipal createUserPrincipal(Employee employee) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + employee.getRole().name());
        return new UserPrincipal(
                employee.getId(),
                employee.getEmployeeId(),
                employee.getEmail(),
                employee.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
