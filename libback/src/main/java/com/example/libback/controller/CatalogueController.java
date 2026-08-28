package com.example.libback.controller;

import com.example.libback.model.Accession;
import com.example.libback.model.Book;
import com.example.libback.model.Member;
import com.example.libback.model.enums.AvailabilityStatus;
import com.example.libback.model.enums.ConditionStatus;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.model.enums.MemberType;
import com.example.libback.repository.AccessionRepository;
import com.example.libback.repository.BookRepository;
import com.example.libback.repository.CategoryRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.repository.MemberRepository;
import com.example.libback.service.AccessionService;
import com.example.libback.service.AuditLogService;
import com.example.libback.service.BookService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
public class CatalogueController {

        private final BookService bookService;
        private final BookRepository bookRepository;
        private final AccessionRepository accessionRepository;
        private final MemberRepository memberRepository;
        private final AuditLogService auditLogService;
        private final CategoryRepository categoryRepository;
        private final LoanRepository loanRepository;
        private final AccessionService accessionService;

        public CatalogueController(
                        BookService bookService,
                        BookRepository bookRepository,
                        AccessionRepository accessionRepository,
                        MemberRepository memberRepository,
                        AuditLogService auditLogService,
                        CategoryRepository categoryRepository,
                        LoanRepository loanRepository,
                        AccessionService accessionService) {

                this.bookService = bookService;
                this.bookRepository = bookRepository;
                this.accessionRepository = accessionRepository;
                this.memberRepository = memberRepository;
                this.auditLogService = auditLogService;
                this.categoryRepository = categoryRepository;
                this.loanRepository = loanRepository;
                this.accessionService = accessionService;
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
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {

                String cleanIsbn = cleanIsbn(isbn);

                Book book = bookService.getBookByIsbn(
                                cleanIsbn);

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(
                                                Sort.Direction.ASC,
                                                "copyNumber"));

                Page<Accession> accessions = bookService.getAccessionsForBook(
                                book.getBookId(),
                                pageable);

                model.addAttribute(
                                "book",
                                book);

                model.addAttribute(
                                "accessions",
                                accessions);

                return "catalogue/details";
        }

        // =========================================================
        // BATCH ACCESSION GENERATION
        //
        // FLOW:
        //
        // Book selected
        // ↓
        // Book ID obtained
        // ↓
        // Generate physical copies
        // ↓
        // Accessions created
        //
        // NO ACCESSION SELECTION
        // =========================================================

        @PostMapping("/catalog/{isbn}/accessions/batch")
        public String processBatchAccessions(
                        @PathVariable("isbn") String isbn,
                        @RequestParam("prefix") String prefix,
                        @RequestParam("quantity") int quantity,
                        @RequestParam("shelfLocation") String shelfLocation,
                        RedirectAttributes redirectAttributes) {

                String cleanIsbn = cleanIsbn(isbn);

                Book book = bookService.getBookByIsbn(
                                cleanIsbn);

                bookService.generateBatchAccessions(
                                book.getBookId(),
                                prefix,
                                quantity,
                                shelfLocation);

                auditLogService.logAction(
                                "BATCH_ADD_ACCESSIONS",
                                "BOOK",
                                String.valueOf(book.getBookId()),
                                "Generated "
                                                + quantity
                                                + " physical "
                                                + (quantity == 1 ? "copy" : "copies")
                                                + " for book: "
                                                + book.getTitle()
                                                + " using prefix: "
                                                + prefix);

                redirectAttributes.addFlashAttribute(
                                "successMessage",
                                quantity
                                                + " physical "
                                                + (quantity == 1 ? "copy" : "copies")
                                                + " successfully added.");

                /*
                 * IMPORTANT:
                 *
                 * Return to the SAME book page.
                 *
                 * Details URL:
                 * /catalog/{isbn}
                 */
                return "redirect:/catalog/" + cleanIsbn;
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
                                MemberType.valueOf(
                                                type.toUpperCase()));

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

                Book savedBook = bookService.saveBook(
                                newBook);

                auditLogService.logAction(
                                "ADD_BOOK",
                                "BOOK",
                                savedBook.getIsbn(),
                                "Added book: "
                                                + savedBook.getTitle());

                return "redirect:/catalog/"
                                + savedBook.getIsbn();
        }

        // =========================================================
        // EDIT BOOK FORM
        // =========================================================

        @GetMapping("/Catalogue/edit/{isbn}")
        public String showEditForm(
                        @PathVariable("isbn") String isbn,
                        Model model) {

                String cleanIsbn = cleanIsbn(isbn);

                Book book = bookService.getBookByIsbn(
                                cleanIsbn);

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

                Book existingBook = bookService.getBookByIsbn(cleanIsbn);

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
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) AvailabilityStatus status,
                        @RequestParam(required = false) ConditionStatus condition,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {

                int pageSize = 7;

                var accessionPage = accessionService.searchRegistry(
                                search,
                                status,
                                condition,
                                page,
                                pageSize);

                model.addAttribute(
                                "accessions",
                                accessionPage.getContent());

                model.addAttribute(
                                "page",
                                accessionPage);

                model.addAttribute(
                                "search",
                                search);

                model.addAttribute(
                                "selectedStatus",
                                status);

                model.addAttribute(
                                "selectedCondition",
                                condition);

                model.addAttribute(
                                "availabilityStatuses",
                                AvailabilityStatus.values());

                model.addAttribute(
                                "conditionStatuses",
                                ConditionStatus.values());

                return "catalogue/registry";
        }

        // =========================================================
        // DELETE BOOK
        // =========================================================

        @PostMapping("/admin/catalog/delete/{isbn}")
        public String deleteCatalogItem(
                        @PathVariable("isbn") String isbn,
                        RedirectAttributes redirectAttributes) {

                String cleanIsbn = cleanIsbn(isbn);

                Book book = bookService.getBookByIsbn(
                                cleanIsbn);

                /*
                 * A catalogue record cannot be deleted while
                 * physical copies still exist.
                 */
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
                                                "Cannot delete this book. "
                                                                + "One or more physical copies "
                                                                + "are currently checked out.");

                                return "redirect:/admin/catalog/registry";
                        }

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        "Cannot delete this book while physical "
                                                        + "copies exist. Delete the physical "
                                                        + "copies first.");

                        return "redirect:/admin/catalog/registry";
                }

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

                /*
                 * Do not allow deletion of a borrowed physical copy.
                 */
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

                Book book = bookService.getBookByIsbn(
                                cleanIsbn);

                Accession accession = accessionRepository.findById(accessionId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Accession not found: "
                                                                + accessionId));

                /*
                 * Security/integrity check:
                 *
                 * Make sure the accession being deleted
                 * actually belongs to the selected book.
                 */
                if (!accession.getBook()
                                .getBookId()
                                .equals(book.getBookId())) {

                        throw new IllegalArgumentException(
                                        "Accession does not belong to this book.");
                }

                accessionRepository.delete(accession);

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

        @GetMapping("/cantalog")
        public String catalogue(
                        @RequestParam(required = false) String search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        Model model) {

                // Prevent invalid page sizes
                if (size != 12 && size != 24 && size != 48) {
                        size = 12;
                }

                // Prevent negative page numbers
                if (page < 0) {
                        page = 0;
                }

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(
                                                Sort.Direction.ASC,
                                                "title"));

                Page<Book> bookPage = bookService.searchBooks(
                                search,
                                pageable);

                model.addAttribute("books", bookPage.getContent());
                model.addAttribute("page", bookPage);
                model.addAttribute(
                                "search",
                                search == null ? "" : search);

                return "public/catalogue";
        }

        @GetMapping("/cantalog/{isbn}")
        public String bookDetails(
                        @PathVariable String isbn,
                        Model model) {

                try {

                        Book book = bookService.getBookByIsbn(isbn);

                        long availableCount = bookService.getAvailableCount(
                                        book.getBookId());

                        model.addAttribute(
                                        "book",
                                        book);

                        model.addAttribute(
                                        "availableCount",
                                        availableCount);

                        return "public/book-detail";

                } catch (IllegalArgumentException e) {

                        return "redirect:/cantalog";
                }
        }

}
