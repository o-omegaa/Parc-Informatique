package com.supplierportal.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    @NotBlank private String name;
    private String description;
    private BigDecimal price;
    @NotBlank private String category;
    private String unit;
    private Integer minOrderQuantity;
}
