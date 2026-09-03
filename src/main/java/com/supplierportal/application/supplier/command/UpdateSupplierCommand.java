package com.supplierportal.application.supplier.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateSupplierCommand {
    private Long supplierId;
    private String companyName;
    private String address;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
}
