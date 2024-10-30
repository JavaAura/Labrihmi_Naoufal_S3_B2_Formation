package com.formation.utils;

import com.formation.dto.FormationDTO;
import com.formation.models.Formation;
import com.formation.models.Apprenant;

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
        if (formation == null) {
            return null;
        }

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
                .apprenantIds(formation.getApprenants() != null ? formation.getApprenants().stream()
                        .map(Apprenant::getId)
                        .collect(Collectors.toSet()) : new HashSet<>())
                .build();
    }

    public Formation toEntity(FormationDTO dto) {
        if (dto == null) {
            return null;
        }

        Formation formation = new Formation();
        formation.setId(dto.getId());
        formation.setTitre(dto.getTitre());
        formation.setNiveau(dto.getNiveau());
        formation.setPrerequis(dto.getPrerequis());
        formation.setCapaciteMin(dto.getCapaciteMin());
        formation.setCapaciteMax(dto.getCapaciteMax());
        formation.setDateDebut(dto.getDateDebut());
        formation.setDateFin(dto.getDateFin());
        formation.setStatut(dto.getStatut());
        formation.setApprenants(new HashSet<>());

        if (dto.getFormateurId() != null) {
            formateurRepository.findById(dto.getFormateurId())
                    .ifPresent(formation::setFormateur);
        }

        return formation;
    }
}