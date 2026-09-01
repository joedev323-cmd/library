package com.example.libback.controller;

import com.example.libback.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class LibrarianController {

    private final UserService userService;

    public LibrarianController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/librarians/new")
    public String showCreateForm(Model model) {
        return "librarians/create";
    }

    @PostMapping("/librarians")
    public String createLibrarian(
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String password,
            Model model
    ) {

        try {

            userService.createLibrarian(
                    name,
                    username,
                    password
            );

            return "redirect:/admin/users/librarians?created";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "librarians/create";
        }
    }
}
