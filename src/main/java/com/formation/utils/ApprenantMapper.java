package com.formation.utils;

import com.formation.dto.ApprenantDTO;
import com.formation.models.Apprenant;
import com.formation.repositories.ClasseRepository;
import com.formation.repositories.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApprenantMapper {
    private final ClasseRepository classeRepository;
    private final FormationRepository formationRepository;

    public ApprenantDTO toDTO(Apprenant apprenant) {
        return ApprenantDTO.builder()
                .id(apprenant.getId())
                .nom(apprenant.getNom())
                .prenom(apprenant.getPrenom())
                .email(apprenant.getEmail())
                .niveau(apprenant.getNiveau())
                .classeId(apprenant.getClasse() != null ? apprenant.getClasse().getId() : null)
                .formationIds(apprenant.getFormations().stream()
                        .map(formation -> formation.getId())
                        .collect(Collectors.toSet()))
                .build();
    }

    public Apprenant toEntity(ApprenantDTO dto) {
        Apprenant apprenant = Apprenant.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .niveau(dto.getNiveau())
                .formations(new HashSet<>())
                .build();

        if (dto.getClasseId() != null) {
            classeRepository.findById(dto.getClasseId())
                    .ifPresent(apprenant::setClasse);
        }

        if (dto.getFormationIds() != null) {
            formationRepository.findAllById(dto.getFormationIds())
                    .forEach(formation -> apprenant.getFormations().add(formation));
        }

        return apprenant;
    }

    public void updateApprenantFromDTO(ApprenantDTO dto, Apprenant apprenant) {
        apprenant.setNom(dto.getNom());
        apprenant.setPrenom(dto.getPrenom());
        apprenant.setEmail(dto.getEmail());
        apprenant.setNiveau(dto.getNiveau());

        if (dto.getClasseId() != null) {
            classeRepository.findById(dto.getClasseId())
                    .ifPresent(apprenant::setClasse);
        } else {
            apprenant.setClasse(null);
        }
    }
}