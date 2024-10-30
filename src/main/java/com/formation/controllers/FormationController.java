package com.formation.controllers;

import com.formation.dto.FormationDTO;
import com.formation.dto.response.ApiResponse;
import com.formation.models.FormationStatus;
import com.formation.services.interfaces.IFormationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@Api(tags = "Gestion des Formations")
public class FormationController {
    private static final Logger logger = LoggerFactory.getLogger(FormationController.class);
    private final IFormationService formationService;

    @PostMapping
    @ApiOperation("Créer une nouvelle formation")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody FormationDTO formationDTO) {
        logger.info("Creating new formation: {}", formationDTO.getTitre());
        FormationDTO created = formationService.save(formationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Formation créée avec succès", created));
    }

    @PutMapping("/{id}")
    @ApiOperation("Mettre à jour une formation")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody FormationDTO formationDTO) {
        logger.info("Updating formation with id: {}", id);
        FormationDTO updated = formationService.update(id, formationDTO);
        return ResponseEntity.ok(new ApiResponse(true, "Formation mise à jour avec succès", updated));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Supprimer une formation")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        logger.info("Deleting formation with id: {}", id);
        formationService.delete(id);
        return ResponseEntity.ok(new ApiResponse(true, "Formation supprimée avec succès", null));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obtenir une formation par son ID")
    public ResponseEntity<FormationDTO> findById(@PathVariable Long id) {
        return formationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation("Obtenir toutes les formations avec pagination")
    public ResponseEntity<Page<FormationDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(formationService.findAll(pageable));
    }

    @GetMapping("/all")
    @ApiOperation("Obtenir toutes les formations")
    public ResponseEntity<List<FormationDTO>> findAll() {
        return ResponseEntity.ok(formationService.findAll());
    }

    @GetMapping("/status/{statut}")
    @ApiOperation("Obtenir les formations par statut")
    public ResponseEntity<List<FormationDTO>> findByStatut(@PathVariable FormationStatus statut) {
        return ResponseEntity.ok(formationService.findByStatut(statut));
    }

    @GetMapping("/dates")
    @ApiOperation("Obtenir les formations entre deux dates")
    public ResponseEntity<List<FormationDTO>> findBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(formationService.findBetweenDates(debut, fin));
    }

    @GetMapping("/formateur/{formateurId}")
    @ApiOperation("Obtenir les formations d'un formateur")
    public ResponseEntity<List<FormationDTO>> findByFormateurId(@PathVariable Long formateurId) {
        return ResponseEntity.ok(formationService.findByFormateurId(formateurId));
    }

    @GetMapping("/available")
    @ApiOperation("Obtenir les formations avec des places disponibles")
    public ResponseEntity<List<FormationDTO>> findWithAvailablePlaces() {
        return ResponseEntity.ok(formationService.findWithAvailablePlaces());
    }

    @PostMapping("/{id}/apprenants/{apprenantId}")
    @ApiOperation("Ajouter un apprenant à une formation")
    public ResponseEntity<ApiResponse> addApprenant(@PathVariable Long id, @PathVariable Long apprenantId) {
        boolean added = formationService.addApprenantToFormation(id, apprenantId);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant ajouté à la formation avec succès", added));
    }

    @DeleteMapping("/{id}/apprenants/{apprenantId}")
    @ApiOperation("Retirer un apprenant d'une formation")
    public ResponseEntity<ApiResponse> removeApprenant(@PathVariable Long id, @PathVariable Long apprenantId) {
        boolean removed = formationService.removeApprenantFromFormation(id, apprenantId);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant retiré de la formation avec succès", removed));
    }

    @PutMapping("/{id}/status/{status}")
    @ApiOperation("Mettre à jour le statut d'une formation")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable Long id, @PathVariable FormationStatus status) {
        formationService.updateStatus(id, status);
        return ResponseEntity.ok(new ApiResponse(true, "Statut de la formation mis à jour avec succès", null));
    }

    @GetMapping("/niveau/{niveau}")
    @ApiOperation("Obtenir les formations planifiées par niveau")
    public ResponseEntity<List<FormationDTO>> findPlannedByNiveau(@PathVariable String niveau) {
        return ResponseEntity.ok(formationService.findPlannedFormationsByNiveau(niveau));
    }

    @GetMapping("/search")
    @ApiOperation("Rechercher des formations par titre")
    public ResponseEntity<Page<FormationDTO>> searchByTitre(@RequestParam String titre, Pageable pageable) {
        return ResponseEntity.ok(formationService.searchByTitre(titre, pageable));
    }

    @GetMapping("/{id}/full")
    @ApiOperation("Vérifier si une formation est complète")
    public ResponseEntity<ApiResponse> isFormationFull(@PathVariable Long id) {
        boolean isFull = formationService.isFormationFull(id);
        return ResponseEntity.ok(new ApiResponse(true, "Vérification effectuée", isFull));
    }

}
