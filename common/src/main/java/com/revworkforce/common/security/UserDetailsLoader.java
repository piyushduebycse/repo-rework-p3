package com.revworkforce.common.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsLoader {
    UserDetails loadUserById(Long id);
}
