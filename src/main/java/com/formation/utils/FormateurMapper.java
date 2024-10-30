package com.formation.utils;

import com.formation.dto.FormateurDTO;
import com.formation.models.Formateur;
import com.formation.models.Formation;
import com.formation.models.Classe;
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
        if (formateur == null) {
            return null;
        }

        FormateurDTO.FormateurDTOBuilder builder = FormateurDTO.builder()
                .id(formateur.getId())
                .nom(formateur.getNom())
                .prenom(formateur.getPrenom())
                .email(formateur.getEmail())
                .specialite(formateur.getSpecialite())
                .formationIds(formateur.getFormations().stream()
                        .map(Formation::getId)
                        .collect(Collectors.toSet()));

        if (formateur.getClasse() != null) {
            builder.classeId(formateur.getClasse().getId());
        }

        return builder.build();
    }

    public Formateur toEntity(FormateurDTO dto) {
        if (dto == null) {
            return null;
        }

        Formateur formateur = new Formateur();
        formateur.setNom(dto.getNom());
        formateur.setPrenom(dto.getPrenom());
        formateur.setEmail(dto.getEmail());
        formateur.setSpecialite(dto.getSpecialite());
        formateur.setFormations(new HashSet<>());

        if (dto.getClasseId() != null) {
            classeRepository.findById(dto.getClasseId())
                    .ifPresent(formateur::setClasse);
        }

        return formateur;
    }

    public void updateFormateurFromDTO(FormateurDTO dto, Formateur formateur) {
        formateur.setNom(dto.getNom());
        formateur.setPrenom(dto.getPrenom());
        formateur.setEmail(dto.getEmail());
        formateur.setSpecialite(dto.getSpecialite());
    }
}