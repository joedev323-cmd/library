package com.example.libback.repository;

import com.example.libback.model.Catergory;  
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Catergory, Long> {
    Optional<Catergory> findByName(String name);
    List<Catergory> findByParentIsNull();
}