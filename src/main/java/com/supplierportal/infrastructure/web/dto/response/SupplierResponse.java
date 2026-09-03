package com.supplierportal.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SupplierResponse {
    private Long id;
    private String companyName;
    private String ice;
    private String commercialRegister;
    private String taxIdentifier;
    private String address;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String category;
    private String status;
    private Long registeredByUserId;
    private String createdAt;
}
