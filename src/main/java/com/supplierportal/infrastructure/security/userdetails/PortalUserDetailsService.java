package com.supplierportal.infrastructure.security.userdetails;

import com.supplierportal.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.supplierportal.infrastructure.persistence.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PortalUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserJpaEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!"ACTIVE".equals(entity.getStatus())) {
            throw new DisabledException("User account is not active");
        }

        return new org.springframework.security.core.userdetails.User(
                entity.getUsername(),
                entity.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + entity.getRole()))
        );
    }
}
