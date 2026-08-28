package com.example.libback.controller;

import com.example.libback.model.User;
import com.example.libback.repository.UserRepository;
import com.example.libback.service.CirculationService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CirculationController {

    private final CirculationService circulationService;
    private final UserRepository userRepository;

    public CirculationController(
            CirculationService circulationService,
            UserRepository userRepository) {

        this.circulationService = circulationService;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CIRCULATION PAGE
    // =========================================================

    @GetMapping("/circulation")
    public String showCirculationPage() {
        return "circulation/index";
    }

    // =========================================================
    // CHECKOUT
    // =========================================================

    @PostMapping("/circulation/checkout")
    public String processCheckout(
            @RequestParam("accessionId") String accessionId,
            @RequestParam("memberId") String memberId,
            RedirectAttributes redirectAttributes) {

        try {

            // Get currently authenticated Spring Security user
            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {

                throw new IllegalStateException(
                        "You must be logged in to issue a book.");
            }

            String username = authentication.getName();

            // Find the corresponding application User
            User issuedBy = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Authenticated user not found: " + username));

            // Perform checkout
            circulationService.checkoutItem(
                    accessionId,
                    memberId,
                    issuedBy);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book successfully issued to member ["
                            + memberId + "]!");

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "System processing fault encountered.");
        }

        return "redirect:/circulation";
    }

    // =========================================================
    // RETURN
    // =========================================================

    @PostMapping("/circulation/return")
    public String processReturn(
            @RequestParam("accessionId") String accessionId,
            @RequestParam("condition") String condition,
            RedirectAttributes redirectAttributes) {

        try {

            circulationService.returnItem(
                    accessionId,
                    condition);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book copy [" + accessionId
                            + "] processed successfully.");

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "An error occurred while closing transaction records.");
        }

        return "redirect:/circulation";
    }
}
