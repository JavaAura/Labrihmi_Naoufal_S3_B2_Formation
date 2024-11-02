package com.formation.controllers;

import com.formation.dto.ClasseDTO;
import com.formation.dto.response.ApiResponse;
import com.formation.services.interfaces.IClasseService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Api(tags = "Gestion des Classes")
@Validated
public class ClasseController {
        private static final Logger logger = LoggerFactory.getLogger(ClasseController.class);
        private final IClasseService classeService;

        @PostMapping
        @ApiOperation(value = "Créer une nouvelle classe", notes = "Crée une nouvelle classe avec les informations fournies. La capacité doit être positive.")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 201, message = "Classe créée avec succès", response = ApiResponse.class),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Données de la classe invalides"),
                        @io.swagger.annotations.ApiResponse(code = 500, message = "Erreur interne du serveur")
        })
        public ResponseEntity<ApiResponse> create(
                        @ApiParam(value = "Données de la classe à créer", required = true) @Valid @RequestBody ClasseDTO classeDTO) {
                try {
                        logger.info("Creating new classe: {}", classeDTO.getNom());
                        ClasseDTO savedClasse = classeService.save(classeDTO);
                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(new ApiResponse(true, "Classe créée avec succès", savedClasse));
                } catch (ValidationException e) {
                        logger.error("Validation error while creating classe: {}", e.getMessage());
                        return ResponseEntity.badRequest()
                                        .body(new ApiResponse(false, e.getMessage(), null));
                } catch (Exception e) {
                        logger.error("Unexpected error while creating classe: ", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse(false, "Une erreur inattendue s'est produite", null));
                }
        }

        @PutMapping("/{id}")
        @ApiOperation(value = "Mettre à jour une classe", notes = "Met à jour les informations d'une classe existante")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Classe mise à jour avec succès"),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Classe non trouvée"),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Données invalides")
        })
        public ResponseEntity<ApiResponse> update(
                        @ApiParam(value = "ID de la classe", required = true) @PathVariable @NotNull Long id,
                        @ApiParam(value = "Nouvelles données de la classe", required = true) @Valid @RequestBody ClasseDTO classeDTO) {
                logger.info("Updating classe with id: {}", id);
                return ResponseEntity.ok(new ApiResponse(true, "Classe mise à jour avec succès",
                                classeService.update(id, classeDTO)));
        }

        @DeleteMapping("/{id}")
        @ApiOperation(value = "Supprimer une classe", notes = "Supprime une classe existante par son ID")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Classe supprimée avec succès"),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Classe non trouvée")
        })
        public ResponseEntity<ApiResponse> delete(
                        @ApiParam(value = "ID de la classe à supprimer", required = true) @PathVariable Long id) {
                logger.info("Deleting classe with id: {}", id);
                classeService.delete(id);
                return ResponseEntity.ok(new ApiResponse(true, "Classe supprimée avec succès", null));
        }

        @GetMapping("/{id}")
        @ApiOperation(value = "Obtenir une classe par son ID", notes = "Récupère les détails d'une classe spécifique")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Classe trouvée"),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Classe non trouvée")
        })
        public ResponseEntity<ClasseDTO> findById(
                        @ApiParam(value = "ID de la classe", required = true) @PathVariable Long id) {
                return classeService.findById(id)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping
        @ApiOperation(value = "Obtenir toutes les classes", notes = "Récupère la liste paginée des classes. Utilise la pagination pour de meilleures performances.")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des classes récupérée avec succès", response = Page.class),
                        @io.swagger.annotations.ApiResponse(code = 500, message = "Erreur interne du serveur")
        })
        public ResponseEntity<Page<ClasseDTO>> findAll(
                        @ApiParam(value = "Informations de pagination") Pageable pageable) {
                return ResponseEntity.ok(classeService.findAll(pageable));
        }

        @GetMapping("/search")
        @ApiOperation(value = "Rechercher des classes par nom", notes = "Recherche des classes dont le nom contient la chaîne spécifiée (insensible à la casse)")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Recherche effectuée avec succès", response = List.class),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Paramètre de recherche invalide")
        })
        public ResponseEntity<List<ClasseDTO>> searchByNom(
                        @ApiParam(value = "Nom à rechercher", required = true) @RequestParam String nom) {
                return ResponseEntity.ok(classeService.findByNomContaining(nom));
        }

        @GetMapping("/available")
        @ApiOperation(value = "Obtenir les classes disponibles", notes = "Récupère les classes ayant une capacité inférieure à la capacité maximale spécifiée")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des classes disponibles récupérée avec succès", response = List.class),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Capacité maximale invalide")
        })
        public ResponseEntity<List<ClasseDTO>> findAvailableClasses(
                        @ApiParam(value = "Capacité maximale", required = true) @RequestParam int maxCapacity) {
                return ResponseEntity.ok(classeService.findAvailableClasses(maxCapacity));
        }

        @PostMapping("/{id}/apprenants/{apprenantId}")
        @ApiOperation(value = "Assigner un apprenant à une classe", notes = "Ajoute un apprenant existant à une classe spécifique si la capacité le permet")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Apprenant assigné avec succès", response = ApiResponse.class),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Classe ou apprenant non trouvé"),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Assignation impossible - Capacité maximale atteinte")
        })
        public ResponseEntity<ApiResponse> assignApprenant(
                        @ApiParam(value = "ID de la classe", required = true) @PathVariable @NotNull Long id,
                        @ApiParam(value = "ID de l'apprenant", required = true) @PathVariable @NotNull Long apprenantId) {
                classeService.assignApprenantToClasse(id, apprenantId);
                return ResponseEntity.ok(new ApiResponse(true,
                                "Apprenant assigné à la classe avec succès", null));
        }

        @DeleteMapping("/{id}/apprenants/{apprenantId}")
        @ApiOperation(value = "Retirer un apprenant d'une classe", notes = "Retire un apprenant d'une classe spécifique")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Apprenant retiré avec succès"),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Classe ou apprenant non trouvé")
        })
        public ResponseEntity<ApiResponse> removeApprenant(
                        @ApiParam(value = "ID de la classe", required = true) @PathVariable Long id,
                        @ApiParam(value = "ID de l'apprenant", required = true) @PathVariable Long apprenantId) {
                classeService.removeApprenantFromClasse(id, apprenantId);
                return ResponseEntity.ok(new ApiResponse(true, "Apprenant retiré de la classe avec succès", null));
        }

        @PostMapping("/{id}/formateurs/{formateurId}")
        @ApiOperation(value = "Assigner un formateur à une classe", notes = "Ajoute un formateur existant à une classe spécifique")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur assigné avec succès"),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Classe ou formateur non trouvé"),
                        @io.swagger.annotations.ApiResponse(code = 400, message = "Assignation impossible")
        })
        public ResponseEntity<ApiResponse> assignFormateur(
                        @ApiParam(value = "ID de la classe", required = true) @PathVariable Long id,
                        @ApiParam(value = "ID du formateur", required = true) @PathVariable Long formateurId) {
                classeService.assignFormateurToClasse(id, formateurId);
                return ResponseEntity.ok(new ApiResponse(true, "Formateur assigné à la classe avec succès", null));
        }

        @DeleteMapping("/{id}/formateurs/{formateurId}")
        @ApiOperation(value = "Retirer un formateur d'une classe", notes = "Retire un formateur d'une classe spécifique")
        @ApiResponses(value = {
                        @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur retiré avec succès"),
                        @io.swagger.annotations.ApiResponse(code = 404, message = "Classe ou formateur non trouvé")
        })
        public ResponseEntity<ApiResponse> removeFormateur(
                        @ApiParam(value = "ID de la classe", required = true) @PathVariable Long id,
                        @ApiParam(value = "ID du formateur", required = true) @PathVariable Long formateurId) {
                classeService.removeFormateurFromClasse(id, formateurId);
                return ResponseEntity.ok(new ApiResponse(true, "Formateur retiré de la classe avec succès", null));
        }
}
