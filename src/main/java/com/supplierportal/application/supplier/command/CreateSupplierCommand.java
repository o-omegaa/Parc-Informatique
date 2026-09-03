package com.supplierportal.application.supplier.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateSupplierCommand {
    private Long registeredByUserId;
    private String companyName;
    private String ice;
    private String category;
    private String address;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
}
