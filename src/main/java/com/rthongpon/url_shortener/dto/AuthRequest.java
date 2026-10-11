package com.rthongpon.url_shortener.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "is required")
    @Email(message = "must be a valid email")
    private String email;

    @NotBlank(message = "is required")
    @Size(min = 8, max = 72, message = "must be 8-72 characters")
    private String password;
}
