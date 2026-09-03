package com.supplierportal.infrastructure.web.controller;

import com.supplierportal.application.appeloffre.service.AppelOffreService;
import com.supplierportal.application.demande.service.DemandeService;
import com.supplierportal.application.facture.service.FactureService;
import com.supplierportal.domain.appeloffre.AppelOffre;
import com.supplierportal.domain.demande.Demande;
import com.supplierportal.domain.demande.DemandeStatus;
import com.supplierportal.domain.facture.Facture;
import com.supplierportal.domain.facture.FactureStatus;
import com.supplierportal.domain.user.Role;
import com.supplierportal.domain.user.User;
import com.supplierportal.domain.user.UserRepository;
import com.supplierportal.domain.user.UserStatus;
import com.supplierportal.infrastructure.security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AppelOffreService appelOffreService;
    private final DemandeService demandeService;
    private final FactureService factureService;
    private final UserRepository userRepository;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();
        long totalFournisseurs = users.stream().filter(u -> u.getRole() == Role.SUPPLIER && u.getStatus() == UserStatus.ACTIVE).count();
        long pendingValidation = users.stream().filter(u -> u.getRole() == Role.SUPPLIER && u.getStatus() == UserStatus.DISABLED).count();

        long totalAppelsOffres = appelOffreService.count();
        long totalDemandes = demandeService.count();
        long facturesEnAttente = factureService.countByStatus(FactureStatus.EN_ATTENTE);

        return ResponseEntity.ok(Map.of(
                "totalUsers", totalUsers,
                "totalFournisseurs", totalFournisseurs,
                "pendingValidation", pendingValidation,
                "totalAppelsOffres", totalAppelsOffres,
                "totalDemandes", totalDemandes,
                "facturesEnAttente", facturesEnAttente
        ));
    }

    @GetMapping("/service-achat")
    @PreAuthorize("hasRole('SERVICE_ACHAT')")
    public ResponseEntity<Map<String, Object>> getServiceAchatStats(Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        List<AppelOffre> mesAppels = appelOffreService.findByPublisher(userId);
        long mesAppelsOffres = mesAppels.size();
        
        List<Demande> toutesDemandes = demandeService.findAll();
        List<Long> mesAppelsIds = mesAppels.stream().map(AppelOffre::getId).collect(Collectors.toList());
        long candidaturesRecues = toutesDemandes.stream().filter(d -> mesAppelsIds.contains(d.getAppelOffreId())).count();
        long demandesEnExamen = toutesDemandes.stream().filter(d -> mesAppelsIds.contains(d.getAppelOffreId()) && d.getStatus() == DemandeStatus.EN_EXAMEN).count();
        
        long facturesEnAttente = factureService.countByStatus(FactureStatus.EN_ATTENTE);

        return ResponseEntity.ok(Map.of(
                "mesAppelsOffres", mesAppelsOffres,
                "candidaturesRecues", candidaturesRecues,
                "demandesEnExamen", demandesEnExamen,
                "facturesEnAttente", facturesEnAttente
        ));
    }

    @GetMapping("/fournisseur")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<Map<String, Object>> getFournisseurStats(Principal principal) {
        Long userId = currentUserResolver.resolveId(principal);
        List<Demande> mesDemandesList = demandeService.findByFournisseur(userId);
        long mesDemandes = mesDemandesList.size();
        long demandesAcceptees = mesDemandesList.stream().filter(d -> d.getStatus() == DemandeStatus.ACCEPTEE).count();
        long demandesRejetees = mesDemandesList.stream().filter(d -> d.getStatus() == DemandeStatus.REJETEE).count();
        
        long mesFactures = factureService.findByFournisseur(userId).size();

        return ResponseEntity.ok(Map.of(
                "mesDemandes", mesDemandes,
                "demandesAcceptees", demandesAcceptees,
                "demandesRejetees", demandesRejetees,
                "mesFactures", mesFactures
        ));
    }
}
