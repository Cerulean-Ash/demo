package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        logger.info("User {} found.", user.getEmail());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(getRolesArray(Optional.ofNullable(user.getRoles()).orElse("")))
                .build();
    }

    private String[] getRolesArray(String roles) {
        if (roles.trim().isEmpty()) {
            logger.warn("User has no roles defined, returning empty array.");
            return new String[0];
        }
        return Arrays.stream(roles.split(","))
                .map(role -> {
                    String trimmedRole = role.trim().toUpperCase();
                    if (trimmedRole.startsWith("ROLE_")) {
                        return trimmedRole.substring(5);
                    }
                    return trimmedRole;
                })
                .toArray(String[]::new);
    }
}
