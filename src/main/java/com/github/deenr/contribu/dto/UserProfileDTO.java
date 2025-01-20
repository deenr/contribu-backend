package com.github.deenr.contribu.dto;

import jakarta.validation.constraints.NotBlank;

public class UserProfileDTO {
    @NotBlank(message = "First name cannot be empty.")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty.")
    private String lastName;

    public UserProfileDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
