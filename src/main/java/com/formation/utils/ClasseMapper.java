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

    public Classe toEntity(ClasseDTO dto) {
        if (dto == null) {
            logger.debug("Converting null DTO to null Classe");
            return null;
        }

        logger.debug("Converting ClasseDTO to Entity {}", dto.getNom());
        return Classe.builder()
                .id(dto.getId())
                .nom(dto.getNom() != null ? dto.getNom().trim() : null)
                .numSalle(dto.getNumSalle() != null ? dto.getNumSalle().trim() : null)
                .build();
    }

    public void updateClasseFromDTO(ClasseDTO dto, Classe classe) {
        if (dto == null) {
            logger.warn("Attempt to update Classe with null DTO");
            return;
        }

        logger.debug("Updating Classe {} with DTO data", classe.getId());
        
        if (dto.getNom() != null) {
            classe.setNom(dto.getNom().trim());
        }
        if (dto.getNumSalle() != null) {
            classe.setNumSalle(dto.getNumSalle().trim());
        }
    }
}