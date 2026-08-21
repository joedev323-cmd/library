package com.example.libback.controller;

import com.example.libback.model.Borrower;
import com.example.libback.model.Item;
import com.example.libback.model.enums.LoanStatus;
import com.example.libback.repository.BorrowerRepository;
import com.example.libback.repository.ItemRepository;
import com.example.libback.repository.CategoryRepository;
import com.example.libback.repository.LoanRepository;
import com.example.libback.service.BookService;
import com.example.libback.service.AuditLogService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CatalogueController {

    private final BookService bookService;
    private final ItemRepository itemRepository;
    private final BorrowerRepository borrowerRepository;
    private final AuditLogService auditLogService;
    private final CategoryRepository categoryRepository;
    private final LoanRepository loanRepository;

    public CatalogueController(BookService bookService, 
                               ItemRepository itemRepository,
                               BorrowerRepository borrowerRepository, 
                               AuditLogService auditLogService,
                               CategoryRepository categoryRepository,
                               LoanRepository loanRepository) {
        this.bookService = bookService;
        this.itemRepository = itemRepository;
        this.borrowerRepository = borrowerRepository;
        this.auditLogService = auditLogService;
        this.categoryRepository = categoryRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/Catalogue")
    public String getCatalogue(Model model) {
        model.addAttribute("books", itemRepository.findAll());
        return "Catalogue";
    }

    @GetMapping("/circulation")
    public String showCirculationPage() {
        return "circulation";  
    }

    // =========================================================================
    // RENDER DEDICATED DETAIL & COPY MANAGEMENT PAGE (GET)
    // =========================================================================
    @GetMapping("/catalog/{isbn}")
    public String showBookDetails(@PathVariable("isbn") String isbn, Model model) {
        String cleanIsbn = isbn.replaceAll("[^a-zA-Z0-9]", "");
        
        Item item = itemRepository.findById(cleanIsbn)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ISBN: " + cleanIsbn));
        
        model.addAttribute("item", item);
        model.addAttribute("accessions", item.getAccessions());
        
        return "Catalogue"; 
    }

    // =========================================================================
    // PROCESS BATCH ACCESSION FORM (POST)
    // =========================================================================
    @PostMapping("/catalog/{isbn}/accessions/batch")
    public String processBatchAccessions(@PathVariable("isbn") String isbn,
                                         @RequestParam("prefix") String prefix,
                                         @RequestParam("quantity") int quantity,
                                         @RequestParam("shelfLocation") String shelfLocation) {
        
        String cleanIsbn = isbn.replaceAll("[^a-zA-Z0-9]", "");

        bookService.generateBatchAccessions(cleanIsbn, prefix, quantity, shelfLocation);

        // Uses cleanIsbn as the key in your audit trace
        auditLogService.logAction(
                cleanIsbn, 
                "BATCH_ADD_ACCESSIONS", 
                "Batch generated " + quantity + " physical copies with prefix: " + prefix
        );

        return "redirect:/catalog/" + cleanIsbn + "?success";
    }

    @PostMapping("/admin/register-borrower")
    public String registerStudent(@RequestParam("studentId") String studentId,
                                  @RequestParam("name") String name,
                                  @RequestParam("email") String email,
                                  @RequestParam("memberType") String type) {
        
        Borrower student = new Borrower();
        student.setBorrowerId(studentId); 
        student.setName(name);
        student.setEmail(email);
        student.setMemberType(com.example.libback.model.enums.MemberType.valueOf(type.toUpperCase()));

        borrowerRepository.save(student);

        return "redirect:/members?success";
    }

    // =========================================================================
    // RENDER ADD FORM WITH CATEGORIES (GET)
    // =========================================================================
    @GetMapping("/admin/catalog/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("item", new Item()); 
        model.addAttribute("categories", categoryRepository.findAll()); // Populates the HTML dropdown
        return "add-book";  
    }

    // =========================================================================
    // PROCESS ADD BOOK FORM (POST)
    // =========================================================================
    @PostMapping("/admin/catalog/add")
    public String processAddBook(@ModelAttribute Item newItem) {
        if (newItem.getIsbn() != null) {
            newItem.setIsbn(newItem.getIsbn().replaceAll("[^a-zA-Z0-9]", ""));
        }
        
        // Save the metadata (which includes Category selections bound by Thymeleaf!)
        bookService.saveBook(newItem);
        
        // Log action with ISBN saved to track exactly which catalog item was touched
        auditLogService.logAction(newItem.getIsbn(), "ADD_BOOK_CATALOG", "Added metadata details for book: " + newItem.getTitle());
        
        return "redirect:/catalog/" + newItem.getIsbn();
    }

    // =========================================================================
    // RENDER EDIT FORM WITH CATEGORIES (GET)
    // =========================================================================
    @GetMapping("/Catalogue/edit/{isbn}")
    public String showEditForm(@PathVariable("isbn") String isbn, Model model) {
        String cleanIsbn = isbn.replaceAll("[^a-zA-Z0-9]", ""); 
        
        Item item = itemRepository.findById(cleanIsbn)
                .orElseThrow(() -> new IllegalArgumentException("No catalog record found for ISBN: " + cleanIsbn));
        
        model.addAttribute("item", item);
        model.addAttribute("categories", categoryRepository.findAll()); // Populates categories for editing
        return "edit-book"; 
    }

    // =========================================================================
    // SUBMIT UPDATED METADATA (POST)
    // =========================================================================
    @PostMapping("/Catalogue/update/{isbn}")
    public String updateItem(@PathVariable("isbn") String isbn, @ModelAttribute("item") Item updatedItem) {
        String cleanIsbn = isbn.replaceAll("[^a-zA-Z0-9]", "");

        Item existingItem = itemRepository.findById(cleanIsbn)
                .orElseThrow(() -> new IllegalArgumentException("No catalog record found for ISBN: " + cleanIsbn));

        // Transfer screen field changes to the managed DB record
        existingItem.setTitle(updatedItem.getTitle());
        existingItem.setAuthor(updatedItem.getAuthor());
        existingItem.setPublisher(updatedItem.getPublisher());
        existingItem.setEdition(updatedItem.getEdition());
        existingItem.setDescription(updatedItem.getDescription());
        
        // Transfer updated categories
        existingItem.setCategories(updatedItem.getCategories());

        itemRepository.save(existingItem); 
        
        // Log updated details
        auditLogService.logAction(cleanIsbn, "UPDATE_BOOK_METADATA", "Modified metadata details for book: " + existingItem.getTitle());
        
        return "redirect:/catalog/" + cleanIsbn; 
    }

    // =========================================================================
    // VIEW GLOBAL CATALOG REGISTER (GET)
    // =========================================================================
    @GetMapping("/admin/catalog/registry")
    public String showCatalogRegistry(Model model) {
        // This returns all items, even those with zero generated accessions
        model.addAttribute("books", itemRepository.findAll());
        return "catalog-registry"; 
    }

    // =========================================================================
    // DELETE MASTER BLUEPRINT (POST)
    // =========================================================================
    @PostMapping("/admin/catalog/delete/{isbn}")
    public String deleteCatalogItem(@PathVariable("isbn") String isbn, RedirectAttributes redirectAttributes) {
        String cleanIsbn = isbn.replaceAll("[^a-zA-Z0-9]", "");

        Item item = itemRepository.findById(cleanIsbn)
                .orElseThrow(() -> new IllegalArgumentException("No catalog record found for ISBN: " + cleanIsbn));

        // 1. Check if physical copies (Accessions) exist at all
        if (item.getAccessions() != null && !item.getAccessions().isEmpty()) {
            
            // 2. Check if ANY of those physical copies currently have an ACTIVE loan record in the system
            boolean hasActiveLoans = item.getAccessions().stream()
                    .anyMatch(accession -> loanRepository
                            .findByAccessionAccessionIdAndStatus(accession.getAccessionId(), LoanStatus.ACTIVE)
                            .isPresent()
                    );

            if (hasActiveLoans) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "CRITICAL WARNING: Cannot delete this book! One or more physical copies are currently checked out by members.");
                return "redirect:/admin/catalog/registry";
            }

            // 3. If copies exist but none are checked out, prevent accidental deletion of inventory
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot delete master registry record. Please delete the inactive physical accession copies first.");
            return "redirect:/admin/catalog/registry";
        }

        // Safe to delete if we passed all checks (0 physical copies exist)
        itemRepository.delete(item);
        
        redirectAttributes.addFlashAttribute("successMessage", "Book blueprint successfully removed.");
        return "redirect:/admin/catalog/registry";
    }

    // =========================================================================
    // DELETE INDIVIDUAL PHYSICAL ACCESSION COPY (POST)
    // =========================================================================
    @PostMapping("/catalog/accession/delete/{accessionId}")
    public String deleteAccessionCopy(@PathVariable("accessionId") String accessionId, 
                                      @RequestParam("isbn") String isbn, 
                                      RedirectAttributes redirectAttributes) {
        
        // 1. Safety Check: Is this specific barcode copy currently out on loan?
        boolean isCurrentlyBorrowed = loanRepository
                .findByAccessionAccessionIdAndStatus(accessionId, LoanStatus.ACTIVE)
                .isPresent();

        if (isCurrentlyBorrowed) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot delete copy " + accessionId + "! It is currently out on loan.");
            return "redirect:/catalog/" + isbn;
        }

        // 2. Fetch the parent item and use our helper methods + orphanRemoval to delete it safely
        Item item = itemRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ISBN: " + isbn));

        item.getAccessions().stream()
                .filter(acc -> acc.getAccessionId().equals(accessionId))
                .findFirst()
                .ifPresent(item::removeAccession); // Triggers JPA orphanRemoval to delete from DB

        itemRepository.save(item);

        // 3. Log the audit trail
        auditLogService.logAction(
                isbn, 
                "DELETE_ACCESSION_COPY", 
                "Removed physical copy barcode: " + accessionId
        );

        redirectAttributes.addFlashAttribute("successMessage", "Physical copy " + accessionId + " has been successfully removed.");
        return "redirect:/catalog/" + isbn;
    }
}