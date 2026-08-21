package com.example.libback.controller;

import com.example.libback.service.CirculationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CirculationController {

    private final CirculationService circulationService;

    public CirculationController(CirculationService circulationService) {
        this.circulationService = circulationService;
    }

    @PostMapping("/circulation/checkout")
    public String processCheckout(@RequestParam("accessionId") String accessionId,
                                  @RequestParam("borrowerId") String borrowerId,
                                  RedirectAttributes redirectAttributes) {
        try {
            circulationService.checkoutItem(accessionId, borrowerId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book successfully issued to Borrower [" + borrowerId + "]!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System processing fault encountered.");
        }

        return "redirect:/circulation";
    }


    @PostMapping("/circulation/return")
public String processReturn(@RequestParam("accessionId") String accessionId,
                            @RequestParam("condition") String condition,
                            RedirectAttributes redirectAttributes) {
    try {
        circulationService.returnItem(accessionId, condition);
        redirectAttributes.addFlashAttribute("successMessage", 
            "Book copy [" + accessionId + "] processed successfully. Status reset to available.");
    } catch (IllegalArgumentException | IllegalStateException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while closing transaction records.");
    }

    return "redirect:/circulation";
}
}