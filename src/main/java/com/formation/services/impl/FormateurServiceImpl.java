package com.formation.services.impl;

import com.formation.dto.FormateurDTO;
import com.formation.exceptions.ResourceNotFoundException;
import com.formation.models.Classe;
import com.formation.models.Formateur;
import com.formation.models.Formation;
import com.formation.models.FormationStatus;
import com.formation.repositories.ClasseRepository;
import com.formation.repositories.FormateurRepository;
import com.formation.repositories.FormationRepository;
import com.formation.services.interfaces.IFormateurService;
import com.formation.utils.FormateurMapper;
import com.formation.validation.FormateurValidator;
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
public class FormateurServiceImpl implements IFormateurService {
    private static final Logger logger = LoggerFactory.getLogger(FormateurServiceImpl.class);

    private final FormateurRepository formateurRepository;
    private final ClasseRepository classeRepository;
    private final FormateurMapper formateurMapper;
    private final FormateurValidator formateurValidator;
    private final FormationRepository formationRepository;

    @Override
    public FormateurDTO save(FormateurDTO formateurDTO) {
        logger.info("Saving new formateur: {}", formateurDTO.getEmail());
        formateurValidator.validateForCreate(formateurDTO);
        Formateur formateur = formateurMapper.toEntity(formateurDTO);
        formateur = formateurRepository.save(formateur);
        return formateurMapper.toDTO(formateur);
    }

    @Override
    public FormateurDTO update(Long id, FormateurDTO formateurDTO) {
        logger.info("Updating formateur with id: {}", id);
        formateurValidator.validateForUpdate(id, formateurDTO);
        return formateurRepository.findById(id)
                .map(existingFormateur -> {
                    formateurMapper.updateFormateurFromDTO(formateurDTO, existingFormateur);
                    return formateurMapper.toDTO(formateurRepository.save(existingFormateur));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Formateur not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting formateur with id: {}", id);
        if (!formateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Formateur not found with id: " + id);
        }
        formateurRepository.deleteById(id);
    }

    @Override
    public Optional<FormateurDTO> findById(Long id) {
        return formateurRepository.findById(id)
                .map(formateurMapper::toDTO);
    }

    @Override
    public List<FormateurDTO> findAll() {
        return formateurRepository.findAll().stream()
                .map(formateurMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FormateurDTO> findAll(Pageable pageable) {
        return formateurRepository.findAll(pageable)
                .map(formateurMapper::toDTO);
    }

    @Override
    public Optional<FormateurDTO> findByEmail(String email) {
        return formateurRepository.findByEmail(email)
                .map(formateurMapper::toDTO);
    }

    @Override
    public List<FormateurDTO> findBySpecialite(String specialite) {
        return formateurRepository.findBySpecialite(specialite).stream()
                .map(formateurMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FormateurDTO> searchByNomOrPrenom(String searchTerm, Pageable pageable) {
        return formateurRepository.findByNomContainingOrPrenomContaining(searchTerm, searchTerm, pageable)
                .map(formateurMapper::toDTO);
    }

    @Override
    public List<FormateurDTO> findAvailableFormateursBySpecialite(String specialite, int maxFormations) {
        return formateurRepository.findAvailableFormateursBySpecialite(specialite, maxFormations).stream()
                .map(formateurMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignToClasse(Long formateurId, Long classeId) {
        logger.info("Assigning formateur {} to classe {}", formateurId, classeId);
        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur not found with id: " + formateurId));

        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new ResourceNotFoundException("Classe not found with id: " + classeId));

        formateur.setClasse(classe);
        formateurRepository.save(formateur);
    }

    @Override
    @Transactional
    public void removeFromClasse(Long formateurId) {
        logger.info("Removing formateur {} from classe", formateurId);
        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur not found with id: " + formateurId));

        formateur.setClasse(null);
        formateurRepository.save(formateur);
    }

    @Override
    @Transactional
    public void assignToFormation(Long formateurId, Long formationId) {
        logger.info("Assigning formateur {} to formation {}", formateurId, formationId);
        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur not found with id: " + formateurId));

        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + formationId));

        if (formation.getFormateur() != null) {
            throw new ValidationException("La formation a déjà un formateur assigné");
        }

        if (formation.getStatut() != FormationStatus.PLANIFIEE) {
            throw new ValidationException("Le formateur ne peut être assigné qu'à une formation planifiée");
        }

        formation.setFormateur(formateur);
        formateur.getFormations().add(formation);
        formateurRepository.save(formateur);
        formationRepository.save(formation);
    }

    @Override
    @Transactional
    public void removeFromFormation(Long formateurId, Long formationId) {
        logger.info("Removing formateur {} from formation {}", formateurId, formationId);
        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur not found with id: " + formateurId));

        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + formationId));

        if (formation.getFormateur() == null || !formation.getFormateur().getId().equals(formateurId)) {
            throw new ValidationException("Le formateur n'est pas assigné à cette formation");
        }

        if (formation.getStatut() != FormationStatus.PLANIFIEE) {
            throw new ValidationException("Le formateur ne peut être retiré que d'une formation planifiée");
        }

        formation.setFormateur(null);
        formateur.getFormations().remove(formation);
        formateurRepository.save(formateur);
        formationRepository.save(formation);
    }
}
