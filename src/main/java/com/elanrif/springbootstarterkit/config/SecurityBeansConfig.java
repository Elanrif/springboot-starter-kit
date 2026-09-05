package com.elanrif.springbootstarterkit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

// Enables JPA Auditing for automatically populating
// auditing fields like createdAt and updatedAt in entities.
@Configuration
@EnableJpaAuditing
public class SecurityBeansConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    // TODO: for BasicAuth, Delete this after it's no longer needed
    // This is just for testing purposes
//    @Bean
//    public UserDetailsService userDetailsService(UserRepository userRepository) {
//        return email -> userRepository.findByEmail(email)
//                .map(user -> User
//                        .withUsername(user.getEmail())
//                        .password(user.getPassword())
//                        .roles(user.getRole().name())
//                        .build()
//                )
//                .orElseThrow(() ->
//                        new UsernameNotFoundException("User not found: " + email)
//                );
//    }
}
