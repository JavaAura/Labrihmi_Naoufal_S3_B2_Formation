package com.formation.repositories;

import com.formation.models.Formateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormateurRepository extends JpaRepository<Formateur, Long> {
    Optional<Formateur> findByEmail(String email);

    List<Formateur> findBySpecialite(String specialite);

    Page<Formateur> findByNomContainingOrPrenomContaining(String nom, String prenom, Pageable pageable);

    @Query("SELECT f FROM Formateur f WHERE f.specialite = :specialite AND SIZE(f.formations) < :maxFormations")
    List<Formateur> findAvailableFormateursBySpecialite(@Param("specialite") String specialite,
            @Param("maxFormations") int maxFormations);

    @Query("SELECT DISTINCT f.specialite FROM Formateur f")
    List<String> findAllSpecialites();

    boolean existsByEmail(String email);
}
