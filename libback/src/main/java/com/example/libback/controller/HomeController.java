package com.example.libback.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.libback.dto.BookSearchResultDto;
import com.example.libback.service.BookService;

import java.util.List;

@Controller
public class HomeController {

    private final BookService bookService;

    // Injected BookService handles everything
    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String getHomepage() {
        return "public/index"; 
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/cantalog/search")
    public String handlePublicSearch(@RequestParam(name = "q", required = false, defaultValue = "") String query, 
                                     Model model) {
        
        // Routes cleanly through your business service layer to get calculated counts
        List<BookSearchResultDto> results = bookService.searchCatalogWithCounts(query);

        model.addAttribute("query", query);
        model.addAttribute("books", results); 
        
        return "public/public"; 
    }
}