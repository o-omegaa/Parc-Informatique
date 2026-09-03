package com.supplierportal.infrastructure.persistence.mapper;

import com.supplierportal.domain.shared.valueobject.IceNumber;
import com.supplierportal.domain.supplier.Supplier;
import com.supplierportal.domain.supplier.SupplierCategory;
import com.supplierportal.domain.supplier.SupplierStatus;
import com.supplierportal.infrastructure.persistence.jpa.entity.SupplierJpaEntity;
import com.supplierportal.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class SupplierMapper {

    public Supplier toDomain(SupplierJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Supplier.builder()
                .id(entity.getId())
                .companyName(entity.getCompanyName())
                .ice(IceNumber.of(entity.getIce()))
                .commercialRegister(entity.getCommercialRegister())
                .taxIdentifier(entity.getTaxIdentifier())
                .address(entity.getAddress())
                .contactPerson(entity.getContactPerson())
                .contactEmail(entity.getContactEmail())
                .contactPhone(entity.getContactPhone())
                .category(SupplierCategory.valueOf(entity.getCategory()))
                .status(SupplierStatus.valueOf(entity.getStatus()))
                .registeredByUserId(entity.getRegisteredByUser() != null ? entity.getRegisteredByUser().getId() : null)
                .createdAt(toInstant(entity.getCreatedAt()))
                .updatedAt(toInstant(entity.getUpdatedAt()))
                .build();
    }

    public SupplierJpaEntity toJpaEntity(Supplier domain) {
        if (domain == null) {
            return null;
        }
        UserJpaEntity registeredByUser = null;
        if (domain.getRegisteredByUserId() != null) {
            registeredByUser = new UserJpaEntity();
            registeredByUser.setId(domain.getRegisteredByUserId());
        }

        return SupplierJpaEntity.builder()
                .id(domain.getId())
                .companyName(domain.getCompanyName())
                .ice(domain.getIce() != null ? domain.getIce().getValue() : null)
                .commercialRegister(domain.getCommercialRegister())
                .taxIdentifier(domain.getTaxIdentifier())
                .address(domain.getAddress())
                .contactPerson(domain.getContactPerson())
                .contactEmail(domain.getContactEmail())
                .contactPhone(domain.getContactPhone())
                .category(domain.getCategory() != null ? domain.getCategory().name() : null)
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .registeredByUser(registeredByUser)
                .createdAt(toLocalDateTime(domain.getCreatedAt()))
                .updatedAt(toLocalDateTime(domain.getUpdatedAt()))
                .build();
    }

    private Instant toInstant(LocalDateTime ldt) {
        return ldt != null ? ldt.toInstant(ZoneOffset.UTC) : null;
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }
}
