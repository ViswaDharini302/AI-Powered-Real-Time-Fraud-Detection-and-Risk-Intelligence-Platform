package com.frauddetectionsystem.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // CUSTOMER, FRAUD_ANALYST, or ADMIN. Defaults to CUSTOMER if left blank.
    private String role;
}
