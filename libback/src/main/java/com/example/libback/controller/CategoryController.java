package com.example.libback.controller;

import com.example.libback.model.Category;
import com.example.libback.repository.CategoryRepository;
import com.example.libback.service.AuditLogService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")    
public class CategoryController {

        private final CategoryRepository categoryRepository;
        private final AuditLogService auditLogService;

        public CategoryController(
                        CategoryRepository categoryRepository,
                        AuditLogService auditLogService) {

                this.categoryRepository = categoryRepository;
                this.auditLogService = auditLogService;
        }

        // =========================================================
        // CATEGORY LIST
        // =========================================================

        @GetMapping
        public String viewCategoryDashboard(Model model) {

                model.addAttribute(
                                "categories",
                                categoryRepository.findAll());

                model.addAttribute(
                                "newCategory",
                                new Category());

                return "categories/list";
        }

        // =========================================================
        // CREATE CATEGORY
        // =========================================================

        @PostMapping("/add")
        public String processAddCategory(
                        @ModelAttribute("newCategory") Category newCategory,
                        RedirectAttributes redirectAttributes) {

                if (newCategory.getName() == null ||
                                newCategory.getName().trim().isEmpty()) {

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        "Category name cannot be blank.");

                        return "redirect:/admin/categories";
                }

                newCategory.setName(
                                newCategory.getName().trim());

                if (categoryRepository.existsByName(
                                newCategory.getName())) {

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        "Category already exists: "
                                                        + newCategory.getName());

                        return "redirect:/admin/categories";
                }

                Category savedCategory = categoryRepository.save(newCategory);

                // =====================================================
                // AUDIT
                // =====================================================

                auditLogService.logAction(
                                "CREATE_CATEGORY",
                                "CATEGORY",
                                String.valueOf(
                                                savedCategory.getCategoryId()),
                                "Created library category: "
                                                + savedCategory.getName()
                                                + " (Loan Period: "
                                                + savedCategory.getLoanPeriodDays()
                                                + " days)");

                redirectAttributes.addFlashAttribute(
                                "successMessage",
                                "Category created successfully.");

                return "redirect:/admin/categories";
        }

        // =========================================================
        // EDIT CATEGORY FORM
        // =========================================================

        @GetMapping("/edit/{id}")
        public String showEditCategoryForm(
                        @PathVariable Long id,
                        Model model) {

                Category category = categoryRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Category not found: " + id));

                model.addAttribute(
                                "category",
                                category);

                return "categories/edit";
        }

        // =========================================================
        // UPDATE CATEGORY
        // =========================================================

        @PostMapping("/edit/{id}")
        public String updateCategory(
                        @PathVariable Long id,
                        @ModelAttribute("category") Category updatedCategory,
                        RedirectAttributes redirectAttributes) {

                Category existingCategory = categoryRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Category not found: " + id));

                if (updatedCategory.getName() == null ||
                                updatedCategory.getName().trim().isEmpty()) {

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        "Category name cannot be blank.");

                        return "redirect:/admin/categories/edit/" + id;
                }

                String oldName = existingCategory.getName();
                int oldLoanPeriod = existingCategory.getLoanPeriodDays();

                String newName = updatedCategory.getName().trim();

                // Don't allow another category to have the same name
                if (!oldName.equalsIgnoreCase(newName)
                                && categoryRepository.existsByName(newName)) {

                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        "Category already exists: " + newName);

                        return "redirect:/admin/categories/edit/" + id;
                }

                existingCategory.setName(newName);
                existingCategory.setLoanPeriodDays(
                                updatedCategory.getLoanPeriodDays());

                Category savedCategory = categoryRepository.save(existingCategory);

                // =====================================================
                // AUDIT EDIT
                // =====================================================

                String auditMessage = "Updated category from ["
                                + oldName
                                + ", Loan Period: "
                                + oldLoanPeriod
                                + " days] to ["
                                + savedCategory.getName()
                                + ", Loan Period: "
                                + savedCategory.getLoanPeriodDays()
                                + " days]";

                auditLogService.logAction(
                                "UPDATE_CATEGORY",
                                "CATEGORY",
                                String.valueOf(
                                                savedCategory.getCategoryId()),
                                auditMessage);

                redirectAttributes.addFlashAttribute(
                                "successMessage",
                                "Category updated successfully.");

                return "redirect:/admin/categories";
        }
}
