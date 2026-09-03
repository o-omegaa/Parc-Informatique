package com.supplierportal.application.facture.service;

import com.supplierportal.domain.demande.Demande;
import com.supplierportal.domain.demande.DemandeRepository;
import com.supplierportal.domain.demande.DemandeStatus;
import com.supplierportal.domain.facture.*;
import com.supplierportal.domain.shared.exception.NotFoundException;
import com.supplierportal.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FactureService {

    private final FactureRepository factureRepository;
    private final DemandeRepository demandeRepository;

    public Facture soumettre(Long demandeId, Long fournisseurUserId, String referenceFacture,
                              BigDecimal montant, LocalDateTime dateEcheance) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new NotFoundException("Demande non trouvee."));
        if (!demande.getFournisseurUserId().equals(fournisseurUserId)) {
            throw new ValidationException("Cette demande ne vous appartient pas.");
        }
        if (demande.getStatus() != DemandeStatus.ACCEPTEE) {
            throw new ValidationException("Vous ne pouvez soumettre une facture que pour une demande acceptee.");
        }
        Facture facture = Facture.builder()
                .demandeId(demandeId)
                .fournisseurUserId(fournisseurUserId)
                .referenceFacture(referenceFacture)
                .montant(montant)
                .dateEmission(LocalDateTime.now())
                .dateEcheance(dateEcheance)
                .status(FactureStatus.EN_ATTENTE)
                .createdAt(LocalDateTime.now())
                .build();
        return factureRepository.save(facture);
    }

    public Facture valider(Long id, String commentaire) {
        Facture f = findByIdOrThrow(id);
        f.valider(commentaire);
        f.setUpdatedAt(LocalDateTime.now());
        return factureRepository.save(f);
    }

    public Facture marquerPayee(Long id) {
        Facture f = findByIdOrThrow(id);
        f.marquerPayee();
        f.setUpdatedAt(LocalDateTime.now());
        return factureRepository.save(f);
    }

    public Facture rejeter(Long id, String commentaire) {
        Facture f = findByIdOrThrow(id);
        f.rejeter(commentaire);
        f.setUpdatedAt(LocalDateTime.now());
        return factureRepository.save(f);
    }

    @Transactional(readOnly = true)
    public List<Facture> findAll() { return factureRepository.findAll(); }

    @Transactional(readOnly = true)
    public List<Facture> findByFournisseur(Long userId) { return factureRepository.findByFournisseurUserId(userId); }

    @Transactional(readOnly = true)
    public Facture findById(Long id) { return findByIdOrThrow(id); }

    private Facture findByIdOrThrow(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Facture non trouvee avec l'ID: " + id));
    }

    public long count() { return factureRepository.count(); }
    public long countByStatus(FactureStatus status) { return factureRepository.findByStatus(status).size(); }
}
