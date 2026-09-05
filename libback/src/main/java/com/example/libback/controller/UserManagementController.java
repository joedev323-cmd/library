package com.example.libback.controller;

import com.example.libback.model.User;
import com.example.libback.model.enums.UserRole;
import com.example.libback.repository.UserRepository;
import com.example.libback.service.AuditLogService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;

    public UserManagementController(UserRepository userRepository,
            AuditLogService auditLogService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/create")
    public String createUser(@RequestParam String name,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            RedirectAttributes ra) {

        // 1. Validate required fields
        if (name == null || name.isBlank()
                || username == null || username.isBlank()) {
            ra.addFlashAttribute("errorMessage", "Name and username are required.");
            return "redirect:/admin/users";
        }
        String trimmedUsername = username.trim();

        // 2. Unique username check
        if (userRepository.existsByUsername(trimmedUsername)) {
            ra.addFlashAttribute("errorMessage",
                    "Username '" + trimmedUsername + "' already exists.");
            return "redirect:/admin/users";
        }

        // 3. Password strength
        if (password == null || password.length() < 8) {
            ra.addFlashAttribute("errorMessage",
                    "Password must be at least 8 characters.");
            return "redirect:/admin/users";
        }

        // 4. Role must match a UserRole enum constant
        UserRole parsedRole;
        try {
            parsedRole = UserRole.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("errorMessage", "Invalid role: " + role);
            return "redirect:/admin/users";
        }

        // 5. Create and save
        User user = new User(name.trim(), trimmedUsername,
                passwordEncoder.encode(password), parsedRole);
        userRepository.save(user);

        // 6. Audit trail
        auditLogService.logAction(
                "CREATE_USER",
                "USER",
                String.valueOf(user.getUserId()),
                "Created account '" + user.getUsername() + "' with role "
                        + parsedRole.name());

        ra.addFlashAttribute("successMessage",
                "Account '" + user.getUsername() + "' created.");
        return "redirect:/admin/users";
    }

    @PostMapping("/toggle/{userId}")
    public String toggleActive(@PathVariable Long userId, RedirectAttributes ra) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            ra.addFlashAttribute("errorMessage", "Account not found.");
            return "redirect:/admin/users";
        }

        if (user.getRole() == UserRole.SUPER_ADMIN && user.isActive()) {

            long activeSuperAdmins = userRepository
                    .countByRoleAndActive(UserRole.SUPER_ADMIN, true);

            if (activeSuperAdmins <= 1) {
                ra.addFlashAttribute("errorMessage",
                        "Cannot deactivate the last active super admin.");
                return "redirect:/admin/users";
            }
        }

        user.setActive(!user.isActive());
        userRepository.save(user);

        auditLogService.logAction(
                "UPDATE_USER",
                "USER",
                String.valueOf(user.getUserId()),
                (user.isActive() ? "Activated" : "Deactivated")
                        + " account '" + user.getUsername() + "'");

        ra.addFlashAttribute("successMessage",
                "Account '" + user.getUsername() + "' "
                        + (user.isActive() ? "activated." : "deactivated."));
        return "redirect:/admin/users";
    }
}
