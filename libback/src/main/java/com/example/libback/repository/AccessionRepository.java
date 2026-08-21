package com.example.libback.repository;

import com.example.libback.dto.BookSearchResultDto;
import com.example.libback.model.Accession;
import com.example.libback.model.enums.AvailabilityStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccessionRepository
        extends JpaRepository<Accession, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a
        FROM Accession a
        WHERE a.accessionId = :id
        """)
    Optional<Accession> findByIdForUpdate(
            @Param("id") String id
    );

    long countByBookBookId(Long bookId);

    long countByBookBookIdAndAvailabilityStatus(
            Long bookId,
            AvailabilityStatus status
    );

    long countByAvailabilityStatus(
            AvailabilityStatus status
    );

    @Query("""
        SELECT COALESCE(MAX(a.copyNumber), 0)
        FROM Accession a
        WHERE a.book.bookId = :bookId
        """)
    Integer findMaxCopyNumberByBookId(
            @Param("bookId") Long bookId
    );

    List<Accession> findByBookBookId(Long bookId);

    List<Accession> findByAvailabilityStatus(
            AvailabilityStatus status
    );

    // PUBLIC CATALOGUE SEARCH
    @Query("""
        SELECT new com.example.libback.dto.BookSearchResultDto(
            a.book.title,
            a.book.author,
            a.book.isbn,
            a.shelfLocation,
            SUM(
                CASE
                    WHEN a.availabilityStatus =
                    com.example.libback.model.enums.AvailabilityStatus.AVAILABLE
                    THEN 1
                    ELSE 0
                END
            ),
            COUNT(a)
        )
        FROM Accession a
        WHERE
            LOWER(a.book.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(a.book.author) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(a.book.isbn) LIKE LOWER(CONCAT('%', :query, '%'))
        GROUP BY
            a.book.title,
            a.book.author,
            a.book.isbn,
            a.shelfLocation
        """)
    List<BookSearchResultDto> searchCatalog(
            @Param("query") String query
    );
}
