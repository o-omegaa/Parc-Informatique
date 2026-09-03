package com.supplierportal.infrastructure.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registration request DTO.
 * NOTE: No password field — the server generates a temporary password
 * and sends it by email. The user must change it on first login.
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    // No password field — system generates it automatically

    @NotBlank
    private String companyName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{15}$", message = "ICE must be 15 digits")
    private String ice;

    @NotBlank
    private String category;

    private String role = "SUPPLIER";
}
