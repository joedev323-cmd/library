package com.example.libback.controller;

import com.example.libback.model.Category;
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

    public CategoryController(
            CategoryRepository categoryRepository,
            AuditLogService auditLogService
    ) {
        this.categoryRepository = categoryRepository;
        this.auditLogService = auditLogService;
    }

    // =========================================================
    // CATEGORY DASHBOARD
    // =========================================================

    @GetMapping
    public String viewCategoryDashboard(Model model) {

        model.addAttribute(
                "categories",
                categoryRepository.findAll()
        );

        model.addAttribute(
                "newCategory",
                new Category()
        );

        return "categories";
    }

    // =========================================================
    // CREATE CATEGORY
    // =========================================================

    @PostMapping("/add")
    public String processAddCategory(
            @ModelAttribute("newCategory") Category newCategory
    ) {

        if (newCategory.getName() == null ||
                newCategory.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Category name cannot be blank."
            );
        }

        newCategory.setName(
                newCategory.getName().trim()
        );

        if (categoryRepository.existsByName(
                newCategory.getName()
        )) {

            throw new IllegalArgumentException(
                    "Category already exists: "
                    + newCategory.getName()
            );
        }

        Category savedCategory =
                categoryRepository.save(newCategory);

        // =====================================================
        // AUDIT
        // =====================================================

        auditLogService.logAction(
                "CREATE_CATEGORY",
                "CATEGORY",
                String.valueOf(savedCategory.getCategoryId()),
                "Created library category: "
                        + savedCategory.getName()
                        + " (Loan Period: "
                        + savedCategory.getLoanPeriodDays()
                        + " days)"
        );

        return "redirect:/admin/categories?success";
    }
}
