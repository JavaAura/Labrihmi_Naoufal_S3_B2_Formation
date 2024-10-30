package com.formation.services.interfaces;

import com.formation.dto.ClasseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IClasseService {
    ClasseDTO save(ClasseDTO classeDTO);

    ClasseDTO update(Long id, ClasseDTO classeDTO);

    void delete(Long id);

    Optional<ClasseDTO> findById(Long id);

    List<ClasseDTO> findAll();

    Page<ClasseDTO> findAll(Pageable pageable);

    List<ClasseDTO> findByNomContaining(String nom);

    List<ClasseDTO> findAvailableClasses(int maxCapacity);

    boolean existsByNumSalle(String numSalle);

    void assignApprenantToClasse(Long classeId, Long apprenantId);

    void removeApprenantFromClasse(Long classeId, Long apprenantId);

    void assignFormateurToClasse(Long classeId, Long formateurId);

    void removeFormateurFromClasse(Long classeId, Long formateurId);
}
