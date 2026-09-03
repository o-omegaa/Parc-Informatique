package com.supplierportal.infrastructure.web.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data @Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private Long reviewerUserId;
    private int rating;
    private String comment;
    private Instant createdAt;
}
