package com.formation.utils;

import com.formation.dto.FormateurDTO;
import com.formation.models.Formateur;
import com.formation.repositories.ClasseRepository;
import com.formation.repositories.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FormateurMapper {
    private final ClasseRepository classeRepository;
    private final FormationRepository formationRepository;

    public FormateurDTO toDTO(Formateur formateur) {
        return FormateurDTO.builder()
                .id(formateur.getId())
                .nom(formateur.getNom())
                .prenom(formateur.getPrenom())
                .email(formateur.getEmail())
                .specialite(formateur.getSpecialite())
                .classeId(formateur.getClasse() != null ? formateur.getClasse().getId() : null)
                .formationIds(formateur.getFormations().stream()
                        .map(formation -> formation.getId())
                        .collect(Collectors.toSet()))
                .build();
    }

    public Formateur toEntity(FormateurDTO dto) {
        Formateur formateur = Formateur.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .specialite(dto.getSpecialite())
                .formations(new HashSet<>())
                .build();

        if (dto.getClasseId() != null) {
            classeRepository.findById(dto.getClasseId())
                    .ifPresent(formateur::setClasse);
        }

        if (dto.getFormationIds() != null) {
            formationRepository.findAllById(dto.getFormationIds())
                    .forEach(formation -> formateur.getFormations().add(formation));
        }

        return formateur;
    }

    public void updateFormateurFromDTO(FormateurDTO dto, Formateur formateur) {
        formateur.setNom(dto.getNom());
        formateur.setPrenom(dto.getPrenom());
        formateur.setEmail(dto.getEmail());
    }
}