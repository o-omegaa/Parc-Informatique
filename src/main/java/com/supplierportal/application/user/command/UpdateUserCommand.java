package com.supplierportal.application.user.command;
import lombok.*;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UpdateUserCommand {
    private Long id;
    private String username;
    private String email;
    private String role;
}
