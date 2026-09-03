package com.supplierportal.domain.appeloffre;
import java.util.List;
import java.util.Optional;
public interface AppelOffreRepository {
    AppelOffre save(AppelOffre appelOffre);
    Optional<AppelOffre> findById(Long id);
    List<AppelOffre> findAll();
    List<AppelOffre> findByStatus(AppelOffreStatus status);
    List<AppelOffre> findByPublishedByUserId(Long userId);
    void deleteById(Long id);
    long count();
}
