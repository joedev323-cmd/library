package com.example.libback.controller;

import com.example.libback.model.Loan;
import com.example.libback.model.User;
import com.example.libback.service.CirculationService;
import com.example.libback.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final CirculationService circulationService;
    private final UserRepository userRepository;

    public PaymentController(
            CirculationService circulationService,
            UserRepository userRepository) {

        this.circulationService = circulationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showPaymentPage(
            @RequestParam(required = false) String accessionId,
            Model model) {

        if (accessionId != null
                && !accessionId.isBlank()) {

            try {

                Loan loan =
                        circulationService.findLoanByAccession(
                                accessionId);

                model.addAttribute("loan", loan);

            } catch (IllegalArgumentException e) {

                model.addAttribute(
                        "errorMessage",
                        e.getMessage());
            }
        }

        return "payments/index";
    }

    @PostMapping
    public String processPayment(
            @RequestParam("loanId") Long loanId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(required = false) String receiptNumber,
            @RequestParam(required = false) String remarks,
            RedirectAttributes redirectAttributes) {

        try {

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || "anonymousUser".equals(
                            authentication.getPrincipal())) {

                throw new IllegalStateException(
                        "You must be logged in to record a payment.");
            }

            String username =
                    authentication.getName();

            User receivedBy =
                    userRepository
                            .findByUsername(username)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Authenticated user not found."));

            circulationService.processFinePayment(
                    loanId,
                    amount,
                    receiptNumber,
                    remarks,
                    receivedBy);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Fine payment recorded successfully.");

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "An error occurred while processing the payment.");
        }

        return "redirect:/payments";
    }
}
