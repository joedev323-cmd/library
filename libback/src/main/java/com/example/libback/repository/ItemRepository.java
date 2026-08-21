package com.example.libback.repository;

import com.example.libback.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> {

    @Query("""
        SELECT i
        FROM Item i
        WHERE LOWER(i.title)
        LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Item> searchByTitle(
            @Param("query") String query
    );
}
