package com.formation.repositories;

import com.formation.models.Classe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {
    List<Classe> findByNomContaining(String nom);

    @Query("SELECT c FROM Classe c WHERE SIZE(c.apprenants) < :maxCapacity")
    List<Classe> findAvailableClasses(@Param("maxCapacity") int maxCapacity);

    boolean existsByNumSalle(String numSalle);

    @Query("SELECT c FROM Classe c WHERE SIZE(c.formateurs) < :maxFormateurs")
    Page<Classe> findClassesWithAvailableFormateurSpots(@Param("maxFormateurs") int maxFormateurs,
            Pageable pageable);
}
