package com.expense.splitter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for person create/update requests.
 * This version uses explicit getters/setters (no Lombok) to avoid IDE/Lombok issues.
 */
public class PersonRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    public PersonRequest() {}

    public PersonRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and setters (explicit)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
