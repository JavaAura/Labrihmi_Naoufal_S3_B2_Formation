package com.formation.controllers;

import com.formation.dto.FormationDTO;
import com.formation.dto.response.ApiResponse;
import com.formation.exceptions.ResourceNotFoundException;
import com.formation.models.FormationStatus;
import com.formation.services.interfaces.IFormationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/formations")
@Api(tags = "Gestion des Formations", description = "API pour la gestion des formations")
@Validated
@RequiredArgsConstructor
public class FormationController {
    private static final Logger logger = LoggerFactory.getLogger(FormationController.class);
    private final IFormationService formationService;

    @PostMapping
    @ApiOperation(value = "Créer une nouvelle formation", notes = "Crée une nouvelle formation avec les informations fournies")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 201, message = "Formation créée avec succès", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Données de la formation invalides"),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(
            @ApiParam(value = "Données de la formation à créer", required = true) @Valid @RequestBody FormationDTO formationDTO) {
        logger.info("Creating new formation: {}", formationDTO.getTitre());
        return Optional.of(formationDTO)
                .map(formationService::save)
                .map(saved -> new ApiResponse(true, "Formation créée avec succès", saved))
                .orElseThrow(() -> new ValidationException("Erreur lors de la création"));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Mettre à jour une formation", notes = "Met à jour les informations d'une formation existante")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formation mise à jour avec succès", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formation non trouvée"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Données invalides")
    })
    public ResponseEntity<ApiResponse> update(
            @ApiParam(value = "ID de la formation", required = true) @PathVariable @NotNull Long id,
            @ApiParam(value = "Nouvelles données de la formation", required = true) @Valid @RequestBody FormationDTO formationDTO) {
        logger.info("Updating formation with id: {}", id);
        try {
            FormationDTO updated = formationService.update(id, formationDTO);
            return ResponseEntity.ok(new ApiResponse(true, "Formation mise à jour avec succès", updated));
        } catch (ValidationException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating formation: ", e);
            throw new ValidationException("Erreur lors de la mise à jour de la formation");
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Supprimer une formation", notes = "Supprime une formation existante par son ID")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formation supprimée avec succès"),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formation non trouvée")
    })
    public ResponseEntity<ApiResponse> delete(
            @ApiParam(value = "ID de la formation à supprimer", required = true) @PathVariable Long id) {
        logger.info("Deleting formation with id: {}", id);
        formationService.delete(id);
        return ResponseEntity.ok(new ApiResponse(true, "Formation supprimée avec succès", null));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Obtenir une formation par son ID", notes = "Récupère les détails d'une formation spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formation trouvée", response = FormationDTO.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formation non trouvée")
    })
    public ResponseEntity<FormationDTO> findById(
            @ApiParam(value = "ID de la formation", required = true) @PathVariable Long id) {
        return formationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation(value = "Obtenir toutes les formations avec pagination", notes = "Récupère une page de formations selon les paramètres de pagination")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Page de formations récupérée avec succès", response = Page.class)
    })
    public ResponseEntity<Page<FormationDTO>> findAll(
            @ApiParam(value = "Informations de pagination") Pageable pageable) {
        return ResponseEntity.ok(formationService.findAll(pageable));
    }

    @GetMapping("/all")
    @ApiOperation(value = "Obtenir toutes les formations", notes = "Récupère la liste complète des formations")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des formations récupérée avec succès", response = List.class)
    })
    public ResponseEntity<List<FormationDTO>> findAll() {
        return ResponseEntity.ok(formationService.findAll());
    }

    @GetMapping("/status/{statut}")
    @ApiOperation(value = "Obtenir les formations par statut", notes = "Récupère la liste des formations ayant un statut spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des formations récupérée avec succès"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Statut invalide")
    })
    public ResponseEntity<List<FormationDTO>> findByStatut(
            @ApiParam(value = "Statut de la formation", required = true) @PathVariable FormationStatus statut) {
        return ResponseEntity.ok(formationService.findByStatut(statut));
    }

    @GetMapping("/dates")
    @ApiOperation(value = "Obtenir les formations entre deux dates", notes = "Récupère la liste des formations programmées entre deux dates spécifiques")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des formations récupérée avec succès"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Dates invalides")
    })
    public ResponseEntity<List<FormationDTO>> findBetweenDates(
            @ApiParam(value = "Date de début", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @ApiParam(value = "Date de fin", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(formationService.findBetweenDates(debut, fin));
    }

    @GetMapping("/formateur/{formateurId}")
    @ApiOperation(value = "Obtenir les formations d'un formateur", notes = "Récupère la liste des formations associées à un formateur spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des formations récupérée avec succès"),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formateur non trouvé")
    })
    public ResponseEntity<List<FormationDTO>> findByFormateurId(
            @ApiParam(value = "ID du formateur", required = true) @PathVariable Long formateurId) {
        logger.info("Fetching formations for formateur: {}", formateurId);
        return ResponseEntity.ok(formationService.findByFormateurId(formateurId));
    }

    @GetMapping("/available")
    @ApiOperation(value = "Obtenir les formations avec des places disponibles", notes = "Récupère la liste des formations ayant encore des places disponibles")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des formations disponibles récupérée avec succès")
    })
    public ResponseEntity<List<FormationDTO>> findWithAvailablePlaces() {
        logger.info("Fetching available formations");
        return ResponseEntity.ok(formationService.findWithAvailablePlaces());
    }

    @PostMapping("/{id}/apprenants/{apprenantId}")
    @ApiOperation(value = "Ajouter un apprenant à une formation", notes = "Ajoute un apprenant existant à une formation spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Apprenant ajouté avec succès"),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formation ou apprenant non trouvé"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Ajout impossible - Formation complète")
    })
    public ResponseEntity<ApiResponse> addApprenant(
            @ApiParam(value = "ID de la formation", required = true) @PathVariable Long id,
            @ApiParam(value = "ID de l'apprenant", required = true) @PathVariable Long apprenantId) {
        logger.info("Adding apprenant {} to formation {}", apprenantId, id);
        boolean added = formationService.addApprenantToFormation(id, apprenantId);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant ajouté à la formation avec succès", added));
    }

    @DeleteMapping("/{id}/apprenants/{apprenantId}")
    @ApiOperation(value = "Retirer un apprenant d'une formation", notes = "Retire un apprenant d'une formation spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Apprenant retiré avec succès"),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formation ou apprenant non trouvé")
    })
    public ResponseEntity<ApiResponse> removeApprenant(
            @ApiParam(value = "ID de la formation", required = true) @PathVariable Long id,
            @ApiParam(value = "ID de l'apprenant", required = true) @PathVariable Long apprenantId) {
        logger.info("Removing apprenant {} from formation {}", apprenantId, id);
        boolean removed = formationService.removeApprenantFromFormation(id, apprenantId);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant retiré de la formation avec succès", removed));
    }

    @PutMapping("/{id}/status/{status}")
    @ApiOperation(value = "Mettre à jour le statut d'une formation", notes = "Met à jour le statut d'une formation existante")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Statut mis à jour avec succès"),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formation non trouvée"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Statut invalide")
    })
    public ResponseEntity<ApiResponse> updateStatus(
            @ApiParam(value = "ID de la formation", required = true) @PathVariable Long id,
            @ApiParam(value = "Nouveau statut", required = true) @PathVariable FormationStatus status) {
        logger.info("Updating status of formation {} to {}", id, status);
        formationService.updateStatus(id, status);
        return ResponseEntity.ok(new ApiResponse(true, "Statut de la formation mis à jour avec succès", null));
    }

    @GetMapping("/niveau/{niveau}")
    @ApiOperation(value = "Obtenir les formations planifiées par niveau", notes = "Récupère la liste des formations planifiées pour un niveau spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des formations récupérée avec succès"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Niveau invalide")
    })
    public ResponseEntity<List<FormationDTO>> findPlannedByNiveau(
            @ApiParam(value = "Niveau de la formation", required = true) @PathVariable String niveau) {
        logger.info("Fetching formations for niveau: {}", niveau);
        return ResponseEntity.ok(formationService.findPlannedFormationsByNiveau(niveau));
    }

    @GetMapping("/search")
    @ApiOperation(value = "Rechercher des formations par titre", notes = "Recherche des formations par titre avec pagination")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Résultats de recherche récupérés avec succès")
    })
    public ResponseEntity<Page<FormationDTO>> searchByTitre(
            @ApiParam(value = "Titre à rechercher", required = true) @RequestParam String titre,
            @ApiParam(value = "Informations de pagination") Pageable pageable) {
        logger.info("Searching formations with title: {}", titre);
        return ResponseEntity.ok(formationService.searchByTitre(titre, pageable));
    }

    @GetMapping("/{id}/full")
    @ApiOperation(value = "Vérifier si une formation est complète", notes = "Vérifie si une formation a atteint sa capacité maximale d'apprenants")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Vérification effectuée avec succès"),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formation non trouvée")
    })
    public ResponseEntity<ApiResponse> isFormationFull(
            @ApiParam(value = "ID de la formation", required = true) @PathVariable Long id) {
        logger.info("Checking if formation {} is full", id);
        boolean isFull = formationService.isFormationFull(id);
        return ResponseEntity.ok(new ApiResponse(true, "Vérification effectuée", isFull));
    }

}
