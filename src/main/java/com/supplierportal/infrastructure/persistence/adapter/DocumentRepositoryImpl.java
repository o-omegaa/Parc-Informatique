package com.supplierportal.infrastructure.persistence.adapter;
import com.supplierportal.domain.document.*;
import com.supplierportal.infrastructure.persistence.jpa.repository.DocumentJpaRepository;
import com.supplierportal.infrastructure.persistence.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {
    private final DocumentJpaRepository jpaRepo;
    private final DocumentMapper mapper;

    @Override public Optional<SupplierDocument> findById(Long id) { return jpaRepo.findById(id).map(mapper::toDomain); }
    @Override public List<SupplierDocument> findAll() { return jpaRepo.findAll().stream().map(mapper::toDomain).collect(Collectors.toList()); }
    @Override public List<SupplierDocument> findBySupplierId(Long supplierId) { return jpaRepo.findBySupplierId(supplierId).stream().map(mapper::toDomain).collect(Collectors.toList()); }
    @Override public List<SupplierDocument> findByStatus(DocumentStatus status) { return jpaRepo.findByStatus(status.name()).stream().map(mapper::toDomain).collect(Collectors.toList()); }
    @Override public SupplierDocument save(SupplierDocument doc) { return mapper.toDomain(jpaRepo.save(mapper.toJpa(doc))); }
    public void deleteById(Long id) { jpaRepo.deleteById(id); }
}
