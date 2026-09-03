package com.supplierportal.infrastructure.web.dto.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String unit;
    private Integer minOrderQuantity;
}
