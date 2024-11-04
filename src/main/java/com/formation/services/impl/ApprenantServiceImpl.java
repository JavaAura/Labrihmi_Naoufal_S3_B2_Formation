package com.formation.services.impl;

import com.formation.dto.ApprenantDTO;
import com.formation.exceptions.ResourceNotFoundException;
import com.formation.models.Apprenant;
import com.formation.models.Classe;
import com.formation.models.Formation;
import com.formation.models.FormationStatus;
import com.formation.models.NiveauFormation;
import com.formation.repositories.ApprenantRepository;
import com.formation.repositories.ClasseRepository;
import com.formation.repositories.FormationRepository;
import com.formation.services.interfaces.IApprenantService;
import com.formation.utils.ApprenantMapper;
import com.formation.validation.ApprenantValidator;
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
public class ApprenantServiceImpl implements IApprenantService {
    private static final Logger logger = LoggerFactory.getLogger(ApprenantServiceImpl.class);
    private static final String APPRENANT_NOT_FOUND_MESSAGE = "Apprenant not found with id: ";

    private final ApprenantRepository apprenantRepository;
    private final ClasseRepository classeRepository;
    private final FormationRepository formationRepository;
    private final ApprenantMapper apprenantMapper;
    private final ApprenantValidator apprenantValidator;

    @Override
    @Transactional
    public ApprenantDTO save(ApprenantDTO apprenantDTO) {
        logger.info("Saving new apprenant: {}", apprenantDTO.getEmail());
        apprenantValidator.validateForCreate(apprenantDTO);
        Apprenant apprenant = apprenantMapper.toEntity(apprenantDTO);
        apprenant = apprenantRepository.save(apprenant);
        ApprenantDTO savedDTO = apprenantMapper.toDTO(apprenant);
        logger.debug("Saved apprenant with ID: {}", savedDTO.getId());
        return savedDTO;
    }

    @Override
    @Transactional
    public ApprenantDTO update(Long id, ApprenantDTO apprenantDTO) {
        logger.info("Updating apprenant with id: {}", id);
        apprenantValidator.validateForUpdate(id, apprenantDTO);

        return apprenantRepository.findById(id)
                .map(existingApprenant -> {
                    apprenantMapper.updateApprenantFromDTO(apprenantDTO, existingApprenant);
                    Apprenant savedApprenant = apprenantRepository.save(existingApprenant);
                    return apprenantMapper.toDTO(savedApprenant);
                })
                .orElseThrow(() -> new ResourceNotFoundException(APPRENANT_NOT_FOUND_MESSAGE + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        logger.info("Deleting apprenant with id: {}", id);
        Apprenant apprenant = apprenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(APPRENANT_NOT_FOUND_MESSAGE + id));

        // Remove from classe if any
        if (apprenant.getClasse() != null) {
            apprenant.setClasse(null);
        }

        // Clear formations
        apprenant.getFormations().clear();
        apprenantRepository.save(apprenant);
        apprenantRepository.flush();

        apprenantRepository.delete(apprenant);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApprenantDTO> findById(Long id) {
        return apprenantRepository.findById(id)
                .map(apprenant -> {
                    ApprenantDTO dto = apprenantMapper.toDTO(apprenant);
                    dto.setFormationIds(apprenant.getFormations().stream()
                            .map(Formation::getId)
                            .collect(Collectors.toSet()));
                    return dto;
                });
    }

    @Override
    public List<ApprenantDTO> findAll() {
        return apprenantRepository.findAll().stream()
                .map(apprenantMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ApprenantDTO> findAll(Pageable pageable) {
        return apprenantRepository.findAll(pageable)
                .map(apprenantMapper::toDTO);
    }

    @Override
    public Optional<ApprenantDTO> findByEmail(String email) {
        return apprenantRepository.findByEmail(email)
                .map(apprenantMapper::toDTO);
    }

    @Override
    public List<ApprenantDTO> findByNiveau(NiveauFormation niveau) {
        return apprenantRepository.findByNiveau(niveau).stream()
                .map(apprenantMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApprenantDTO> findByClasseId(Long classeId) {
        return apprenantRepository.findByClasseId(classeId).stream()
                .map(apprenantMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ApprenantDTO> searchByNomOrPrenom(String searchTerm, Pageable pageable) {
        return apprenantRepository.findByNomContainingOrPrenomContaining(searchTerm, searchTerm, pageable)
                .map(apprenantMapper::toDTO);
    }

    @Override
    public boolean existsByEmail(String email) {
        return apprenantRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void assignToClasse(Long apprenantId, Long classeId) {
        logger.info("Assigning apprenant {} to classe {}", apprenantId, classeId);
        Apprenant apprenant = apprenantRepository.findById(apprenantId)
                .orElseThrow(() -> new ResourceNotFoundException(APPRENANT_NOT_FOUND_MESSAGE + apprenantId));

        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new ResourceNotFoundException("Classe not found with id: " + classeId));

        if (apprenant.getClasse() != null) {
            throw new ValidationException("L'apprenant est déjà assigné à une classe");
        }

        apprenant.setClasse(classe);
        apprenantRepository.save(apprenant);
    }

    @Override
    @Transactional
    public void removeFromClasse(Long apprenantId) {
        logger.info("Removing apprenant {} from classe", apprenantId);
        Apprenant apprenant = apprenantRepository.findById(apprenantId)
                .orElseThrow(() -> new ResourceNotFoundException(APPRENANT_NOT_FOUND_MESSAGE + apprenantId));

        apprenant.setClasse(null);
        apprenantRepository.save(apprenant);
    }

    @Override
    @Transactional
    public void assignToFormation(Long apprenantId, Long formationId) {
        logger.info("Assigning apprenant {} to formation {}", apprenantId, formationId);
        Apprenant apprenant = apprenantRepository.findById(apprenantId)
                .orElseThrow(() -> new ResourceNotFoundException(APPRENANT_NOT_FOUND_MESSAGE + apprenantId));

        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + formationId));

        if (formation.getStatut() != FormationStatus.PLANIFIEE) {
            throw new ValidationException("Les apprenants ne peuvent être assignés qu'aux formations planifiées");
        }

        if (formation.getApprenants().size() >= formation.getCapaciteMax()) {
            throw new ValidationException("La formation a atteint sa capacité maximale");
        }

        apprenant.getFormations().add(formation);
        formation.getApprenants().add(apprenant);
        apprenantRepository.save(apprenant);
        apprenantRepository.flush();
    }

    @Override
    @Transactional
    public void removeFromFormation(Long apprenantId, Long formationId) {
        Apprenant apprenant = apprenantRepository.findById(apprenantId)
                .orElseThrow(() -> new ResourceNotFoundException(APPRENANT_NOT_FOUND_MESSAGE + apprenantId));

        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found"));

        apprenant.getFormations().remove(formation);
        formation.getApprenants().remove(apprenant);

        apprenantRepository.save(apprenant);
    }
}
