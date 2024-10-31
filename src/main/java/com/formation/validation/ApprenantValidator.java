package com.formation.validation;

import com.formation.dto.ApprenantDTO;
import com.formation.models.Apprenant;
import com.formation.repositories.ApprenantRepository;
import com.formation.validation.base.EntityValidator;
import com.formation.validation.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApprenantValidator implements EntityValidator<ApprenantDTO> {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String NAME_REGEX = "^[A-Za-z\\s-]{2,50}$";
    private static final List<String> VALID_NIVEAUX = Arrays.asList("DEBUTANT", "INTERMEDIAIRE", "AVANCE");

    private final ApprenantRepository apprenantRepository;

    @Override
    public void validateForCreate(ApprenantDTO apprenant) {
        validateCommon(apprenant);
        validateUniqueEmail(apprenant.getEmail(), null);
    }

    @Override
    public void validateForUpdate(Long id, ApprenantDTO apprenant) {
        if (id == null) {
            throw new ValidationException("L'ID de l'apprenant est obligatoire pour la mise à jour");
        }
        validateCommon(apprenant);
        validateUniqueEmail(apprenant.getEmail(), id);
    }

    private void validateCommon(ApprenantDTO apprenant) {
        validateNom(apprenant.getNom());
        validatePrenom(apprenant.getPrenom());
        validateEmail(apprenant.getEmail());
        validateNiveau(apprenant.getNiveau());
    }

    private void validateNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new ValidationException("Le nom est obligatoire");
        }
        if (!nom.matches(NAME_REGEX)) {
            throw new ValidationException("Le nom doit contenir entre 2 et 50 caractères alphabétiques");
        }
    }

    private void validatePrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new ValidationException("Le prénom est obligatoire");
        }
        if (!prenom.matches(NAME_REGEX)) {
            throw new ValidationException("Le prénom doit contenir entre 2 et 50 caractères alphabétiques");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches(EMAIL_REGEX)) {
            throw new ValidationException("Format d'email invalide");
        }
    }

    private void validateUniqueEmail(String email, Long excludeId) {
        Optional<Apprenant> existingApprenant = apprenantRepository.findByEmail(email);
        if (existingApprenant.isPresent() &&
                (excludeId == null || !existingApprenant.get().getId().equals(excludeId))) {
            throw new ValidationException("Cet email est déjà utilisé");
        }
    }

    private void validateNiveau(String niveau) {
        if (niveau == null || !VALID_NIVEAUX.contains(niveau.toUpperCase())) {
            throw new ValidationException("Le niveau doit être DEBUTANT, INTERMEDIAIRE ou AVANCE");
        }
    }
}