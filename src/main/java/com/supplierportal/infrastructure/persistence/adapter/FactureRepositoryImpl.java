package com.supplierportal.infrastructure.persistence.adapter;

import com.supplierportal.domain.facture.*;
import com.supplierportal.infrastructure.persistence.jpa.repository.FactureJpaRepository;
import com.supplierportal.infrastructure.persistence.mapper.FactureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FactureRepositoryImpl implements FactureRepository {
    private final FactureJpaRepository jpaRepository;

    @Override public Facture save(Facture f) { return FactureMapper.toDomain(jpaRepository.save(FactureMapper.toEntity(f))); }
    @Override public Optional<Facture> findById(Long id) { return jpaRepository.findById(id).map(FactureMapper::toDomain); }
    @Override public List<Facture> findAll() { return jpaRepository.findAll().stream().map(FactureMapper::toDomain).collect(Collectors.toList()); }
    @Override public List<Facture> findByFournisseurUserId(Long userId) { return jpaRepository.findByFournisseurUserId(userId).stream().map(FactureMapper::toDomain).collect(Collectors.toList()); }
    @Override public List<Facture> findByDemandeId(Long id) { return jpaRepository.findByDemandeId(id).stream().map(FactureMapper::toDomain).collect(Collectors.toList()); }
    @Override public List<Facture> findByStatus(FactureStatus s) { return jpaRepository.findByStatus(s.name()).stream().map(FactureMapper::toDomain).collect(Collectors.toList()); }
    @Override public long count() { return jpaRepository.count(); }
}
