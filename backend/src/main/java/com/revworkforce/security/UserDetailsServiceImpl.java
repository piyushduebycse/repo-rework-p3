package com.revworkforce.security;

import com.revworkforce.entity.Employee;
import com.revworkforce.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Can login with either Email or Employee ID
        Employee employee = employeeRepository.findByEmail(username)
                .orElseGet(() -> employeeRepository.findByEmployeeId(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier : " + username)));

        if (!employee.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }

        return UserPrincipal.create(employee);
    }
    
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
        return UserPrincipal.create(employee);
    }
}
