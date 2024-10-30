package com.formation.controllers;

import com.formation.dto.ApprenantDTO;
import com.formation.dto.response.ApiResponse;
import com.formation.services.interfaces.IApprenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/apprenants")
@RequiredArgsConstructor
@Api(tags = "Gestion des Apprenants")
public class ApprenantController {
    private static final Logger logger = LoggerFactory.getLogger(ApprenantController.class);
    private final IApprenantService apprenantService;

    @PostMapping
    @ApiOperation("Créer un nouvel apprenant")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ApprenantDTO apprenantDTO) {
        logger.info("Creating new apprenant: {}", apprenantDTO.getEmail());
        ApprenantDTO created = apprenantService.save(apprenantDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Apprenant créé avec succès", created));
    }

    @PutMapping("/{id}")
    @ApiOperation("Mettre à jour un apprenant")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ApprenantDTO apprenantDTO) {
        logger.info("Updating apprenant with id: {}", id);
        ApprenantDTO updated = apprenantService.update(id, apprenantDTO);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant mis à jour avec succès", updated));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Supprimer un apprenant")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        logger.info("Deleting apprenant with id: {}", id);
        apprenantService.delete(id);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant supprimé avec succès", null));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obtenir un apprenant par son ID")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        logger.info("Fetching apprenant with id: {}", id);
        return apprenantService.findById(id)
                .map(apprenant -> ResponseEntity.ok(new ApiResponse(true, "Apprenant trouvé", apprenant)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Apprenant non trouvé", null)));
    }

    @GetMapping
    @ApiOperation("Obtenir tous les apprenants (paginé)")
    public ResponseEntity<Page<ApprenantDTO>> findAll(Pageable pageable) {
        logger.info("Fetching page {} of apprenants", pageable.getPageNumber());
        return ResponseEntity.ok(apprenantService.findAll(pageable));
    }

    @GetMapping("/search")
    @ApiOperation("Rechercher des apprenants par nom ou prénom")
    public ResponseEntity<Page<ApprenantDTO>> search(@RequestParam String term, Pageable pageable) {
        logger.info("Searching apprenants with term: {}", term);
        return ResponseEntity.ok(apprenantService.searchByNomOrPrenom(term, pageable));
    }

    @GetMapping("/niveau/{niveau}")
    @ApiOperation("Obtenir les apprenants par niveau")
    public ResponseEntity<List<ApprenantDTO>> findByNiveau(@PathVariable String niveau) {
        logger.info("Fetching apprenants by niveau: {}", niveau);
        return ResponseEntity.ok(apprenantService.findByNiveau(niveau));
    }

    @PostMapping("/{id}/formations/{formationId}")
    @ApiOperation("Inscrire un apprenant à une formation")
    public ResponseEntity<ApiResponse> assignToFormation(@PathVariable Long id, @PathVariable Long formationId) {
        logger.info("Assigning apprenant {} to formation {}", id, formationId);
        apprenantService.assignToFormation(id, formationId);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant inscrit à la formation avec succès", null));
    }

    @DeleteMapping("/{id}/formations/{formationId}")
    @ApiOperation("Désinscrire un apprenant d'une formation")
    public ResponseEntity<ApiResponse> removeFromFormation(@PathVariable Long id, @PathVariable Long formationId) {
        logger.info("Removing apprenant {} from formation {}", id, formationId);
        apprenantService.removeFromFormation(id, formationId);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant désinscrit de la formation avec succès", null));
    }
}
