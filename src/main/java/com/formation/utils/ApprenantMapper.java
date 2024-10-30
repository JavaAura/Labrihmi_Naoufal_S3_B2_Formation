package com.formation.utils;

import com.formation.dto.ApprenantDTO;
import com.formation.models.Apprenant;
import com.formation.repositories.ClasseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApprenantMapper {
    private final ClasseRepository classeRepository;

    public ApprenantDTO toDTO(Apprenant apprenant) {
        if (apprenant == null) {
            return null;
        }

        return ApprenantDTO.builder()
                .id(apprenant.getId())
                .nom(apprenant.getNom())
                .prenom(apprenant.getPrenom())
                .email(apprenant.getEmail())
                .niveau(apprenant.getNiveau())
                .classeId(apprenant.getClasse() != null ? apprenant.getClasse().getId() : null)
                .build();
    }

    public Apprenant toEntity(ApprenantDTO dto) {
        if (dto == null) {
            return null;
        }

        Apprenant apprenant = new Apprenant();
        updateApprenantFromDTO(dto, apprenant);
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
        }
    }
}