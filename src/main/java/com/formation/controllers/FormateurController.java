package com.formation.controllers;

import com.formation.dto.FormateurDTO;
import com.formation.dto.response.ApiResponse;
import com.formation.services.interfaces.IFormateurService;
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
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/formateurs")
@RequiredArgsConstructor
@Api(tags = "Gestion des Formateurs", description = "API pour la gestion des formateurs")
@Validated
public class FormateurController {
    private static final Logger logger = LoggerFactory.getLogger(FormateurController.class);
    private final IFormateurService formateurService;

    @PostMapping
    @ApiOperation(value = "Créer un nouveau formateur", notes = "Crée un nouveau formateur avec les informations fournies")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 201, message = "Formateur créé avec succès", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Données du formateur invalides"),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    public ResponseEntity<ApiResponse> create(
            @ApiParam(value = "Données du formateur à créer", required = true) @Valid @RequestBody FormateurDTO formateurDTO) {
        logger.info("Creating new formateur: {}", formateurDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Formateur créé avec succès",
                        formateurService.save(formateurDTO)));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Mettre à jour un formateur", notes = "Met à jour les informations d'un formateur existant")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur mis à jour avec succès", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formateur non trouvé"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Données invalides")
    })
    public ResponseEntity<ApiResponse> update(
            @ApiParam(value = "ID du formateur", required = true) @PathVariable @NotNull Long id,
            @ApiParam(value = "Nouvelles données du formateur", required = true) @Valid @RequestBody FormateurDTO formateurDTO) {
        logger.info("Updating formateur with id: {}", id);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur mis à jour avec succès",
                formateurService.update(id, formateurDTO)));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Supprimer un formateur", notes = "Supprime un formateur existant par son ID")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur supprimé avec succès"),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formateur non trouvé")
    })
    public ResponseEntity<ApiResponse> delete(
            @ApiParam(value = "ID du formateur à supprimer", required = true) @PathVariable Long id) {
        logger.info("Deleting formateur with id: {}", id);
        formateurService.delete(id);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur supprimé avec succès", null));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Obtenir un formateur par son ID", notes = "Récupère les détails d'un formateur spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur trouvé", response = FormateurDTO.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formateur non trouvé")
    })
    public ResponseEntity<FormateurDTO> findById(
            @ApiParam(value = "ID du formateur", required = true) @PathVariable Long id) {
        logger.info("Fetching formateur with id: {}", id);
        return formateurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation(value = "Obtenir tous les formateurs", notes = "Récupère la liste complète des formateurs")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des formateurs récupérée avec succès", response = List.class)
    })
    public ResponseEntity<List<FormateurDTO>> findAll() {
        logger.info("Fetching all formateurs");
        return ResponseEntity.ok(formateurService.findAll());
    }

    @GetMapping("/page")
    @ApiOperation(value = "Obtenir les formateurs avec pagination", notes = "Récupère une page de formateurs selon les paramètres de pagination")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Page de formateurs récupérée avec succès", response = Page.class)
    })
    public ResponseEntity<Page<FormateurDTO>> findAllPaginated(
            @ApiParam(value = "Informations de pagination") Pageable pageable) {
        logger.info("Fetching formateurs page: {}", pageable.getPageNumber());
        return ResponseEntity.ok(formateurService.findAll(pageable));
    }

    @GetMapping("/email/{email}")
    @ApiOperation(value = "Obtenir un formateur par son email", notes = "Recherche un formateur spécifique par son adresse email")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur trouvé", response = FormateurDTO.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formateur non trouvé")
    })
    public ResponseEntity<FormateurDTO> findByEmail(
            @ApiParam(value = "Email du formateur", required = true) @PathVariable String email) {
        logger.info("Fetching formateur by email: {}", email);
        return formateurService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/specialite/{specialite}")
    @ApiOperation(value = "Obtenir les formateurs par spécialité", notes = "Récupère la liste des formateurs ayant une spécialité spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Liste des formateurs récupérée avec succès", response = List.class),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Spécialité invalide")
    })
    public ResponseEntity<List<FormateurDTO>> findBySpecialite(
            @ApiParam(value = "Spécialité recherchée", required = true) @PathVariable String specialite) {
        logger.info("Fetching formateurs by specialite: {}", specialite);
        return ResponseEntity.ok(formateurService.findBySpecialite(specialite));
    }

    @GetMapping("/search")
    @ApiOperation(value = "Rechercher des formateurs", notes = "Recherche des formateurs par nom ou prénom avec pagination")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Recherche effectuée avec succès", response = Page.class),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Paramètres de recherche invalides")
    })
    public ResponseEntity<Page<FormateurDTO>> searchByNomOrPrenom(
            @ApiParam(value = "Terme de recherche", required = true) @RequestParam String term,
            @ApiParam(value = "Informations de pagination") Pageable pageable) {
        logger.info("Searching formateurs with term: {}", term);
        return ResponseEntity.ok(formateurService.searchByNomOrPrenom(term, pageable));
    }

    @PostMapping("/{id}/classes/{classeId}")
    @ApiOperation(value = "Assigner un formateur à une classe", notes = "Assigne un formateur existant à une classe spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur assigné avec succès", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formateur ou classe non trouvé"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Assignation impossible")
    })
    public ResponseEntity<ApiResponse> assignToClasse(
            @ApiParam(value = "ID du formateur", required = true) @PathVariable Long id,
            @ApiParam(value = "ID de la classe", required = true) @PathVariable Long classeId) {
        logger.info("Assigning formateur {} to classe {}", id, classeId);
        formateurService.assignToClasse(id, classeId);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur assigné à la classe avec succès", null));
    }

    @DeleteMapping("/{id}/classes")
    @ApiOperation(value = "Retirer un formateur d'une classe", notes = "Retire un formateur de sa classe actuelle")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur retiré avec succès", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formateur non trouvé")
    })
    public ResponseEntity<ApiResponse> removeFromClasse(
            @ApiParam(value = "ID du formateur", required = true) @PathVariable Long id) {
        logger.info("Removing formateur {} from classe", id);
        formateurService.removeFromClasse(id);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur retiré de la classe avec succès", null));
    }

    @PostMapping("/{id}/formations/{formationId}")
    @ApiOperation(value = "Assigner un formateur à une formation", notes = "Assigne un formateur existant à une formation spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur assigné avec succès", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formateur ou formation non trouvé"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Assignation impossible")
    })
    public ResponseEntity<ApiResponse> assignToFormation(
            @ApiParam(value = "ID du formateur", required = true) @PathVariable @NotNull Long id,
            @ApiParam(value = "ID de la formation", required = true) @PathVariable @NotNull Long formationId) {
        logger.info("Assigning formateur {} to formation {}", id, formationId);
        formateurService.assignToFormation(id, formationId);
        return ResponseEntity.ok(new ApiResponse(true,
                "Formateur assigné à la formation avec succès", null));
    }

    @DeleteMapping("/{id}/formations/{formationId}")
    @ApiOperation(value = "Retirer un formateur d'une formation", notes = "Retire un formateur d'une formation spécifique")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Formateur retiré avec succès", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Formateur ou formation non trouvé"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Retrait impossible")
    })
    public ResponseEntity<ApiResponse> removeFromFormation(
            @ApiParam(value = "ID du formateur", required = true) @PathVariable Long id,
            @ApiParam(value = "ID de la formation", required = true) @PathVariable Long formationId) {
        logger.info("Removing formateur {} from formation {}", id, formationId);
        formateurService.removeFromFormation(id, formationId);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur retiré de la formation avec succès", null));
    }
}
