package com.formation.services.interfaces;

import com.formation.dto.FormationDTO;
import com.formation.models.FormationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IFormationService {
    FormationDTO save(FormationDTO formationDTO);

    FormationDTO update(Long id, FormationDTO formationDTO);

    void delete(Long id);

    Optional<FormationDTO> findById(Long id);

    List<FormationDTO> findAll();

    Page<FormationDTO> findAll(Pageable pageable);

    List<FormationDTO> findByStatut(FormationStatus statut);

    List<FormationDTO> findBetweenDates(LocalDateTime debut, LocalDateTime fin);

    List<FormationDTO> findByFormateurId(Long formateurId);

    List<FormationDTO> findWithAvailablePlaces();

    boolean addApprenantToFormation(Long formationId, Long apprenantId);

    boolean removeApprenantFromFormation(Long formationId, Long apprenantId);

    void updateStatus(Long id, FormationStatus newStatus);

    List<FormationDTO> findPlannedFormationsByNiveau(String niveau);

    Page<FormationDTO> searchByTitre(String titre, Pageable pageable);

    boolean isFormationFull(Long formationId);
}
