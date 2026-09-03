package com.supplierportal.application.supplier.service;

import com.supplierportal.application.supplier.command.UpdateSupplierCommand;
import com.supplierportal.application.supplier.command.CreateSupplierCommand;
import com.supplierportal.application.supplier.result.SupplierResult;
import com.supplierportal.domain.supplier.Supplier;
import com.supplierportal.domain.supplier.SupplierCategory;
import com.supplierportal.domain.supplier.SupplierRepository;
import com.supplierportal.domain.supplier.SupplierStatus;
import com.supplierportal.domain.shared.exception.ValidationException;
import com.supplierportal.domain.shared.valueobject.IceNumber;
import com.supplierportal.domain.user.UserRepository;
import com.supplierportal.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    @Transactional
    public SupplierResult create(CreateSupplierCommand command) {
        var account = userRepository.findById(command.getRegisteredByUserId())
                .orElseThrow(() -> new ValidationException("The selected user does not exist"));
        if (account.getRole() != Role.SUPPLIER) {
            throw new ValidationException("A supplier profile can only be linked to a SUPPLIER account");
        }
        if (supplierRepository.findByRegisteredByUserId(command.getRegisteredByUserId()).isPresent()) {
            throw new ValidationException("This supplier account already has a profile");
        }
        if (supplierRepository.existsByIce(command.getIce())) {
            throw new ValidationException("A supplier already exists for this ICE");
        }
        Supplier supplier = Supplier.builder()
                .registeredByUserId(command.getRegisteredByUserId())
                .companyName(command.getCompanyName())
                .ice(IceNumber.of(command.getIce()))
                .category(SupplierCategory.valueOf(command.getCategory().toUpperCase()))
                .address(command.getAddress())
                .contactPerson(command.getContactPerson())
                .contactEmail(command.getContactEmail())
                .contactPhone(command.getContactPhone())
                .status(SupplierStatus.DRAFT)
                .createdAt(java.time.Instant.now())
                .build();
        return toResult(supplierRepository.save(supplier));
    }

    public List<SupplierResult> findAll() {
        return supplierRepository.findAll().stream()
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    public SupplierResult findById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return toResult(supplier);
    }

    public SupplierResult findByUserId(Long userId) {
        Supplier supplier = supplierRepository.findByRegisteredByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return toResult(supplier);
    }

    @Transactional
    public SupplierResult update(UpdateSupplierCommand command) {
        Supplier supplier = supplierRepository.findById(command.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        if (command.getCompanyName() != null) supplier.setCompanyName(command.getCompanyName());
        if (command.getAddress() != null) supplier.setAddress(command.getAddress());
        if (command.getContactPerson() != null) supplier.setContactPerson(command.getContactPerson());
        if (command.getContactEmail() != null) supplier.setContactEmail(command.getContactEmail());
        if (command.getContactPhone() != null) supplier.setContactPhone(command.getContactPhone());
        return toResult(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResult submit(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.submitForValidation();
        return toResult(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResult validate(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.validate();
        return toResult(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResult reject(Long supplierId, String reason) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.reject(reason);
        return toResult(supplierRepository.save(supplier));
    }

    public void delete(Long id) {
        supplierRepository.findById(id).orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplierRepository.deleteById(id);
    }

    public SupplierResult suspend(Long id, String reason) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.suspend(reason);
        return toResult(supplierRepository.save(supplier));
    }

    public SupplierResult reinstate(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.reinstate();
        return toResult(supplierRepository.save(supplier));
    }

    public List<SupplierResult> search(String query, String status, String category) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        String normalizedStatus = status == null ? "" : status.trim().toUpperCase();
        String normalizedCategory = category == null ? "" : category.trim().toUpperCase();
        List<Supplier> all = supplierRepository.findAll();
        return all.stream()
                .filter(s -> normalizedQuery.isEmpty()
                        || s.getCompanyName().toLowerCase().contains(normalizedQuery)
                        || (s.getIce() != null && s.getIce().getValue().contains(normalizedQuery)))
                .filter(s -> normalizedStatus.isEmpty() || s.getStatus().name().equals(normalizedStatus))
                .filter(s -> normalizedCategory.isEmpty() || s.getCategory().name().equals(normalizedCategory))
                .map(this::toResult)
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean isOwnedByUser(Long supplierId, Long userId) {
        return supplierRepository.findById(supplierId)
                .map(s -> userId.equals(s.getRegisteredByUserId()))
                .orElse(false);
    }

    private SupplierResult toResult(Supplier s) {
        return SupplierResult.builder()
                .id(s.getId())
                .companyName(s.getCompanyName())
                .ice(s.getIce() != null ? s.getIce().getValue() : null)
                .commercialRegister(s.getCommercialRegister())
                .taxIdentifier(s.getTaxIdentifier())
                .address(s.getAddress())
                .contactPerson(s.getContactPerson())
                .contactEmail(s.getContactEmail())
                .contactPhone(s.getContactPhone())
                .category(s.getCategory() != null ? s.getCategory().name() : null)
                .status(s.getStatus() != null ? s.getStatus().name() : null)
                .registeredByUserId(s.getRegisteredByUserId())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
