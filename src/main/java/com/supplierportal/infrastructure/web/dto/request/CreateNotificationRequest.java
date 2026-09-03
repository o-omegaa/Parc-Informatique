package com.supplierportal.infrastructure.web.dto.request;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateNotificationRequest {
    private Long recipientUserId;
    private String type;
    private String message;
}
