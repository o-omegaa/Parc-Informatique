package com.supplierportal.domain.facture;
import java.util.List;
import java.util.Optional;
public interface FactureRepository {
    Facture save(Facture facture);
    Optional<Facture> findById(Long id);
    List<Facture> findAll();
    List<Facture> findByFournisseurUserId(Long userId);
    List<Facture> findByDemandeId(Long demandeId);
    List<Facture> findByStatus(FactureStatus status);
    long count();
}
