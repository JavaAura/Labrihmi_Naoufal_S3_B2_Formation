package com.formation.repositories;

import com.formation.models.Formation;
import com.formation.models.FormationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByStatut(FormationStatus statut);

    Page<Formation> findByTitreContaining(String titre, Pageable pageable);

    @Query("SELECT f FROM Formation f WHERE f.dateDebut BETWEEN :debut AND :fin")
    List<Formation> findFormationsBetweenDates(@Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    List<Formation> findByFormateurId(Long formateurId);

    @Query("SELECT f FROM Formation f WHERE SIZE(f.apprenants) < f.capaciteMax")
    List<Formation> findFormationsWithAvailablePlaces();

    @Query("SELECT f FROM Formation f WHERE f.niveau = :niveau AND f.statut = 'PLANIFIEE'")
    List<Formation> findPlannedFormationsByNiveau(@Param("niveau") String niveau);
}
