package com.supplierportal.domain.supplier;

import com.supplierportal.domain.shared.exception.InvalidStateTransitionException;
import com.supplierportal.domain.shared.valueobject.IceNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {
    private Long id;
    private String companyName;
    private IceNumber ice;
    private String commercialRegister;
    private String taxIdentifier;
    private String address;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private SupplierCategory category;
    private SupplierStatus status;
    private Long registeredByUserId;
    private Instant createdAt;
    private Instant updatedAt;

    public void submitForValidation() {
        if (this.status != SupplierStatus.DRAFT) {
            throw new InvalidStateTransitionException("Only DRAFT suppliers can be submitted for validation");
        }
        this.status = SupplierStatus.PENDING_VALIDATION;
    }

    public void validate() {
        if (this.status != SupplierStatus.PENDING_VALIDATION) {
            throw new InvalidStateTransitionException("Only PENDING_VALIDATION suppliers can be validated");
        }
        this.status = SupplierStatus.VALIDATED;
    }

    public void reject(String reason) {
        if (this.status != SupplierStatus.PENDING_VALIDATION) {
            throw new InvalidStateTransitionException("Only PENDING_VALIDATION suppliers can be rejected");
        }
        this.status = SupplierStatus.REJECTED;
    }

    public void suspend(String reason) {
        if (this.status != SupplierStatus.VALIDATED) {
            throw new InvalidStateTransitionException("Only VALIDATED suppliers can be suspended");
        }
        this.status = SupplierStatus.SUSPENDED;
    }

    public void reinstate() {
        if (this.status != SupplierStatus.SUSPENDED) {
            throw new InvalidStateTransitionException("Only SUSPENDED suppliers can be reinstated");
        }
        this.status = SupplierStatus.VALIDATED;
    }
}
