package com.example.libback.service;

import com.example.libback.dto.BookSearchResultDto;
import com.example.libback.model.Accession;
import com.example.libback.model.Book;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.BookRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AccessionRepository accessionRepository;

    public BookService(
            BookRepository bookRepository,
            AccessionRepository accessionRepository
    ) {
        this.bookRepository = bookRepository;
        this.accessionRepository = accessionRepository;
    }

    // =========================================================
    // CATALOGUE SEARCH
    // =========================================================

    public List<BookSearchResultDto> searchCatalogWithCounts(String query) {

        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return accessionRepository.searchCatalog(query.trim());
    }

    // =========================================================
    // BOOK SEARCH
    // =========================================================

    public List<Book> searchBooks(String query) {

        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAll();
        }

        return bookRepository.findByTitleContainingIgnoreCase(
                query.trim()
        );
    }

    // =========================================================
    // SAVE BOOK
    // =========================================================

    @Transactional
    public Book saveBook(Book book) {

        if (book.getTitle() == null ||
                book.getTitle().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Book title cannot be blank."
            );
        }

        if (book.getIsbn() == null ||
                book.getIsbn().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "ISBN cannot be blank."
            );
        }

        if (book.getAuthor() == null ||
                book.getAuthor().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Book author cannot be blank."
            );
        }

        if (book.getCategory() == null) {

            throw new IllegalArgumentException(
                    "Book category is required."
            );
        }

        return bookRepository.save(book);
    }

    // =========================================================
    // GENERATE PHYSICAL COPIES
    // =========================================================

    @Transactional
    public void generateBatchAccessions(
            Long bookId,
            String prefix,
            int quantity,
            String shelfLocation
    ) {

        if (bookId == null) {
            throw new IllegalArgumentException(
                    "Book ID is required."
            );
        }

        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Accession prefix cannot be blank."
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book not found with ID: " + bookId
                        )
                );

        Integer maxCopy =
                accessionRepository.findMaxCopyNumberByBookId(bookId);

        int nextCopyNumber =
                maxCopy == null ? 1 : maxCopy + 1;

        for (int i = 0; i < quantity; i++) {

            Accession accession = new Accession();

            String accessionId =
                    prefix.trim()
                    + "-"
                    + String.format("%04d", nextCopyNumber);

            accession.setAccessionId(accessionId);
            accession.setBarcode("BAR-" + accessionId);
            accession.setCopyNumber(nextCopyNumber);
            accession.setBook(book);
            accession.setShelfLocation(shelfLocation);

            accession.setReplacementCost(
                    BigDecimal.valueOf(20.00)
            );

            accession.setPurchaseDate(
                    LocalDate.now()
            );

            accessionRepository.save(accession);

            nextCopyNumber++;
        }
    }
}
