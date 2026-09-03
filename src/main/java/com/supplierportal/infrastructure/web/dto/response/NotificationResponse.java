package com.supplierportal.infrastructure.web.dto.response;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationResponse {
    private Long id;
    private Long recipientUserId;
    private String type;
    private String message;
    private boolean read;
    private String createdAt;
}
