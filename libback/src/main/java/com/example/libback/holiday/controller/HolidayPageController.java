
package com.example.libback.holiday.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/holidays")
public class HolidayPageController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle","Holiday management");
        return "holidays/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("pageTitle","Add Holiday");
        model.addAttribute("holidayId", "");
        return "holidays/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {
        model.addAttribute("holidayId", id.toString());
        return "holidays/form";
    }
}
