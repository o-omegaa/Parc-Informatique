package com.supplierportal.infrastructure.web.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateSupplierRequest {
    private String companyName;
    private String address;
    private String contactPerson;
    @Email
    private String contactEmail;
    private String contactPhone;
}
