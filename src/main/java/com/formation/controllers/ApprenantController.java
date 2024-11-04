package com.formation.controllers;

import com.formation.dto.ApprenantDTO;
import com.formation.dto.response.ApiResponse;
import com.formation.exceptions.ResourceNotFoundException;
import com.formation.services.interfaces.IApprenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/apprenants")
@RequiredArgsConstructor
@Api(tags = "Gestion des Apprenants")
@Validated
public class ApprenantController {
        private static final Logger logger = LoggerFactory.getLogger(ApprenantController.class);
        private final IApprenantService apprenantService;

        @PostMapping
        @ApiOperation(value = "Créer un nouvel apprenant", notes = "Crée un nouvel apprenant avec les informations fournies")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 201, message = "Apprenant créé avec succès", response = ApiResponse.class),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Données de l'apprenant invalides"),
                        @io.swagger.annotations.ApiResponse(code = 500, message = "Erreur interne du serveur")
        })
        @ResponseStatus(HttpStatus.CREATED)
        public ResponseEntity<ApiResponse<ApprenantDTO>> create(
                        @ApiParam(value = "Données de l'apprenant à créer", required = true) @Valid @RequestBody ApprenantDTO apprenantDTO) {
                logger.info("Creating new apprenant: {}", apprenantDTO.getEmail());
                ApprenantDTO created = apprenantService.save(apprenantDTO);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(new ApiResponse<>(true, "Apprenant créé avec succès", created));
        }

        @PutMapping("/{id}")
        @ApiOperation(value = "Mettre à jour un apprenant", notes = "Met à jour les informations d'un apprenant existant")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Apprenant mis à jour avec succès", response = ApiResponse.class),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Apprenant non trouvé"),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Données invalides")
        })
        public ResponseEntity<ApiResponse<ApprenantDTO>> update(
                        @ApiParam(value = "ID de l'apprenant", required = true) @PathVariable @NotNull Long id,
                        @ApiParam(value = "Nouvelles données de l'apprenant", required = true) @Valid @RequestBody ApprenantDTO apprenantDTO) {
                logger.info("Updating apprenant with id: {}", id);
                try {
                        ApprenantDTO updated = apprenantService.update(id, apprenantDTO);
                        return ResponseEntity.ok(new ApiResponse<>(true, "Apprenant mis à jour avec succès", updated));
                } catch (ResourceNotFoundException | ValidationException e) {
                        throw e;
                } catch (Exception e) {
                        logger.error("Error updating apprenant: ", e);
                        throw new ValidationException("Erreur lors de la mise à jour de l'apprenant");
                }
        }

        @DeleteMapping("/{id}")
        @ApiOperation(value = "Supprimer un apprenant", notes = "Supprime un apprenant existant par son ID")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Apprenant supprimé avec succès"),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Apprenant non trouvé")
        })
        public ResponseEntity<ApiResponse<Void>> delete(
                        @ApiParam(value = "ID de l'apprenant à supprimer", required = true) @PathVariable Long id) {
                logger.info("Deleting apprenant with id: {}", id);
                apprenantService.delete(id);
                return ResponseEntity.ok(new ApiResponse<>(true, "Apprenant supprimé avec succès", null));
        }

        @GetMapping("/{id}")
        @ApiOperation(value = "Obtenir un apprenant par son ID", notes = "Récupère les détails d'un apprenant spécifique")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Apprenant trouvé", response = ApiResponse.class),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Apprenant non trouvé")
        })
        public ResponseEntity<ApiResponse<ApprenantDTO>> findById(
                        @ApiParam(value = "ID de l'apprenant", required = true) @PathVariable Long id) {
                logger.info("Fetching apprenant with id: {}", id);
                return apprenantService.findById(id)
                                .map(apprenant -> ResponseEntity
                                                .ok(new ApiResponse<>(true, "Apprenant trouvé", apprenant)))
                                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(false, "Apprenant non trouvé", null)));
        }

        @GetMapping
        @ApiOperation(value = "Obtenir tous les apprenants", notes = "Récupère la liste paginée des apprenants")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des apprenants récupérée avec succès", response = ApiResponse.class)
        })
        public ResponseEntity<Page<ApprenantDTO>> findAll(
                        @ApiParam(value = "Informations de pagination") Pageable pageable) {
                logger.info("Fetching page {} of apprenants", pageable.getPageNumber());
                return ResponseEntity.ok(apprenantService.findAll(pageable));
        }

        @GetMapping("/search")
        @ApiOperation(value = "Rechercher des apprenants", notes = "Recherche des apprenants par nom ou prénom")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Recherche effectuée avec succès", response = Page.class),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Paramètre de recherche invalide")
        })
        public ResponseEntity<Page<ApprenantDTO>> search(
                        @ApiParam(value = "Terme de recherche", required = true) @RequestParam String term,
                        @ApiParam(value = "Informations de pagination") Pageable pageable) {
                logger.info("Searching apprenants with term: {}", term);
                return ResponseEntity.ok(apprenantService.searchByNomOrPrenom(term, pageable));
        }

        @GetMapping("/niveau/{niveau}")
        @ApiOperation(value = "Obtenir les apprenants par niveau", notes = "Récupère la liste des apprenants d'un niveau spécifique")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des apprenants récupérée avec succès", response = ApiResponse.class),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Niveau invalide")
        })
        public ResponseEntity<ApiResponse<List<ApprenantDTO>>> findByNiveau(
                        @ApiParam(value = "Niveau d'étude", required = true) @PathVariable String niveau) {
                logger.info("Fetching apprenants by niveau: {}", niveau);
                List<ApprenantDTO> apprenants = apprenantService.findByNiveau(niveau);
                return ResponseEntity.ok(new ApiResponse<>(true,
                                apprenants.isEmpty() ? "Aucun apprenant trouvé pour ce niveau"
                                                : "Liste des apprenants récupérée avec succès",
                                apprenants));
        }

        @PostMapping("/{id}/formations/{formationId}")
        @ApiOperation(value = "Inscrire un apprenant à une formation", notes = "Inscrit un apprenant existant à une formation spécifique")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Inscription réussie", response = ApiResponse.class),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Apprenant ou formation non trouvé"),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Inscription impossible")
        })
        public ResponseEntity<ApiResponse<ApprenantDTO>> assignToFormation(
                        @ApiParam(value = "ID de l'apprenant", required = true) @PathVariable Long id,
                        @ApiParam(value = "ID de la formation", required = true) @PathVariable Long formationId) {
                logger.info("Assigning apprenant {} to formation {}", id, formationId);
                apprenantService.assignToFormation(id, formationId);
                return ResponseEntity.ok(new ApiResponse<>(true, "Apprenant inscrit à la formation avec succès",
                                apprenantService.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                                                "Apprenant not found with id: " + id))));
        }

        @DeleteMapping("/{id}/formations/{formationId}")
        @ApiOperation(value = "Désinscrire un apprenant d'une formation", notes = "Retire un apprenant d'une formation spécifique")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Désinscription réussie", response = ApiResponse.class),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Apprenant ou formation non trouvé"),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Désinscription impossible")
        })
        public ResponseEntity<ApiResponse<Void>> removeFromFormation(
                        @ApiParam(value = "ID de l'apprenant", required = true) @PathVariable Long id,
                        @ApiParam(value = "ID de la formation", required = true) @PathVariable Long formationId) {
                logger.info("Removing apprenant {} from formation {}", id, formationId);
                apprenantService.removeFromFormation(id, formationId);
                return ResponseEntity
                                .ok(new ApiResponse<>(true, "Apprenant désinscrit de la formation avec succès", null));
        }
}