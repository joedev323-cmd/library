package com.example.libback.controller;

import com.example.libback.model.Accession;
import com.example.libback.model.Book;
import com.example.libback.model.Member;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.repository.BookRepository;
import com.example.libback.repository.CategoryRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.repository.MemberRepository;
import com.example.libback.service.AuditLogService;
import com.example.libback.service.BookService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CatalogueController {

        private final BookService bookService;
        private final BookRepository bookRepository;
        private final MemberRepository memberRepository;
        private final AuditLogService auditLogService;
        private final CategoryRepository categoryRepository;
        private final LoanRepository loanRepository;

        public CatalogueController(
                        BookService bookService,
                        BookRepository bookRepository,
                        MemberRepository memberRepository,
                        AuditLogService auditLogService,
                        CategoryRepository categoryRepository,
                        LoanRepository loanRepository) {
                this.bookService = bookService;
                this.bookRepository = bookRepository;
                this.memberRepository = memberRepository;
                this.auditLogService = auditLogService;
                this.categoryRepository = categoryRepository;
                this.loanRepository = loanRepository;
        }

        // =========================================================
        // CATALOGUE
        // =========================================================

        @GetMapping("/Catalogue")
        public String getCatalogue(Model model) {

                model.addAttribute(
                                "books",
                                bookRepository.findAll());

                return "catalogue/index";
        }
        
        // =========================================================
        // BOOK DETAILS
        // =========================================================

        @GetMapping("/catalog/{isbn}")
        public String showBookDetails(
                        @PathVariable("isbn") String isbn,
                        Model model) {

                String cleanIsbn = cleanIsbn(isbn);

                Book book = bookRepository.findByIsbn(cleanIsbn)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Book not found with ISBN: "
                                                                + cleanIsbn));

                model.addAttribute(
                                "book",
                                book);

                model.addAttribute(
                                "accessions",
                                book.getAccessions());

                return "Catalogue/details";
        }

        // =========================================================
        // BATCH ACCESSION GENERATION
        // =========================================================

        @PostMapping("/catalog/{isbn}/accessions/batch")
        public String processBatchAccessions(
                        @PathVariable("isbn") String isbn,
                        @RequestParam("prefix") String prefix,
                        @RequestParam("quantity") int quantity,
                        @RequestParam("shelfLocation") String shelfLocation) {

                String cleanIsbn = isbn.replaceAll("[^a-zA-Z0-9]", "");

                Book book = bookRepository.findByIsbn(cleanIsbn)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Book not found with ISBN: " + cleanIsbn));

                bookService.generateBatchAccessions(
                                book.getBookId(),
                                prefix,
                                quantity,
                                shelfLocation);

                auditLogService.logAction(
                                "BATCH_ADD_ACCESSIONS",
                                "BOOK",
                                String.valueOf(book.getBookId()),
                                "Batch generated "
                                                + quantity
                                                + " physical copies with prefix: "
                                                + prefix);

                return "redirect:/catalog/" + cleanIsbn + "?success";
        }

        // =========================================================
        // REGISTER MEMBER
        // =========================================================

        @PostMapping("/admin/register-borrower")
        public String registerMember(
                        @RequestParam("studentId") String memberId,
                        @RequestParam("name") String name,
                        @RequestParam("email") String email,
                        @RequestParam("memberType") String type) {

                Member member = new Member();

                member.setMemberId(memberId);
                member.setName(name);
                member.setEmail(email);

                member.setMemberType(
                                com.example.libback.model.enums.MemberType
                                                .valueOf(type.toUpperCase()));

                memberRepository.save(member);

                auditLogService.logAction(
                                "REGISTER_MEMBER",
                                "MEMBER",
                                memberId,
                                "Registered member: " + name);

                return "redirect:/members?success";
        }

        // =========================================================
        // ADD BOOK FORM
        // =========================================================

        @GetMapping("/admin/catalog/add")
        public String showAddBookForm(
                        Model model) {

                model.addAttribute(
                                "book",
                                new Book());

                model.addAttribute(
                                "categories",
                                categoryRepository.findAll());

                return "books/add";
        }

        // =========================================================
        // ADD BOOK
        // =========================================================

        @PostMapping("/admin/catalog/add")
        public String processAddBook(
                        @ModelAttribute("book") Book newBook) {

                if (newBook.getIsbn() != null) {

                        newBook.setIsbn(
                                        cleanIsbn(newBook.getIsbn()));
                }

                bookService.saveBook(newBook);

                auditLogService.logAction(
                                "ADD_BOOK",
                                "BOOK",
                                newBook.getIsbn(),
                                "Added book: "
                                                + newBook.getTitle());

                return "redirect:/catalog/"
                                + newBook.getIsbn();
        }

        // =========================================================
        // EDIT BOOK FORM
        // =========================================================

        @GetMapping("/Catalogue/edit/{isbn}")
        public String showEditForm(
                        @PathVariable("isbn") String isbn,
                        Model model) {

                String cleanIsbn = cleanIsbn(isbn);

                Book book = bookRepository.findByIsbn(cleanIsbn)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No catalog record found for ISBN: "
                                                                + cleanIsbn));

                model.addAttribute(
                                "book",
                                book);

                model.addAttribute(
                                "categories",
                                categoryRepository.findAll());

                return "books/edit";
        }

        // =========================================================
        // UPDATE BOOK
        // =========================================================

        @PostMapping("/Catalogue/update/{isbn}")
        public String updateBook(
                        @PathVariable("isbn") String isbn,
                        @ModelAttribute("book") Book updatedBook) {

                String cleanIsbn = cleanIsbn(isbn);

                Book existingBook = bookRepository.findByIsbn(cleanIsbn)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No catalog record found for ISBN: "
                                                                + cleanIsbn));

                existingBook.setTitle(
                                updatedBook.getTitle());

                existingBook.setAuthor(
                                updatedBook.getAuthor());

                existingBook.setPublisher(
                                updatedBook.getPublisher());

                existingBook.setEdition(
                                updatedBook.getEdition());

                existingBook.setDescription(
                                updatedBook.getDescription());

                existingBook.setCategory(
                                updatedBook.getCategory());

                bookRepository.save(existingBook);

                auditLogService.logAction(
                                "UPDATE_BOOK",
                                "BOOK",
                                cleanIsbn,
                                "Modified metadata for book: "
                                                + existingBook.getTitle());

                return "redirect:/catalog/"
                                + cleanIsbn;
        }

        // =========================================================
        // CATALOGUE REGISTRY
        // =========================================================

        @GetMapping("/admin/catalog/registry")
        public String showCatalogRegistry(
                        Model model) {

                model.addAttribute(
                                "books",
                                bookRepository.findAll());

                return "catalogue/index";
        }

        // =========================================================
        // DELETE BOOK
        // =========================================================

        @PostMapping("/admin/catalog/delete/{isbn}")
        public String deleteCatalogItem(
                        @PathVariable("isbn") String isbn,
                        RedirectAttributes redirectAttributes) {

                String cleanIsbn = cleanIsbn(isbn);

                Book book = bookRepository.findByIsbn(cleanIsbn)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No catalog record found for ISBN: "
                                                                + cleanIsbn));

                // -----------------------------------------------------
                // Physical copies exist
                // -----------------------------------------------------

                if (book.getAccessions() != null
                                && !book.getAccessions().isEmpty()) {

                        boolean hasActiveLoans = book.getAccessions()
                                        .stream()
                                        .anyMatch(accession -> loanRepository
                                                        .findByAccessionAccessionIdAndStatus(
                                                                        accession.getAccessionId(),
                                                                        LoanStatus.ACTIVE)
                                                        .isPresent());

                        if (hasActiveLoans) {

                                redirectAttributes.addFlashAttribute(
                                                "errorMessage",
                                                "Cannot delete this book. One or more physical copies are currently checked out.");

                                return "redirect:/admin/catalog/registry";
                        }

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        "Cannot delete this book while physical copies exist. Delete the physical copies first.");

                        return "redirect:/admin/catalog/registry";
                }

                // -----------------------------------------------------
                // Safe to delete
                // -----------------------------------------------------

                bookRepository.delete(book);

                auditLogService.logAction(
                                "DELETE_BOOK",
                                "BOOK",
                                cleanIsbn,
                                "Deleted book from catalogue");

                redirectAttributes.addFlashAttribute(
                                "successMessage",
                                "Book successfully removed.");

                return "redirect:/admin/catalog/registry";
        }

        // =========================================================
        // DELETE PHYSICAL ACCESSION
        // =========================================================

        @PostMapping("/catalog/accession/delete/{accessionId}")
        public String deleteAccessionCopy(
                        @PathVariable("accessionId") String accessionId,
                        @RequestParam("isbn") String isbn,
                        RedirectAttributes redirectAttributes) {

                String cleanIsbn = cleanIsbn(isbn);

                // -----------------------------------------------------
                // Cannot delete an active loan
                // -----------------------------------------------------

                boolean isCurrentlyBorrowed = loanRepository
                                .findByAccessionAccessionIdAndStatus(
                                                accessionId,
                                                LoanStatus.ACTIVE)
                                .isPresent();

                if (isCurrentlyBorrowed) {

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        "Cannot delete copy "
                                                        + accessionId
                                                        + ". It is currently checked out.");

                        return "redirect:/catalog/"
                                        + cleanIsbn;
                }

                // -----------------------------------------------------
                // Find book
                // -----------------------------------------------------

                Book book = bookRepository.findByIsbn(cleanIsbn)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Book not found with ISBN: "
                                                                + cleanIsbn));

                // -----------------------------------------------------
                // Find accession
                // -----------------------------------------------------

                Accession accession = book.getAccessions()
                                .stream()
                                .filter(a -> a.getAccessionId()
                                                .equals(accessionId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Accession not found: "
                                                                + accessionId));

                // -----------------------------------------------------
                // Remove from parent collection
                // -----------------------------------------------------

                book.removeAccession(accession);

                bookRepository.save(book);

                // -----------------------------------------------------
                // Audit
                // -----------------------------------------------------

                auditLogService.logAction(
                                "DELETE_ACCESSION",
                                "ACCESSION",
                                accessionId,
                                "Removed physical copy from book "
                                                + cleanIsbn);

                redirectAttributes.addFlashAttribute(
                                "successMessage",
                                "Physical copy "
                                                + accessionId
                                                + " has been successfully removed.");

                return "redirect:/catalog/"
                                + cleanIsbn;
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
}
