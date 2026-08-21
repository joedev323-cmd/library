package com.example.libback.repository;

import com.example.libback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository uses the uppercase wrapper class 'Long' for the ID type
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}