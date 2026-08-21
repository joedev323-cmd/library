package com.example.libback.repository;

import com.example.libback.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerRepository extends JpaRepository<Borrower, String> {

    boolean existsByEmail(String email);

    long countByActiveTrue();
}
