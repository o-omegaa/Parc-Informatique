package com.supplierportal.infrastructure.web.controller;

import com.supplierportal.application.appeloffre.service.AppelOffreService;
import com.supplierportal.domain.appeloffre.AppelOffre;
import com.supplierportal.domain.user.UserRepository;
import com.supplierportal.infrastructure.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appels-offres")
@RequiredArgsConstructor
public class AppelOffreController {

    private final AppelOffreService appelOffreService;
    private final CurrentUserResolver currentUserResolver;

    record AppelOffreRequest(String titre, String description, String categorie, BigDecimal budgetEstime, String dateCloture) {}

    @PostMapping
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<AppelOffre> create(@RequestBody AppelOffreRequest request, Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        AppelOffre ao = appelOffreService.create(request.titre(), request.description(), request.categorie(),
                request.budgetEstime(), LocalDateTime.parse(request.dateCloture(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ao);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<AppelOffre> update(@PathVariable Long id, @RequestBody AppelOffreRequest request, Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        AppelOffre ao = appelOffreService.update(id, request.titre(), request.description(), request.categorie(),
                request.budgetEstime(), LocalDateTime.parse(request.dateCloture(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), userId);
        return ResponseEntity.ok(ao);
    }

    @PutMapping("/{id}/publier")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<AppelOffre> publier(@PathVariable Long id, Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        return ResponseEntity.ok(appelOffreService.publier(id, userId));
    }

    @PutMapping("/{id}/clore")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<AppelOffre> clore(@PathVariable Long id, Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        return ResponseEntity.ok(appelOffreService.clore(id, userId));
    }

    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<AppelOffre> annuler(@PathVariable Long id, Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        return ResponseEntity.ok(appelOffreService.annuler(id, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        appelOffreService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('SERVICE_ACHAT') or hasRole('ADMIN')")
    public ResponseEntity<List<AppelOffre>> getAll() {
        return ResponseEntity.ok(appelOffreService.findAll());
    }

    @GetMapping("/mes-appels")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<List<AppelOffre>> getMesAppels(Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        return ResponseEntity.ok(appelOffreService.findByPublisher(userId));
    }

    @GetMapping("/publies")
    public ResponseEntity<List<AppelOffre>> getPublies() {
        return ResponseEntity.ok(appelOffreService.findPublies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppelOffre> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appelOffreService.findById(id));
    }
}
