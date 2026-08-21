package com.example.libback.controller;

import com.example.libback.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// Use this for Spring Boot 3.4+:
import org.springframework.test.context.bean.override.mockito.MockitoBean; 
// OR use this if on an older Spring Boot 3.x version:
// import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") 
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
 
    @MockitoBean 
    private MemberRepository borrowerRepository;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListMembers_Authenticated() throws Exception {
        // Arrange
        when(borrowerRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("members"))
                .andExpect(model().attributeExists("members"));
    }

    @Test
    void testListMembers_Unauthenticated_RedirectsToLogin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/members"))
                .andExpect(status().is3xxRedirection()); // Redirects to login
    }
}