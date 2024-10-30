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
    private final FormateurRepository formateurRepository;

    @Override
    public void validateForCreate(FormateurDTO formateur) {
        validateEmail(formateur.getEmail());
        validateSpecialite(formateur.getSpecialite());
        validateNomPrenom(formateur);
    }

    @Override
    public void validateForUpdate(Long id, FormateurDTO formateur) {
        formateurRepository.findByEmail(formateur.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ValidationException("Email already exists: " + formateur.getEmail());
                    }
                });
    }

    private void validateEmail(String email) {
        if (formateurRepository.existsByEmail(email)) {
            throw new ValidationException("Email already exists: " + email);
        }
    }

    private void validateSpecialite(String specialite) {
        if (specialite == null || specialite.trim().isEmpty()) {
            throw new ValidationException("La spécialité est obligatoire");
        }
    }

    private void validateNomPrenom(FormateurDTO formateur) {
        if (formateur.getNom() == null || formateur.getNom().trim().isEmpty()) {
            throw new ValidationException("Le nom est obligatoire");
        }
        if (formateur.getPrenom() == null || formateur.getPrenom().trim().isEmpty()) {
            throw new ValidationException("Le prénom est obligatoire");
        }
    }
}