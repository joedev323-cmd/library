package com.example.libback.repository;

import com.example.libback.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("""
        SELECT b
        FROM Book b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Book> searchBooks(
            @Param("query") String query
    );
    
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);
}
