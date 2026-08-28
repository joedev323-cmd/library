package com.example.libback.repository;

import com.example.libback.model.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository
        extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);


    // =========================================================
    // PAGINATED BOOK SEARCH
    // =========================================================

    @Query("""
        SELECT b
        FROM Book b
        WHERE
            :query IS NULL
            OR :query = ''
            OR LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY b.title ASC
        """)
    Page<Book> searchBooks(
            @Param("query") String query,
            Pageable pageable
    );
}
