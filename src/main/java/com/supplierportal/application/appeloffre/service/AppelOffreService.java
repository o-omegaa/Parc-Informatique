package com.supplierportal.application.appeloffre.service;

import com.supplierportal.domain.appeloffre.*;
import com.supplierportal.domain.shared.exception.NotFoundException;
import com.supplierportal.domain.shared.exception.ValidationException;
import com.supplierportal.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppelOffreService {

    private final AppelOffreRepository appelOffreRepository;
    private final UserRepository userRepository;

    public AppelOffre create(String titre, String description, String categorie,
                              BigDecimal budgetEstime, LocalDateTime dateCloture, Long userId) {
        AppelOffre ao = AppelOffre.builder()
                .titre(titre)
                .description(description)
                .categorie(AppelOffreCategorie.valueOf(categorie.toUpperCase()))
                .budgetEstime(budgetEstime)
                .dateCloture(dateCloture)
                .datePublication(LocalDateTime.now())
                .status(AppelOffreStatus.BROUILLON)
                .publishedByUserId(userId)
                .createdAt(LocalDateTime.now())
                .build();
        return appelOffreRepository.save(ao);
    }

    public AppelOffre update(Long id, String titre, String description, String categorie,
                              BigDecimal budgetEstime, LocalDateTime dateCloture, Long userId) {
        AppelOffre ao = findByIdOrThrow(id);
        if (!ao.getPublishedByUserId().equals(userId)) {
            throw new ValidationException("Vous n'etes pas autorise a modifier cet appel d'offre.");
        }
        if (ao.getStatus() != AppelOffreStatus.BROUILLON) {
            throw new ValidationException("Seul un brouillon peut etre modifie.");
        }
        ao.setTitre(titre);
        ao.setDescription(description);
        ao.setCategorie(AppelOffreCategorie.valueOf(categorie.toUpperCase()));
        ao.setBudgetEstime(budgetEstime);
        ao.setDateCloture(dateCloture);
        ao.setUpdatedAt(LocalDateTime.now());
        return appelOffreRepository.save(ao);
    }

    public AppelOffre publier(Long id, Long userId) {
        AppelOffre ao = findByIdOrThrow(id);
        if (!ao.getPublishedByUserId().equals(userId)) {
            throw new ValidationException("Vous n'etes pas autorise a publier cet appel d'offre.");
        }
        ao.publier();
        ao.setUpdatedAt(LocalDateTime.now());
        return appelOffreRepository.save(ao);
    }

    public AppelOffre clore(Long id, Long userId) {
        AppelOffre ao = findByIdOrThrow(id);
        if (!ao.getPublishedByUserId().equals(userId)) {
            throw new ValidationException("Vous n'etes pas autorise a clore cet appel d'offre.");
        }
        ao.clore();
        ao.setUpdatedAt(LocalDateTime.now());
        return appelOffreRepository.save(ao);
    }

    public AppelOffre annuler(Long id, Long userId) {
        AppelOffre ao = findByIdOrThrow(id);
        if (!ao.getPublishedByUserId().equals(userId)) {
            throw new ValidationException("Vous n'etes pas autorise a annuler cet appel d'offre.");
        }
        ao.annuler();
        ao.setUpdatedAt(LocalDateTime.now());
        return appelOffreRepository.save(ao);
    }

    public void delete(Long id, Long userId) {
        AppelOffre ao = findByIdOrThrow(id);
        if (!ao.getPublishedByUserId().equals(userId)) {
            throw new ValidationException("Vous n'etes pas autorise a supprimer cet appel d'offre.");
        }
        if (ao.getStatus() != AppelOffreStatus.BROUILLON) {
            throw new ValidationException("Seul un brouillon peut etre supprime.");
        }
        appelOffreRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AppelOffre> findAll() {
        return appelOffreRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AppelOffre> findPublies() {
        return appelOffreRepository.findByStatus(AppelOffreStatus.PUBLIE);
    }

    @Transactional(readOnly = true)
    public List<AppelOffre> findByPublisher(Long userId) {
        return appelOffreRepository.findByPublishedByUserId(userId);
    }

    @Transactional(readOnly = true)
    public AppelOffre findById(Long id) {
        return findByIdOrThrow(id);
    }

    private AppelOffre findByIdOrThrow(Long id) {
        return appelOffreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appel d'offre non trouve avec l'ID: " + id));
    }

    public long count() { return appelOffreRepository.count(); }
    public long countByStatus(AppelOffreStatus status) { return appelOffreRepository.findByStatus(status).size(); }
}
