package com.supplierportal.application.supplier.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
public class SupplierResult {
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
    private Instant createdAt;
}
