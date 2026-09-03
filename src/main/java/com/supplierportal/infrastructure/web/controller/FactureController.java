package com.supplierportal.infrastructure.web.controller;

import com.supplierportal.application.facture.service.FactureService;
import com.supplierportal.domain.facture.Facture;
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
@RequestMapping("/api/v1/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;
    private final CurrentUserResolver currentUserResolver;

    record FactureRequest(Long demandeId, String referenceFacture, BigDecimal montant, String dateEcheance) {}
    record CommentaireRequest(String commentaire) {}

    @PostMapping
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<Facture> soumettre(@RequestBody FactureRequest request, Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        Facture f = factureService.soumettre(request.demandeId(), userId, request.referenceFacture(),
                request.montant(), LocalDateTime.parse(request.dateEcheance(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return ResponseEntity.status(HttpStatus.CREATED).body(f);
    }

    @GetMapping("/mes-factures")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<List<Facture>> getMesFactures(Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        return ResponseEntity.ok(factureService.findByFournisseur(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('SERVICE_ACHAT') or hasRole('ADMIN')")
    public ResponseEntity<List<Facture>> getAll() {
        return ResponseEntity.ok(factureService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getById(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.findById(id));
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<Facture> valider(@PathVariable Long id, @RequestBody CommentaireRequest request) {
        return ResponseEntity.ok(factureService.valider(id, request.commentaire()));
    }

    @PutMapping("/{id}/marquer-payee")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<Facture> marquerPayee(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.marquerPayee(id));
    }

    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<Facture> rejeter(@PathVariable Long id, @RequestBody CommentaireRequest request) {
        return ResponseEntity.ok(factureService.rejeter(id, request.commentaire()));
    }
}
