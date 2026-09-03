package com.supplierportal.application.auth.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Command to register a new supplier.
 * Password is NOT included — the system generates a temporary one
 * and sends it by email.
 */
@Getter
@AllArgsConstructor
public class RegisterCommand {
    private String username;
    private String email;
    private String companyName;
    private String ice;
    private String category;
    private String role;
}
