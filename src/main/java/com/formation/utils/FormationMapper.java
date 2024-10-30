package com.formation.utils;

import com.formation.dto.FormationDTO;
import com.formation.models.Formation;
import com.formation.repositories.ApprenantRepository;
import com.formation.repositories.FormateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FormationMapper {
    private final FormateurRepository formateurRepository;
    private final ApprenantRepository apprenantRepository;

    public FormationDTO toDTO(Formation formation) {
        return FormationDTO.builder()
                .id(formation.getId())
                .titre(formation.getTitre())
                .niveau(formation.getNiveau())
                .prerequis(formation.getPrerequis())
                .capaciteMin(formation.getCapaciteMin())
                .capaciteMax(formation.getCapaciteMax())
                .dateDebut(formation.getDateDebut())
                .dateFin(formation.getDateFin())
                .statut(formation.getStatut())
                .formateurId(formation.getFormateur() != null ? formation.getFormateur().getId() : null)
                .apprenantIds(formation.getApprenants().stream()
                        .map(apprenant -> apprenant.getId())
                        .collect(Collectors.toSet()))
                .build();
    }

    public Formation toEntity(FormationDTO dto) {
        Formation formation = Formation.builder()
                .id(dto.getId())
                .titre(dto.getTitre())
                .niveau(dto.getNiveau())
                .prerequis(dto.getPrerequis())
                .capaciteMin(dto.getCapaciteMin())
                .capaciteMax(dto.getCapaciteMax())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .statut(dto.getStatut())
                .apprenants(new HashSet<>())
                .build();

        if (dto.getFormateurId() != null) {
            formateurRepository.findById(dto.getFormateurId())
                    .ifPresent(formation::setFormateur);
        }

        if (dto.getApprenantIds() != null) {
            apprenantRepository.findAllById(dto.getApprenantIds())
                    .forEach(apprenant -> formation.getApprenants().add(apprenant));
        }

        return formation;
    }
}