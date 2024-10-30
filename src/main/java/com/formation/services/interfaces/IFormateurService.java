package com.formation.services.interfaces;

import com.formation.dto.FormateurDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IFormateurService {
    FormateurDTO save(FormateurDTO formateurDTO);

    FormateurDTO update(Long id, FormateurDTO formateurDTO);

    void delete(Long id);

    Optional<FormateurDTO> findById(Long id);

    List<FormateurDTO> findAll();

    Page<FormateurDTO> findAll(Pageable pageable);

    Optional<FormateurDTO> findByEmail(String email);

    List<FormateurDTO> findBySpecialite(String specialite);

    Page<FormateurDTO> searchByNomOrPrenom(String searchTerm, Pageable pageable);

    List<FormateurDTO> findAvailableFormateursBySpecialite(String specialite, int maxFormations);

    void assignToClasse(Long formateurId, Long classeId);

    void removeFromClasse(Long formateurId);

    void assignToFormation(Long formateurId, Long formationId);

    void removeFromFormation(Long formateurId, Long formationId);
}
