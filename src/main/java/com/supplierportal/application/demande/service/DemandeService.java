package com.supplierportal.application.demande.service;

import com.supplierportal.domain.appeloffre.AppelOffre;
import com.supplierportal.domain.appeloffre.AppelOffreRepository;
import com.supplierportal.domain.appeloffre.AppelOffreStatus;
import com.supplierportal.domain.demande.*;
import com.supplierportal.domain.shared.exception.NotFoundException;
import com.supplierportal.domain.shared.exception.ValidationException;
import com.supplierportal.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeService {

    private final DemandeRepository demandeRepository;
    private final AppelOffreRepository appelOffreRepository;
    private final UserRepository userRepository;

    public Demande soumettre(Long appelOffreId, Long fournisseurUserId,
                              String propositionTechnique, BigDecimal montantPropose) {
        AppelOffre ao = appelOffreRepository.findById(appelOffreId)
                .orElseThrow(() -> new NotFoundException("Appel d'offre non trouve."));
        if (ao.getStatus() != AppelOffreStatus.PUBLIE) {
            throw new ValidationException("Cet appel d'offre n'est pas ouvert aux candidatures.");
        }
        if (!ao.isOuvert()) {
            throw new ValidationException("La date de cloture de cet appel d'offre est depassee.");
        }
        if (demandeRepository.existsByAppelOffreIdAndFournisseurUserId(appelOffreId, fournisseurUserId)) {
            throw new ValidationException("Vous avez deja soumis une candidature pour cet appel d'offre.");
        }
        Demande demande = Demande.builder()
                .appelOffreId(appelOffreId)
                .fournisseurUserId(fournisseurUserId)
                .propositionTechnique(propositionTechnique)
                .montantPropose(montantPropose)
                .dateSubmission(LocalDateTime.now())
                .status(DemandeStatus.SOUMISE)
                .createdAt(LocalDateTime.now())
                .build();
        return demandeRepository.save(demande);
    }

    public Demande accepter(Long id, String commentaire) {
        Demande demande = findByIdOrThrow(id);
        demande.accepter(commentaire);
        demande.setUpdatedAt(LocalDateTime.now());
        return demandeRepository.save(demande);
    }

    public Demande rejeter(Long id, String commentaire) {
        Demande demande = findByIdOrThrow(id);
        demande.rejeter(commentaire);
        demande.setUpdatedAt(LocalDateTime.now());
        return demandeRepository.save(demande);
    }

    @Transactional(readOnly = true)
    public List<Demande> findAll() { return demandeRepository.findAll(); }

    @Transactional(readOnly = true)
    public List<Demande> findByFournisseur(Long userId) { return demandeRepository.findByFournisseurUserId(userId); }

    @Transactional(readOnly = true)
    public List<Demande> findByAppelOffre(Long appelOffreId) { return demandeRepository.findByAppelOffreId(appelOffreId); }

    @Transactional(readOnly = true)
    public Demande findById(Long id) { return findByIdOrThrow(id); }

    private Demande findByIdOrThrow(Long id) {
        return demandeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demande non trouvee avec l'ID: " + id));
    }

    public long count() { return demandeRepository.count(); }
    public long countByStatus(DemandeStatus status) { return demandeRepository.findByStatus(status).size(); }
}
