package com.example.libback.service;

import com.example.libback.model.User;
import com.example.libback.model.enums.UserRole;
import com.example.libback.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createLibrarian(
            String name,
            String username,
            String password
    ) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        User librarian = new User();

        librarian.setName(name);
        librarian.setUsername(username);
        librarian.setPassword(
                passwordEncoder.encode(password)
        );
        librarian.setRole(UserRole.LIBRARIAN);
        librarian.setActive(true);

        return userRepository.save(librarian);
    }
}
