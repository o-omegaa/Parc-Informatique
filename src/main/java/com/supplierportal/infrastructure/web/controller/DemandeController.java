package com.supplierportal.infrastructure.web.controller;

import com.supplierportal.application.demande.service.DemandeService;
import com.supplierportal.domain.demande.Demande;
import com.supplierportal.infrastructure.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/demandes")
@RequiredArgsConstructor
public class DemandeController {

    private final DemandeService demandeService;
    private final CurrentUserResolver currentUserResolver;

    record DemandeRequest(Long appelOffreId, String propositionTechnique, BigDecimal montantPropose) {}
    record CommentaireRequest(String commentaire) {}

    @PostMapping
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<Demande> soumettre(@RequestBody DemandeRequest request, Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        Demande d = demandeService.soumettre(request.appelOffreId(), userId, request.propositionTechnique(), request.montantPropose());
        return ResponseEntity.status(HttpStatus.CREATED).body(d);
    }

    @GetMapping("/mes-demandes")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<List<Demande>> getMesDemandes(Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        return ResponseEntity.ok(demandeService.findByFournisseur(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('SERVICE_ACHAT') or hasRole('ADMIN')")
    public ResponseEntity<List<Demande>> getAll() {
        return ResponseEntity.ok(demandeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Demande> getById(@PathVariable Long id) {
        return ResponseEntity.ok(demandeService.findById(id));
    }

    @GetMapping("/appel-offre/{aoId}")
    @PreAuthorize("hasRole('SERVICE_ACHAT') or hasRole('ADMIN')")
    public ResponseEntity<List<Demande>> getByAppelOffre(@PathVariable Long aoId) {
        return ResponseEntity.ok(demandeService.findByAppelOffre(aoId));
    }

    @PutMapping("/{id}/accepter")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<Demande> accepter(@PathVariable Long id, @RequestBody CommentaireRequest request) {
        return ResponseEntity.ok(demandeService.accepter(id, request.commentaire()));
    }

    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<Demande> rejeter(@PathVariable Long id, @RequestBody CommentaireRequest request) {
        return ResponseEntity.ok(demandeService.rejeter(id, request.commentaire()));
    }
}
