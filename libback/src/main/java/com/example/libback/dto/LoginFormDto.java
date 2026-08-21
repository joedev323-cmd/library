package com.example.libback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginFormDto {

    @NotBlank(message = "Username or email is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private boolean rememberMe;

    // Standard No-Args Constructor (Required by Spring/Thymeleaf)
    public LoginFormDto() {
    }

    // All-Args Constructor (Convenient for testing)
    public LoginFormDto(String username, String password, Boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe =rememberMe;
    }

    // Getters and Setters (Required for Spring to bind form data)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}