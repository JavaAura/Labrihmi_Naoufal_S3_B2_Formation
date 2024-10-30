package com.formation.utils;

import com.formation.dto.ClasseDTO;
import com.formation.models.Classe;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClasseMapper {
    private static final Logger logger = LoggerFactory.getLogger(ClasseMapper.class);

    public ClasseDTO toDTO(Classe classe) {
        if (classe == null) {
            logger.debug("Converting null Classe to null DTO");
            return null;
        }

        logger.debug("Converting Classe with ID {} to DTO", classe.getId());
        return ClasseDTO.builder()
                .id(classe.getId())
                .nom(classe.getNom())
                .numSalle(classe.getNumSalle())
                .apprenantIds(Optional.ofNullable(classe.getApprenants())
                        .map(apprenants -> apprenants.stream()
                                .map(apprenant -> apprenant.getId())
                                .collect(Collectors.toSet()))
                        .orElse(new HashSet<>()))
                .formateurIds(Optional.ofNullable(classe.getFormateurs())
                        .map(formateurs -> formateurs.stream()
                                .map(formateur -> formateur.getId())
                                .collect(Collectors.toSet()))
                        .orElse(new HashSet<>()))
                .build();
    }

    public Classe toEntity(ClasseDTO classeDTO) {
        if (classeDTO == null) {
            logger.debug("Converting null DTO to null Classe");
            return null;
        }

        logger.debug("Converting ClasseDTO to Entity {}", classeDTO.getNom());
        return Classe.builder()
                .id(classeDTO.getId())
                .nom(classeDTO.getNom() != null ? classeDTO.getNom().trim() : null)
                .numSalle(classeDTO.getNumSalle() != null ? classeDTO.getNumSalle().trim() : null)
                .apprenants(new HashSet<>())
                .formateurs(new HashSet<>())
                .build();
    }

    public void updateClasseFromDTO(ClasseDTO classeDTO, Classe classe) {
        if (classeDTO == null) {
            logger.warn("Attempt to update Classe with null DTO");
            return;
        }

        logger.debug("Updating Classe {} with DTO data", classe.getId());

        Optional.ofNullable(classeDTO.getNom())
                .map(String::trim)
                .ifPresent(classe::setNom);

        Optional.ofNullable(classeDTO.getNumSalle())
                .map(String::trim)
                .ifPresent(classe::setNumSalle);
    }
}