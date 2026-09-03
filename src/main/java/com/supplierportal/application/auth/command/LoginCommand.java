package com.supplierportal.application.auth.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginCommand {
    private String username;
    private String password;
}
