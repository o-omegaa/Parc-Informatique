package com.supplierportal.infrastructure.persistence.adapter;

import com.supplierportal.domain.supplier.Supplier;
import com.supplierportal.domain.supplier.SupplierRepository;
import com.supplierportal.domain.supplier.SupplierStatus;
import com.supplierportal.infrastructure.persistence.jpa.repository.SupplierJpaRepository;
import com.supplierportal.infrastructure.persistence.mapper.SupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SupplierRepositoryImpl implements SupplierRepository {

    private final SupplierJpaRepository supplierJpaRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public Supplier save(Supplier supplier) {
        return supplierMapper.toDomain(supplierJpaRepository.save(supplierMapper.toJpaEntity(supplier)));
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return supplierJpaRepository.findById(id).map(supplierMapper::toDomain);
    }

    @Override
    public Optional<Supplier> findByRegisteredByUserId(Long userId) {
        return supplierJpaRepository.findByRegisteredByUserId(userId).map(supplierMapper::toDomain);
    }

    @Override
    public List<Supplier> findAll() {
        return supplierJpaRepository.findAll().stream()
                .map(supplierMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Supplier> findByStatus(SupplierStatus status) {
        return supplierJpaRepository.findByStatus(status.name()).stream()
                .map(supplierMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIce(String iceValue) {
        return supplierJpaRepository.existsByIce(iceValue);
    }

    @Override
    public void deleteById(Long id) {
        supplierJpaRepository.deleteById(id);
    }
}
