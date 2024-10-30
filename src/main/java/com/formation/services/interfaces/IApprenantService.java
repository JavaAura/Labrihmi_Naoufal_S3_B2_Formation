package com.formation.services.interfaces;

import com.formation.dto.ApprenantDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IApprenantService {
    ApprenantDTO save(ApprenantDTO apprenantDTO);

    ApprenantDTO update(Long id, ApprenantDTO apprenantDTO);

    void delete(Long id);

    Optional<ApprenantDTO> findById(Long id);

    List<ApprenantDTO> findAll();

    Page<ApprenantDTO> findAll(Pageable pageable);

    Optional<ApprenantDTO> findByEmail(String email);

    List<ApprenantDTO> findByNiveau(String niveau);

    List<ApprenantDTO> findByClasseId(Long classeId);

    Page<ApprenantDTO> searchByNomOrPrenom(String searchTerm, Pageable pageable);

    boolean existsByEmail(String email);

    void assignToClasse(Long apprenantId, Long classeId);

    void removeFromClasse(Long apprenantId);

    void assignToFormation(Long apprenantId, Long formationId);

    void removeFromFormation(Long apprenantId, Long formationId);
}
