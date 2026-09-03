package com.supplierportal.infrastructure.persistence.adapter;

import com.supplierportal.domain.demande.*;
import com.supplierportal.infrastructure.persistence.jpa.repository.DemandeJpaRepository;
import com.supplierportal.infrastructure.persistence.mapper.DemandeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DemandeRepositoryImpl implements DemandeRepository {
    private final DemandeJpaRepository jpaRepository;

    @Override public Demande save(Demande d) { return DemandeMapper.toDomain(jpaRepository.save(DemandeMapper.toEntity(d))); }
    @Override public Optional<Demande> findById(Long id) { return jpaRepository.findById(id).map(DemandeMapper::toDomain); }
    @Override public List<Demande> findAll() { return jpaRepository.findAll().stream().map(DemandeMapper::toDomain).collect(Collectors.toList()); }
    @Override public List<Demande> findByFournisseurUserId(Long userId) { return jpaRepository.findByFournisseurUserId(userId).stream().map(DemandeMapper::toDomain).collect(Collectors.toList()); }
    @Override public List<Demande> findByAppelOffreId(Long id) { return jpaRepository.findByAppelOffreId(id).stream().map(DemandeMapper::toDomain).collect(Collectors.toList()); }
    @Override public List<Demande> findByStatus(DemandeStatus s) { return jpaRepository.findByStatus(s.name()).stream().map(DemandeMapper::toDomain).collect(Collectors.toList()); }
    @Override public boolean existsByAppelOffreIdAndFournisseurUserId(Long aoId, Long fuId) { return jpaRepository.existsByAppelOffreIdAndFournisseurUserId(aoId, fuId); }
    @Override public long count() { return jpaRepository.count(); }
}
