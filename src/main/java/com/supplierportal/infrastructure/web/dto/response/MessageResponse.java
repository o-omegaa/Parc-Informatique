package com.supplierportal.infrastructure.web.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data @Builder
public class MessageResponse {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String content;
    private boolean read;
    private Instant sentAt;
}
