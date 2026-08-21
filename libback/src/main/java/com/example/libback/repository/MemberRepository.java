package com.example.libback.repository;

import com.example.libback.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

    boolean existsByEmail(String email);

    long countByActiveTrue();
}
