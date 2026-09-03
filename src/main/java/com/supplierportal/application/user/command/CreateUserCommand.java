package com.supplierportal.application.user.command;
import lombok.*;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CreateUserCommand {
    private String username;
    private String email;
    private String password;
    private String role;
}
