package com.example.libback.controller;

import com.example.libback.model.Catergory;
import com.example.libback.repository.CategoryRepository;
import com.example.libback.service.AuditLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    public CategoryController(CategoryRepository categoryRepository, AuditLogService auditLogService) {
        this.categoryRepository = categoryRepository;
        this.auditLogService = auditLogService;
    }

    // =========================================================================
    // RENDER CATEGORY MANAGEMENT DASHBOARD (GET)
    // =========================================================================
    @GetMapping
    public String viewCategoryDashboard(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("parentOptions", categoryRepository.findByParentIsNull());
        model.addAttribute("newCategory", new Catergory());
        return "categories";
    }

    // =========================================================================
    // PROCESS NEW CATEGORY REGISTRATION (POST)
    // =========================================================================
    @PostMapping("/add")
    public String processAddCategory(@ModelAttribute("newCategory") Catergory newCategory,
                                     @RequestParam(value = "parentId", required = false) Long parentId) {
        
        // Handle nested parent relation safely if a parent ID was selected
        if (parentId != null) {
            Catergory parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Parent Category ID: " + parentId));
            newCategory.setParent(parent);
        }

        categoryRepository.save(newCategory);

        // Track category registration event in our security log tracking system
        auditLogService.logAction(
                "CAT-" + newCategory.getCategoryId(),
                "CREATE_CATEGORY",
                "Created library category: " + newCategory.getName() + " (Loan Period: " + newCategory.getLoanPeriodDays() + " days)"
        );

        return "redirect:/admin/categories?success";
    }
}