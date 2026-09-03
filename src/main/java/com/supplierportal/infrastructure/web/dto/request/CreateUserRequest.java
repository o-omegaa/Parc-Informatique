package com.supplierportal.infrastructure.web.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateUserRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 12, max = 128)
    private String password;
    @NotBlank
    private String role;
}
