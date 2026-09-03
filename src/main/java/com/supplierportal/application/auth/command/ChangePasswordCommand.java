package com.supplierportal.application.auth.command;
import lombok.*;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ChangePasswordCommand {
    private String username;
    private String currentPassword;
    private String newPassword;
}
