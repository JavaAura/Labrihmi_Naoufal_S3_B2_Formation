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

    private final ClasseRepository classeRepository;
    private final ApprenantRepository apprenantRepository;
    private final FormateurRepository formateurRepository;
    private final ClasseMapper classeMapper;
    private final ClasseValidator classeValidator;

    @Override
    public ClasseDTO save(ClasseDTO classeDTO) {
        logger.info("Saving new classe: {}", classeDTO.getNom());
        classeValidator.validateForCreate(classeDTO);
        Classe classe = classeMapper.toEntity(classeDTO);
        classe = classeRepository.save(classe);
        return classeMapper.toDTO(classe);
    }

    @Override
    public ClasseDTO update(Long id, ClasseDTO classeDTO) {
        logger.info("Updating classe with id: {}", id);
        classeValidator.validateForUpdate(id, classeDTO);
        return classeRepository.findById(id)
                .map(existingClasse -> {
                    classeMapper.updateClasseFromDTO(classeDTO, existingClasse);
                    return classeMapper.toDTO(classeRepository.save(existingClasse));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Classe not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting classe with id: {}", id);
        Optional<Classe> classeOptional = classeRepository.findById(id);
        if (classeOptional.isPresent()) {
            classeRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Classe not found with id: " + id);
        }
    }

    @Override
    public Optional<ClasseDTO> findById(Long id) {
        return classeRepository.findById(id).map(classeMapper::toDTO);
    }

    @Override
    public List<ClasseDTO> findAll() {
        return classeRepository.findAll().stream()
                .map(classeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
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
        logger.info("Assigning apprenant {} to classe {}", apprenantId, classeId);

        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new ResourceNotFoundException("Classe not found with id: " + classeId));

        Apprenant apprenant = apprenantRepository.findById(apprenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Apprenant not found with id: " + apprenantId));

        if (apprenant.getClasse() != null) {
            throw new ValidationException("L'apprenant est déjà assigné à une classe");
        }

        apprenant.setClasse(classe);
        apprenantRepository.save(apprenant);
    }

    @Override
    @Transactional
    public void removeApprenantFromClasse(Long classeId, Long apprenantId) {
        logger.info("Removing apprenant {} from classe {}", apprenantId, classeId);

        Apprenant apprenant = apprenantRepository.findById(apprenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Apprenant not found with id: " + apprenantId));

        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new ResourceNotFoundException("Classe not found with id: " + classeId));

        if (apprenant.getClasse() == null || !apprenant.getClasse().getId().equals(classeId)) {
            throw new ValidationException("L'apprenant n'est pas assigné à cette classe");
        }

        apprenant.setClasse(null);
        classe.getApprenants().remove(apprenant);

        apprenantRepository.save(apprenant);
        classeRepository.save(classe);
    }

    @Override
    @Transactional
    public void assignFormateurToClasse(Long classeId, Long formateurId) {
        logger.info("Assigning formateur {} to classe {}", formateurId, classeId);
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new ResourceNotFoundException("Classe not found with id: " + classeId));

        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur not found with id: " + formateurId));

        if (formateur.getClasse() != null) {
            throw new ValidationException("Le formateur est déjà assigné à une classe");
        }

        formateur.setClasse(classe);
        formateurRepository.save(formateur);
    }

    @Override
    @Transactional
    public void removeFormateurFromClasse(Long classeId, Long formateurId) {
        logger.info("Removing formateur {} from classe {}", formateurId, classeId);
        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur not found with id: " + formateurId));

        if (formateur.getClasse() == null || !formateur.getClasse().getId().equals(classeId)) {
            throw new ValidationException("Le formateur n'est pas assigné à cette classe");
        }

        formateur.setClasse(null);
        formateurRepository.save(formateur);
    }
}
