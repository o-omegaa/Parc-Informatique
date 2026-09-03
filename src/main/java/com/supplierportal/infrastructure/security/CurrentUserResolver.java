package com.supplierportal.infrastructure.security;

import com.supplierportal.domain.shared.exception.NotFoundException;
import com.supplierportal.domain.user.User;
import com.supplierportal.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * Spring Security ne connait le principal authentifie que par son
 * username. Cette classe le retrouve dans la base pour obtenir son
 * vrai id numerique - necessaire pour l'audit (qui a fait quoi) et
 * pour les verifications de propriete (ce fournisseur m'appartient-il).
 */
@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

    private final UserRepository userRepository;

    public User resolve(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException(
                        "Utilisateur authentifie introuvable: " + principal.getName()));
    }

    public Long resolveId(Principal principal) {
        return resolve(principal).getId();
    }
}