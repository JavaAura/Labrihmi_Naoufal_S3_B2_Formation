package com.formation.validation;

import com.formation.dto.FormateurDTO;
import com.formation.repositories.FormateurRepository;
import com.formation.validation.base.EntityValidator;
import com.formation.validation.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormateurValidator implements EntityValidator<FormateurDTO> {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String NAME_REGEX = "^[A-Za-z\\s-]{2,50}$";
    private final FormateurRepository formateurRepository;

    @Override
    public void validateForCreate(FormateurDTO formateur) {
        validateCommon(formateur);
        validateUniqueEmail(formateur.getEmail(), null);
    }

    @Override
    public void validateForUpdate(Long id, FormateurDTO formateur) {
        if (id == null) {
            throw new ValidationException("L'ID du formateur est obligatoire pour la mise à jour");
        }
        validateCommon(formateur);
        validateUniqueEmail(formateur.getEmail(), id);
    }

    private void validateCommon(FormateurDTO formateur) {
        validateNomPrenom(formateur);
        validateEmail(formateur.getEmail());
        validateSpecialite(formateur.getSpecialite());
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("L'email est obligatoire");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new ValidationException("Format d'email invalide");
        }
    }

    private void validateUniqueEmail(String email, Long excludeId) {
        formateurRepository.findByEmail(email)
                .ifPresent(existing -> {
                    if (excludeId == null || !existing.getId().equals(excludeId)) {
                        throw new ValidationException("Cet email est déjà utilisé");
                    }
                });
    }

    private void validateSpecialite(String specialite) {
        if (specialite == null || specialite.trim().isEmpty()) {
            throw new ValidationException("La spécialité est obligatoire");
        }
        if (specialite.trim().length() < 2 || specialite.trim().length() > 50) {
            throw new ValidationException("La spécialité doit contenir entre 2 et 50 caractères");
        }
    }

    private void validateNomPrenom(FormateurDTO formateur) {
        if (formateur.getNom() == null || formateur.getNom().trim().isEmpty()) {
            throw new ValidationException("Le nom est obligatoire");
        }
        if (!formateur.getNom().matches(NAME_REGEX)) {
            throw new ValidationException("Le nom doit contenir entre 2 et 50 caractères alphabétiques");
        }

        if (formateur.getPrenom() == null || formateur.getPrenom().trim().isEmpty()) {
            throw new ValidationException("Le prénom est obligatoire");
        }
        if (!formateur.getPrenom().matches(NAME_REGEX)) {
            throw new ValidationException("Le prénom doit contenir entre 2 et 50 caractères alphabétiques");
        }
    }
}