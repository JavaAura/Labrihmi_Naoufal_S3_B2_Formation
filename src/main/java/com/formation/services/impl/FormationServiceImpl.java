package com.formation.services.impl;

import com.formation.dto.FormationDTO;
import com.formation.exceptions.ResourceNotFoundException;
import com.formation.models.Apprenant;
import com.formation.models.Formation;

import com.formation.models.FormationStatus;
import com.formation.repositories.ApprenantRepository;
import com.formation.repositories.FormationRepository;
import com.formation.repositories.FormateurRepository;
import com.formation.services.interfaces.IFormationService;
import com.formation.utils.FormationMapper;
import com.formation.validation.FormationValidator;
import com.formation.validation.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FormationServiceImpl implements IFormationService {
    private static final Logger logger = LoggerFactory.getLogger(FormationServiceImpl.class);
    private static final String FORMATION_NOT_FOUND = "Formation not found with id: ";

    private final FormationRepository formationRepository;
    private final ApprenantRepository apprenantRepository;
    private final FormateurRepository formateurRepository;
    private final FormationMapper formationMapper;
    private final FormationValidator formationValidator;

    @Override
    @Transactional
    public FormationDTO save(FormationDTO formationDTO) {
        logger.info("Saving new formation: {}", formationDTO.getTitre());
        try {
            formationValidator.validateForCreate(formationDTO);
            Formation formation = formationMapper.toEntity(formationDTO);
            Formation savedFormation = formationRepository.save(formation);
            return formationMapper.toDTO(savedFormation);
        } catch (Exception e) {
            logger.error("Error while saving formation", e);
            throw new ValidationException("Erreur lors de la sauvegarde de la formation", e);
        }
    }

    @Override
    public FormationDTO update(Long id, FormationDTO formationDTO) {
        logger.info("Updating formation with id: {}", id);
        formationValidator.validateForUpdate(id, formationDTO);
        return formationRepository.findById(id)
                .map(existingFormation -> {
                    Formation formation = formationMapper.toEntity(formationDTO);
                    formation.setId(id);
                    return formationMapper.toDTO(formationRepository.save(formation));
                })
                .orElseThrow(() -> new ResourceNotFoundException(FORMATION_NOT_FOUND + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        logger.info("Deleting formation with id: {}", id);
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FORMATION_NOT_FOUND + id));

        // Clear formateur if any
        if (formation.getFormateur() != null) {
            formation.setFormateur(null);
        }

        // Clear apprenants
        formation.getApprenants().clear();
        formationRepository.save(formation);
        formationRepository.flush();

        formationRepository.delete(formation);
    }

    @Override
    public Optional<FormationDTO> findById(Long id) {
        return formationRepository.findById(id)
                .map(formationMapper::toDTO);
    }

    @Override
    public List<FormationDTO> findAll() {
        return formationRepository.findAll().stream()
                .map(formationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FormationDTO> findAll(Pageable pageable) {
        return formationRepository.findAll(pageable)
                .map(formationMapper::toDTO);
    }

    @Override
    public List<FormationDTO> findByStatut(FormationStatus statut) {
        return formationRepository.findByStatut(statut).stream()
                .map(formationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FormationDTO> findBetweenDates(LocalDateTime debut, LocalDateTime fin) {
        return formationRepository.findFormationsBetweenDates(debut, fin).stream()
                .map(formationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FormationDTO> findByFormateurId(Long formateurId) {
        return formationRepository.findByFormateurId(formateurId).stream()
                .map(formationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FormationDTO> findWithAvailablePlaces() {
        return formationRepository.findFormationsWithAvailablePlaces().stream()
                .map(formationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean addApprenantToFormation(Long formationId, Long apprenantId) {
        logger.info("Adding apprenant {} to formation {}", apprenantId, formationId);
        try {
            Formation formation = formationRepository.findById(formationId)
                    .orElseThrow(() -> new ResourceNotFoundException(FORMATION_NOT_FOUND + formationId));

            Apprenant apprenant = apprenantRepository.findById(apprenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Apprenant", "id", apprenantId));

            formationValidator.validateAddApprenant(formation, apprenant);

            formation.getApprenants().add(apprenant);
            formationRepository.save(formation);
            return true;
        } catch (ResourceNotFoundException | ValidationException e) {
            logger.error("Error in apprenant assignment", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while adding apprenant to formation", e);
            throw new ValidationException("Erreur lors de l'ajout de l'apprenant à la formation", e);
        }
    }

    @Override
    @Transactional
    public boolean removeApprenantFromFormation(Long formationId, Long apprenantId) {
        logger.info("Removing apprenant {} from formation {}", apprenantId, formationId);
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException(FORMATION_NOT_FOUND + formationId));

        if (formation.getStatut() != FormationStatus.PLANIFIEE) {
            throw new ValidationException("Les apprenants ne peuvent être retirés que des formations planifiées");
        }

        Apprenant apprenant = apprenantRepository.findById(apprenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Apprenant not found with id: " + apprenantId));

        boolean removed = formation.getApprenants().remove(apprenant);
        if (removed) {
            formationRepository.save(formation);
        }
        return removed;
    }

    @Override
    @Transactional
    public void updateStatus(Long id, FormationStatus newStatus) {
        logger.info("Updating formation {} status to {}", id, newStatus);
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FORMATION_NOT_FOUND + id));
        formationValidator.validateStatusTransition(formation.getStatut(), newStatus);
        formation.setStatut(newStatus);
        formationRepository.save(formation);
    }

    @Override
    public List<FormationDTO> findPlannedFormationsByNiveau(String niveau) {
        return formationRepository.findPlannedFormationsByNiveau(niveau).stream()
                .map(formationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FormationDTO> searchByTitre(String titre, Pageable pageable) {
        logger.info("Searching formations by titre containing: {}", titre);
        return formationRepository.findByTitreContaining(titre, pageable)
                .map(formationMapper::toDTO);
    }

    @Override
    public boolean isFormationFull(Long formationId) {
        logger.info("Checking if formation {} is full", formationId);
        return formationRepository.findById(formationId)
                .map(formation -> formation.getApprenants().size() >= formation.getCapaciteMax())
                .orElseThrow(() -> new ResourceNotFoundException(FORMATION_NOT_FOUND + formationId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormationDTO> findUpcomingFormations(FormationStatus statut, Pageable pageable) {
        logger.info("Finding upcoming formations with status: {}", statut);
        try {
            if (statut == null) {
                throw new ValidationException("Le statut est obligatoire");
            }
            if (pageable == null) {
                throw new ValidationException("Les informations de pagination sont obligatoires");
            }
            Page<Formation> formations = formationRepository.findUpcomingFormationsByStatus(statut, pageable);
            if (formations.isEmpty()) {
                logger.info("No upcoming formations found with status: {}", statut);
            }
            return formations.map(formationMapper::toDTO);
        } catch (ValidationException e) {
            logger.error("Validation error in findUpcomingFormations", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching upcoming formations", e);
            throw new ValidationException("Erreur lors de la récupération des formations à venir", e);
        }
    }
}
