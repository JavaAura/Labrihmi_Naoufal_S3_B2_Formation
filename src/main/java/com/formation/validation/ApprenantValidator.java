package com.formation.validation;

import com.formation.dto.ApprenantDTO;
import com.formation.repositories.ApprenantRepository;
import com.formation.validation.base.EntityValidator;
import com.formation.validation.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApprenantValidator implements EntityValidator<ApprenantDTO> {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private final ApprenantRepository apprenantRepository;

    private void validateEmailFormat(String email) {
        if (email == null || !email.matches(EMAIL_REGEX)) {
            throw new ValidationException("Format d'email invalide");
        }
    }

    @Override
    public void validateForCreate(ApprenantDTO apprenant) {
        validateEmailFormat(apprenant.getEmail());
        validateEmail(apprenant.getEmail());
        validateNom(apprenant.getNom());
        validatePrenom(apprenant.getPrenom());
        validateNiveau(apprenant.getNiveau());
    }

    private void validateNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new ValidationException("Le nom est obligatoire");
        }
    }

    private void validatePrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new ValidationException("Le prénom est obligatoire");
        }
    }

    @Override
    public void validateForUpdate(Long id, ApprenantDTO apprenant) {
        validateNiveau(apprenant.getNiveau());
        apprenantRepository.findByEmail(apprenant.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ValidationException("Email already exists: " + apprenant.getEmail());
                    }
                });
    }

    private void validateEmail(String email) {
        if (apprenantRepository.existsByEmail(email)) {
            throw new ValidationException("Email already exists: " + email);
        }
    }

    private void validateNiveau(String niveau) {
        if (niveau == null || niveau.trim().isEmpty()) {
            throw new ValidationException("Le niveau est obligatoire");
        }
    }
}