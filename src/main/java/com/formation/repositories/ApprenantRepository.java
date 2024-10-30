package com.formation.repositories;

import com.formation.models.Apprenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprenantRepository extends JpaRepository<Apprenant, Long> {
    Optional<Apprenant> findByEmail(String email);

    List<Apprenant> findByNiveau(String niveau);

    Page<Apprenant> findByNomContainingOrPrenomContaining(String nom, String prenom, Pageable pageable);

    @Query("SELECT a FROM Apprenant a WHERE a.classe.id = :classeId")
    List<Apprenant> findByClasseId(@Param("classeId") Long classeId);

    @Query("SELECT a FROM Apprenant a WHERE SIZE(a.formations) < :maxFormations")
    List<Apprenant> findApprenantsAvailableForFormation(@Param("maxFormations") int maxFormations);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(a) FROM Apprenant a WHERE a.classe.id = :classeId")
    long countByClasseId(@Param("classeId") Long classeId);
}
