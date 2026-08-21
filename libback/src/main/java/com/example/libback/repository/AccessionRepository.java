package com.example.libback.repository;

import com.example.libback.model.Accession;
import com.example.libback.dto.BookSearchResultDto;
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

    // ---------------------------------------------------------
    // Lock accession during circulation operations
    // ---------------------------------------------------------

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a
        FROM Accession a
        WHERE a.accessionId = :id
        """)
    Optional<Accession> findByIdForUpdate(
            @Param("id") String id
    );


    // ---------------------------------------------------------
    // Item copy count
    // ---------------------------------------------------------

    long countByItemIsbn(
            String isbn
    );


    // ---------------------------------------------------------
    // Inventory statistics
    // ---------------------------------------------------------

    long countByAvailabilityStatus(
            AvailabilityStatus availabilityStatus
    );


    // ---------------------------------------------------------
    // Catalogue search
    // ---------------------------------------------------------

    @Query("""
        SELECT new com.example.libback.dto.BookSearchResultDto(
            a.item.title,
            a.item.author,
            a.item.isbn,
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
            LOWER(a.item.title)
                LIKE LOWER(CONCAT('%', :query, '%'))
            OR
            LOWER(a.item.author)
                LIKE LOWER(CONCAT('%', :query, '%'))
            OR
            LOWER(a.item.isbn)
                LIKE LOWER(CONCAT('%', :query, '%'))
        GROUP BY
            a.item.title,
            a.item.author,
            a.item.isbn,
            a.shelfLocation
        """)
    List<BookSearchResultDto> searchCatalog(
            @Param("query") String query
    );
}
