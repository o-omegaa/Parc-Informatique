package com.supplierportal.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSupplierRequest {
    @NotNull
    private Long registeredByUserId;
    @NotBlank
    private String companyName;
    @NotBlank
    @Pattern(regexp = "\\d{15}", message = "L'ICE doit contenir 15 chiffres")
    private String ice;
    @NotBlank
    private String category;
    private String address;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
}
