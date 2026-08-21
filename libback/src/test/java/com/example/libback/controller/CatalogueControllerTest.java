 package com.example.libback.controller;

import com.example.libback.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CatalogueController.class)
public class CatalogueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService; // Mock service dependencies so WebMvc context loads successfully

    @Test
    public void publicShouldAccessCatalogue() throws Exception {
        mockMvc.perform(get("/Catalogue"))
                .andExpect(status().isOk())
                .andExpect(view().name("Catalogue"));
    }

    @Test
    public void anonymousUserShouldNotAccessAddBook() throws Exception {
        mockMvc.perform(get("/add-book"))
                .andExpect(status().is3xxRedirection()); // Redirects to login page
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminShouldAccessAddBook() throws Exception {
        mockMvc.perform(get("/add-book"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-book"));
    }
}