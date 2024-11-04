package com.formation.services.impl;

import com.formation.dto.ClasseDTO;
import com.formation.exceptions.ResourceNotFoundException;
import com.formation.models.Classe;
import com.formation.models.Apprenant;
import com.formation.models.Formateur;
import com.formation.repositories.ClasseRepository;
import com.formation.repositories.ApprenantRepository;
import com.formation.repositories.FormateurRepository;
import com.formation.services.interfaces.IClasseService;
import com.formation.utils.ClasseMapper;
import com.formation.validation.ClasseValidator;
import com.formation.validation.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClasseServiceImpl implements IClasseService {
    private static final Logger logger = LoggerFactory.getLogger(ClasseServiceImpl.class);
    private static final String CLASSE_NOT_FOUND_MESSAGE = "Classe not found with id: ";
    private static final String APPRENANT_NOT_FOUND_MESSAGE = "Apprenant not found with id: ";
    private static final String FORMATEUR_NOT_FOUND_MESSAGE = "Formateur not found with id: ";
    private static final String ERROR_REMOVING_APPRENANT = "Error while removing apprenant from classe: {}";
    private static final String ERROR_ASSIGNING_FORMATEUR = "Error while assigning formateur to classe: {}";

    private final ClasseRepository classeRepository;
    private final ApprenantRepository apprenantRepository;
    private final FormateurRepository formateurRepository;
    private final ClasseMapper classeMapper;
    private final ClasseValidator classeValidator;

    @Override
    @Transactional
    public ClasseDTO save(ClasseDTO classeDTO) {
        try {
            logger.info("Saving new classe: {}", classeDTO.getNom());
            classeValidator.validateForCreate(classeDTO);
            Classe classe = classeMapper.toEntity(classeDTO);
            return classeMapper.toDTO(classeRepository.save(classe));
        } catch (Exception e) {
            String errorMessage = String.format("Failed to save classe with name '%s'", classeDTO.getNom());
            logger.error(errorMessage, e);
            throw new ValidationException(errorMessage);
        }
    }

    @Transactional
    public ClasseDTO update(Long id, ClasseDTO classeDTO) {
        try {
            logger.info("Updating classe with id: {}", id);
            classeValidator.validateForUpdate(id, classeDTO);

            return classeRepository.findById(id)
                    .map(existingClasse -> {
                        classeMapper.updateClasseFromDTO(classeDTO, existingClasse);
                        return classeMapper.toDTO(classeRepository.save(existingClasse));
                    })
                    .orElseThrow(() -> new ResourceNotFoundException(CLASSE_NOT_FOUND_MESSAGE + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating classe: {}", e.getMessage(), e);
            throw new ValidationException("Error updating classe", e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        logger.info("Deleting classe with id: {}", id);
        Classe classe = classeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CLASSE_NOT_FOUND_MESSAGE + id));

        // Remove all apprenants from classe
        classe.getApprenants().forEach(apprenant -> {
            apprenant.setClasse(null);
        });
        classe.getApprenants().clear();

        // Remove formateur from classe if any
        if (classe.getFormateur() != null) {
            classe.getFormateur().setClasse(null);
            classe.setFormateur(null);
        }

        classeRepository.save(classe);
        classeRepository.flush();
        classeRepository.delete(classe);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClasseDTO> findById(Long id) {
        return classeRepository.findById(id).map(classeMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClasseDTO> findAll() {
        return classeRepository.findAll().stream()
                .map(classeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClasseDTO> findAll(Pageable pageable) {
        return classeRepository.findAll(pageable).map(classeMapper::toDTO);
    }

    @Override
    public List<ClasseDTO> findByNomContaining(String nom) {
        return classeRepository.findByNomContaining(nom).stream()
                .map(classeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClasseDTO> findAvailableClasses(int maxCapacity) {
        return classeRepository.findAvailableClasses(maxCapacity).stream()
                .map(classeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNumSalle(String numSalle) {
        return classeRepository.existsByNumSalle(numSalle);
    }

    @Override
    @Transactional
    public void assignApprenantToClasse(Long classeId, Long apprenantId) {
        try {
            logger.info("Assigning apprenant {} to classe {}", apprenantId, classeId);
            classeValidator.validateAssignApprenant(classeId, apprenantId);

            Classe classe = classeRepository.findById(classeId)
                    .orElseThrow(() -> new ResourceNotFoundException(CLASSE_NOT_FOUND_MESSAGE + classeId));

            Apprenant apprenant = apprenantRepository.findById(apprenantId)
                    .orElseThrow(() -> new ResourceNotFoundException(APPRENANT_NOT_FOUND_MESSAGE + apprenantId));

            apprenant.setClasse(classe);
            apprenantRepository.save(apprenant);
        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error assigning apprenant to classe: {}", e.getMessage(), e);
            throw new ValidationException("Error assigning apprenant to classe: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeApprenantFromClasse(Long classeId, Long apprenantId) {
        try {
            logger.info("Removing apprenant {} from classe {}", apprenantId, classeId);
            classeValidator.validateRemoveApprenant(classeId, apprenantId);

            Apprenant apprenant = apprenantRepository.findById(apprenantId)
                    .orElseThrow(() -> new ResourceNotFoundException(APPRENANT_NOT_FOUND_MESSAGE + apprenantId));

            if (apprenant.getClasse() == null || !apprenant.getClasse().getId().equals(classeId)) {
                throw new ValidationException("L'apprenant n'est pas assigné à cette classe");
            }

            apprenant.setClasse(null);
            apprenantRepository.save(apprenant);
        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error(ERROR_REMOVING_APPRENANT, e.getMessage(), e);
            throw new ValidationException("Error removing apprenant from classe: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void assignFormateurToClasse(Long classeId, Long formateurId) {
        try {
            logger.info("Assigning formateur {} to classe {}", formateurId, classeId);

            Classe classe = classeRepository.findById(classeId)
                    .orElseThrow(() -> new ResourceNotFoundException(CLASSE_NOT_FOUND_MESSAGE + classeId));

            Formateur formateur = formateurRepository.findById(formateurId)
                    .orElseThrow(() -> new ResourceNotFoundException(FORMATEUR_NOT_FOUND_MESSAGE + formateurId));

            if (formateur.getClasse() != null) {
                throw new ValidationException("Le formateur est déjà assigné à une classe");
            }

            formateur.setClasse(classe);
            formateurRepository.save(formateur);
        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error(ERROR_ASSIGNING_FORMATEUR, e.getMessage(), e);
            throw new ValidationException("Error assigning formateur to classe: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeFormateurFromClasse(Long classeId, Long formateurId) {
        try {
            logger.info("Removing formateur {} from classe {}", formateurId, classeId);
            Formateur formateur = formateurRepository.findById(formateurId)
                    .orElseThrow(() -> new ResourceNotFoundException(FORMATEUR_NOT_FOUND_MESSAGE + formateurId));

            if (formateur.getClasse() == null || !formateur.getClasse().getId().equals(classeId)) {
                throw new ValidationException("Le formateur n'est pas assigné à cette classe");
            }

            formateur.setClasse(null);
            formateurRepository.save(formateur);
        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error removing formateur from classe: {}", e.getMessage(), e);
            throw new ValidationException("Error removing formateur from classe: " + e.getMessage());
        }
    }
}
