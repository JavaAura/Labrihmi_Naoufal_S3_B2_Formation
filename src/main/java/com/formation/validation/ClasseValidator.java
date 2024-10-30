package com.formation.validation;

import com.formation.dto.ClasseDTO;
import com.formation.repositories.ApprenantRepository;
import com.formation.repositories.ClasseRepository;
import com.formation.repositories.FormateurRepository;
import com.formation.validation.base.EntityValidator;
import com.formation.validation.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ClasseValidator implements EntityValidator<ClasseDTO> {
    private final ClasseRepository classeRepository;
    private final ApprenantRepository apprenantRepository;
    private final FormateurRepository formateurRepository;

    @Override
    public void validateForCreate(ClasseDTO classe) {
        validateCommon(classe);
        validateUniqueNumSalle(classe.getNumSalle(), null);
        if (classe.getApprenantIds() != null || classe.getFormateurIds() != null) {
            throw new ValidationException(
                    "Une nouvelle classe ne peut pas avoir d'apprenants ou de formateurs à la création");
        }
    }

    @Override
    public void validateForUpdate(Long id, ClasseDTO classe) {
        if (id == null) {
            throw new ValidationException("L'ID de la classe est obligatoire pour la mise à jour");
        }
        validateCommon(classe);
        validateUniqueNumSalle(classe.getNumSalle(), id);
        if (classe.getApprenantIds() != null) {
            validateExistingApprenants(classe.getApprenantIds());
        }
        if (classe.getFormateurIds() != null) {
            validateExistingFormateurs(classe.getFormateurIds());
        }
    }

    private void validateCommon(ClasseDTO classe) {
        validateNom(classe);
        validateNumSalle(classe);
    }

    private void validateNom(ClasseDTO classe) {
        if (classe.getNom() == null || classe.getNom().trim().isEmpty()) {
            throw new ValidationException("Le nom de la classe est obligatoire");
        }
        String nom = classe.getNom().trim();
        if (nom.length() < 2) {
            throw new ValidationException("Le nom de la classe doit contenir au moins 2 caractères");
        }
        if (nom.length() > 50) {
            throw new ValidationException("Le nom de la classe ne peut pas dépasser 50 caractères");
        }
    }

    private void validateNumSalle(ClasseDTO classe) {
        if (classe.getNumSalle() == null || classe.getNumSalle().trim().isEmpty()) {
            throw new ValidationException("Le numéro de salle est obligatoire");
        }

        String numSalle = classe.getNumSalle().trim();
        try {
            int numSalleInt = Integer.parseInt(numSalle);
            if (numSalleInt <= 0) {
                throw new ValidationException("Le numéro de salle doit être positif");
            }
            if (numSalleInt > 999) {
                throw new ValidationException("Le numéro de salle ne peut pas dépasser 999");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Le numéro de salle doit être un nombre valide");
        }
    }

    private void validateUniqueNumSalle(String numSalle, Long excludeId) {
        if (classeRepository.existsByNumSalle(numSalle)) {
            if (excludeId == null || !classeRepository.findById(excludeId)
                    .map(classe -> classe.getNumSalle().equals(numSalle))
                    .orElse(false)) {
                throw new ValidationException("Le numéro de salle " + numSalle + " est déjà utilisé");
            }
        }
    }

    private void validateExistingApprenants(Set<Long> apprenantIds) {
        for (Long apprenantId : apprenantIds) {
            if (!apprenantRepository.existsById(apprenantId)) {
                throw new ValidationException("L'apprenant avec l'ID " + apprenantId + " n'existe pas");
            }
            if (apprenantRepository.findById(apprenantId)
                    .map(apprenant -> apprenant.getClasse() != null)
                    .orElse(false)) {
                throw new ValidationException(
                        "L'apprenant avec l'ID " + apprenantId + " est déjà assigné à une classe");
            }
        }
    }

    private void validateExistingFormateurs(Set<Long> formateurIds) {
        for (Long formateurId : formateurIds) {
            if (!formateurRepository.existsById(formateurId)) {
                throw new ValidationException("Le formateur avec l'ID " + formateurId + " n'existe pas");
            }
            if (formateurRepository.findById(formateurId)
                    .map(formateur -> formateur.getClasse() != null)
                    .orElse(false)) {
                throw new ValidationException(
                        "Le formateur avec l'ID " + formateurId + " est déjà assigné à une classe");
            }
        }
    }
}