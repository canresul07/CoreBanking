package com.example.Back.auth.config;

import com.example.Back.auth.entity.User;
import com.example.Back.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@nexbank.com");
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            admin.setRole("ROLE_ADMIN");
            admin.setFailedLoginAttempts(0);
            userRepository.save(admin);
            System.out.println("Default admin user created: admin / admin");
        }
    }
}
