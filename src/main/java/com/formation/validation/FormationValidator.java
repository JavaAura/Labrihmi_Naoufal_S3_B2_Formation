package com.formation.validation;

import com.formation.dto.FormationDTO;
import com.formation.models.FormationStatus;
import com.formation.repositories.FormateurRepository;
import com.formation.validation.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FormationValidator {
    private final FormateurRepository formateurRepository;

    public void validateForCreate(FormationDTO formation) {
        validateCommon(formation);
        validateInitialStatus(formation);
    }

    public void validateForUpdate(Long id, FormationDTO formation) {
        if (id == null) {
            throw new ValidationException("L'ID de la formation est obligatoire pour la mise à jour");
        }
        validateCommon(formation);
    }

    private void validateCommon(FormationDTO formation) {
        validateTitre(formation);
        validateNiveau(formation);
        validatePrerequis(formation);
        validateFormateur(formation);
        validateFormationDates(formation);
        validateCapacities(formation);
    }

    public void validateFormationDates(FormationDTO formation) {
        if (formation.getDateDebut() == null) {
            throw new ValidationException("La date de début est obligatoire");
        }
        if (formation.getDateFin() == null) {
            throw new ValidationException("La date de fin est obligatoire");
        }
        if (formation.getDateDebut().isAfter(formation.getDateFin())) {
            throw new ValidationException("La date de début ne peut pas être après la date de fin");
        }
        if (formation.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new ValidationException("La date de début ne peut pas être dans le passé");
        }
    }

    public void validateCapacities(FormationDTO formation) {
        if (formation.getCapaciteMin() <= 0) {
            throw new ValidationException("La capacité minimale doit être supérieure à 0");
        }
        if (formation.getCapaciteMax() <= 0) {
            throw new ValidationException("La capacité maximale doit être supérieure à 0");
        }
        if (formation.getCapaciteMin() > formation.getCapaciteMax()) {
            throw new ValidationException("La capacité minimale ne peut pas être supérieure à la capacité maximale");
        }
    }

    private void validateInitialStatus(FormationDTO formation) {
        if (formation.getStatut() != FormationStatus.PLANIFIEE) {
            throw new ValidationException("Une nouvelle formation doit avoir le statut PLANIFIEE");
        }
    }

    public void validateStatusTransition(FormationStatus currentStatus, FormationStatus newStatus) {
        if (currentStatus == FormationStatus.TERMINEE || currentStatus == FormationStatus.ANNULEE) {
            throw new ValidationException("Impossible de modifier une formation terminée ou annulée");
        }

        if (currentStatus == FormationStatus.EN_COURS && newStatus == FormationStatus.PLANIFIEE) {
            throw new ValidationException("Impossible de remettre en planification une formation en cours");
        }

        if (currentStatus == FormationStatus.PLANIFIEE && newStatus == FormationStatus.TERMINEE) {
            throw new ValidationException("Une formation planifiée doit d'abord passer en cours avant d'être terminée");
        }
    }

    private void validateTitre(FormationDTO formation) {
        if (formation.getTitre() == null || formation.getTitre().trim().isEmpty()) {
            throw new ValidationException("Le titre est obligatoire");
        }
        if (formation.getTitre().trim().length() < 3) {
            throw new ValidationException("Le titre doit contenir au moins 3 caractères");
        }
    }

    private void validateNiveau(FormationDTO formation) {
        if (formation.getNiveau() == null || formation.getNiveau().trim().isEmpty()) {
            throw new ValidationException("Le niveau est obligatoire");
        }
    }

    private void validatePrerequis(FormationDTO formation) {
        if (formation.getPrerequis() == null || formation.getPrerequis().trim().isEmpty()) {
            throw new ValidationException("Les prérequis sont obligatoires");
        }
    }

    private void validateFormateur(FormationDTO formation) {
        if (formation.getFormateurId() == null) {
            throw new ValidationException("Le formateur est obligatoire");
        }
        if (!formateurRepository.existsById(formation.getFormateurId())) {
            throw new ValidationException("Le formateur spécifié n'existe pas");
        }
    }
}