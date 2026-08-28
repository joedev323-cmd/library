package com.example.libback;

import com.example.libback.model.User;
import com.example.libback.model.enums.UserRole;
import com.example.libback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LibbackApplication {

    public static void main(String[] args) {
        
        SpringApplication.run(LibbackApplication.class, args);
        System.out.println(">>> Application started successfully!");
    }

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                @Value("${app.admin.username:adman}") String adminUsername,
                                @Value("${app.admin.password:admin123}") String adminPassword) {
        return args -> {
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setName("System Admin");
                admin.setUsername(adminUsername);
                
                // Uses the global PasswordEncoder bean configured in your SecurityConfig
                admin.setPassword(passwordEncoder.encode(adminPassword));
                
                admin.setRole(UserRole.LIBRARIAN);

                userRepository.save(admin);
                System.out.println(">>> Initial Admin account created! Username: " + adminUsername);
            } else {
                System.out.println(">>> Admin account already exists. Skipping initialization.");
            }
        };
    }
}