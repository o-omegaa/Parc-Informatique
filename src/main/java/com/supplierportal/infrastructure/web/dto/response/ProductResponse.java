package com.supplierportal.infrastructure.web.dto.response;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data @Builder
public class ProductResponse {
    private Long id;
    private Long supplierId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String unit;
    private Integer minOrderQuantity;
    private boolean active;
    private List<String> imageReferences;
    private Double averageRating;
    private Long reviewCount;
    private Instant createdAt;
}
