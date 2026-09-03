package com.supplierportal.infrastructure.persistence.adapter;

import com.supplierportal.domain.appeloffre.*;
import com.supplierportal.infrastructure.persistence.jpa.repository.AppelOffreJpaRepository;
import com.supplierportal.infrastructure.persistence.mapper.AppelOffreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AppelOffreRepositoryImpl implements AppelOffreRepository {
    private final AppelOffreJpaRepository jpaRepository;

    @Override public AppelOffre save(AppelOffre ao) { return AppelOffreMapper.toDomain(jpaRepository.save(AppelOffreMapper.toEntity(ao))); }
    @Override public Optional<AppelOffre> findById(Long id) { return jpaRepository.findById(id).map(AppelOffreMapper::toDomain); }
    @Override public List<AppelOffre> findAll() { return jpaRepository.findAll().stream().map(AppelOffreMapper::toDomain).collect(Collectors.toList()); }
    @Override public List<AppelOffre> findByStatus(AppelOffreStatus status) { return jpaRepository.findByStatus(status.name()).stream().map(AppelOffreMapper::toDomain).collect(Collectors.toList()); }
    @Override public List<AppelOffre> findByPublishedByUserId(Long userId) { return jpaRepository.findByPublishedByUserId(userId).stream().map(AppelOffreMapper::toDomain).collect(Collectors.toList()); }
    @Override public void deleteById(Long id) { jpaRepository.deleteById(id); }
    @Override public long count() { return jpaRepository.count(); }
}
