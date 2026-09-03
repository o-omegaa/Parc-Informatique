package com.supplierportal.domain.demande;
import java.util.List;
import java.util.Optional;
public interface DemandeRepository {
    Demande save(Demande demande);
    Optional<Demande> findById(Long id);
    List<Demande> findAll();
    List<Demande> findByFournisseurUserId(Long userId);
    List<Demande> findByAppelOffreId(Long appelOffreId);
    List<Demande> findByStatus(DemandeStatus status);
    boolean existsByAppelOffreIdAndFournisseurUserId(Long appelOffreId, Long fournisseurUserId);
    long count();
}
