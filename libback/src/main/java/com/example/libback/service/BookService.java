package com.example.libback.service;

import com.example.libback.dto.BookSearchResultDto;
import com.example.libback.model.Accession;
import com.example.libback.model.Book;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.BookRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                        AccessionRepository accessionRepository) {
                this.bookRepository = bookRepository;
                this.accessionRepository = accessionRepository;
        }

        // =========================================================
        // CATALOGUE SEARCH
        // =========================================================

        @Transactional(readOnly = true)
        public List<BookSearchResultDto> searchCatalogWithCounts(
                        String query) {

                if (query == null || query.trim().isEmpty()) {
                        return new ArrayList<>();
                }

                return accessionRepository.searchCatalog(
                                query.trim());
        }

        // =========================================================
        // BOOK SEARCH
        // =========================================================
        @Transactional(readOnly = true)
        public Page<Book> searchBooks(
                        String query,
                        Pageable pageable) {

                String cleanQuery = query == null
                                ? ""
                                : query.trim();

                return bookRepository.searchBooks(
                                cleanQuery,
                                pageable);
        }

        // =========================================================
        // SAVE BOOK
        // =========================================================

        @Transactional
        public Book saveBook(Book book) {

                validateBook(book);

                book.setIsbn(
                                cleanIsbn(book.getIsbn()));

                return bookRepository.save(book);
        }

        // =========================================================
        // FIND BOOK
        // =========================================================

        @Transactional(readOnly = true)
        public Book getBookByIsbn(String isbn) {

                String cleanIsbn = cleanIsbn(isbn);

                return bookRepository.findByIsbn(cleanIsbn)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Book not found with ISBN: "
                                                                + cleanIsbn));
        }

        @Transactional(readOnly = true)
        public Book getBookById(Long bookId) {

                if (bookId == null) {
                        throw new IllegalArgumentException(
                                        "Book ID is required.");
                }

                return bookRepository.findById(bookId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Book not found with ID: "
                                                                + bookId));
        }

        // =========================================================
        // GET ACCESSIONS FOR BOOK
        // =========================================================
        //
        // PAGINATED VERSION
        //
        // The controller supplies the Pageable.
        //
        // Example:
        //
        // page = 0
        // size = 10
        //
        // returns copies 1 - 10
        //
        // =========================================================

        @Transactional(readOnly = true)
        public Page<Accession> getAccessionsForBook(
                        Long bookId,
                        Pageable pageable) {

                if (bookId == null) {
                        throw new IllegalArgumentException(
                                        "Book ID is required.");
                }

                if (pageable == null) {
                        throw new IllegalArgumentException(
                                        "Page information is required.");
                }

                return accessionRepository
                                .findByBookBookIdOrderByCopyNumberAsc(
                                                bookId,
                                                pageable);
        }

        // =========================================================
        // GENERATE PHYSICAL COPIES
        // =========================================================

        @Transactional
        public void generateBatchAccessions(
                        Long bookId,
                        String prefix,
                        int quantity,
                        String shelfLocation) {

                validateGenerationRequest(
                                bookId,
                                prefix,
                                quantity,
                                shelfLocation);

                Book book = bookRepository.findById(bookId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Book not found with ID: "
                                                                + bookId));

                String cleanPrefix = prefix.trim().toUpperCase();

                String cleanShelfLocation = shelfLocation.trim();

                Integer maxCopyNumber = accessionRepository
                                .findMaxCopyNumberByBookId(bookId);

                int nextCopyNumber = maxCopyNumber == null
                                ? 1
                                : maxCopyNumber + 1;

                List<Accession> accessions = new ArrayList<>(quantity);

                for (int i = 0; i < quantity; i++) {

                        String accessionId = buildAccessionId(
                                        cleanPrefix,
                                        nextCopyNumber);

                        Accession accession = new Accession();

                        accession.setAccessionId(
                                        accessionId);

                        accession.setBook(book);

                        accession.setCopyNumber(
                                        nextCopyNumber);

                        accession.setBarcode(
                                        "BAR-" + accessionId);

                        accession.setShelfLocation(
                                        cleanShelfLocation);

                        accession.setPurchaseDate(
                                        LocalDate.now());

                        accession.setReplacementCost(
                                        BigDecimal.valueOf(20.00));

                        accessions.add(accession);

                        nextCopyNumber++;
                }

                accessionRepository.saveAll(
                                accessions);
        }

        // =========================================================
        // BUILD ACCESSION ID
        // =========================================================

        private String buildAccessionId(
                        String prefix,
                        int copyNumber) {

                return prefix
                                + "-"
                                + String.format(
                                                "%04d",
                                                copyNumber);
        }

        // =========================================================
        // VALIDATION
        // =========================================================

        private void validateGenerationRequest(
                        Long bookId,
                        String prefix,
                        int quantity,
                        String shelfLocation) {

                if (bookId == null) {
                        throw new IllegalArgumentException(
                                        "Book ID is required.");
                }

                if (prefix == null ||
                                prefix.trim().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "Accession prefix cannot be blank.");
                }

                if (quantity <= 0) {

                        throw new IllegalArgumentException(
                                        "Quantity must be greater than zero.");
                }

                if (shelfLocation == null ||
                                shelfLocation.trim().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "Shelf location cannot be blank.");
                }
        }

        // =========================================================
        // BOOK VALIDATION
        // =========================================================

        private void validateBook(Book book) {

                if (book == null) {
                        throw new IllegalArgumentException(
                                        "Book is required.");
                }

                if (book.getTitle() == null ||
                                book.getTitle().trim().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "Book title cannot be blank.");
                }

                if (book.getIsbn() == null ||
                                book.getIsbn().trim().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "ISBN cannot be blank.");
                }

                if (book.getAuthor() == null ||
                                book.getAuthor().trim().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "Book author cannot be blank.");
                }

                if (book.getCategory() == null) {

                        throw new IllegalArgumentException(
                                        "Book category is required.");
                }
        }

        // =========================================================
        // ISBN CLEANING
        // =========================================================

        private String cleanIsbn(String isbn) {

                if (isbn == null) {
                        return null;
                }

                return isbn.replaceAll(
                                "[^a-zA-Z0-9]",
                                "");
        }

        // =========================================================
        // LEGACY / PUBLIC BOOK LOOKUP
        // =========================================================

        /*
         * Your public controller currently calls:
         *
         * bookService.findByIsbn(isbn)
         *
         * Previously this method was unimplemented.
         *
         * Delegate to the real lookup method instead.
         */
        @Transactional(readOnly = true)
        public Book findByIsbn(String isbn) {

                return getBookByIsbn(isbn);
        }

        @Transactional(readOnly = true)
        public long getAvailableCount(Long bookId) {

                if (bookId == null) {
                        return 0;
                }

                return accessionRepository.countByBookBookIdAndAvailabilityStatus(
                                bookId,
                                com.example.libback.model.enums.AvailabilityStatus.AVAILABLE);
        }

}
