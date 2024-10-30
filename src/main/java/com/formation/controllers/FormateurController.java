package com.formation.controllers;

import com.formation.dto.FormateurDTO;
import com.formation.dto.response.ApiResponse;
import com.formation.services.interfaces.IFormateurService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/formateurs")
@Api(tags = "Formateur Management API")
@RequiredArgsConstructor
public class FormateurController {
    private static final Logger logger = LoggerFactory.getLogger(FormateurController.class);
    private final IFormateurService formateurService;

    @PostMapping
    @ApiOperation("Créer un nouveau formateur")
    public ResponseEntity<FormateurDTO> create(@Valid @RequestBody FormateurDTO formateurDTO) {
        logger.info("Creating new formateur with email: {}", formateurDTO.getEmail());
        return ResponseEntity.ok(formateurService.save(formateurDTO));
    }

    @PutMapping("/{id}")
    @ApiOperation("Mettre à jour un formateur")
    public ResponseEntity<FormateurDTO> update(@PathVariable Long id, @Valid @RequestBody FormateurDTO formateurDTO) {
        logger.info("Updating formateur with id: {}", id);
        return ResponseEntity.ok(formateurService.update(id, formateurDTO));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Supprimer un formateur")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        logger.info("Deleting formateur with id: {}", id);
        formateurService.delete(id);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur supprimé avec succès", null));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obtenir un formateur par son ID")
    public ResponseEntity<FormateurDTO> findById(@PathVariable Long id) {
        logger.info("Fetching formateur with id: {}", id);
        return formateurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation("Obtenir tous les formateurs")
    public ResponseEntity<List<FormateurDTO>> findAll() {
        logger.info("Fetching all formateurs");
        return ResponseEntity.ok(formateurService.findAll());
    }

    @GetMapping("/page")
    @ApiOperation("Obtenir les formateurs avec pagination")
    public ResponseEntity<Page<FormateurDTO>> findAllPaginated(Pageable pageable) {
        logger.info("Fetching formateurs page: {}", pageable.getPageNumber());
        return ResponseEntity.ok(formateurService.findAll(pageable));
    }

    @GetMapping("/email/{email}")
    @ApiOperation("Obtenir un formateur par son email")
    public ResponseEntity<FormateurDTO> findByEmail(@PathVariable String email) {
        logger.info("Fetching formateur by email: {}", email);
        return formateurService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/specialite/{specialite}")
    @ApiOperation("Obtenir les formateurs par spécialité")
    public ResponseEntity<List<FormateurDTO>> findBySpecialite(@PathVariable String specialite) {
        logger.info("Fetching formateurs by specialite: {}", specialite);
        return ResponseEntity.ok(formateurService.findBySpecialite(specialite));
    }

    @GetMapping("/search")
    @ApiOperation("Rechercher des formateurs par nom ou prénom")
    public ResponseEntity<Page<FormateurDTO>> searchByNomOrPrenom(
            @RequestParam String term, Pageable pageable) {
        logger.info("Searching formateurs with term: {}", term);
        return ResponseEntity.ok(formateurService.searchByNomOrPrenom(term, pageable));
    }

    @PostMapping("/{id}/classes/{classeId}")
    @ApiOperation("Assigner un formateur à une classe")
    public ResponseEntity<ApiResponse> assignToClasse(@PathVariable Long id, @PathVariable Long classeId) {
        logger.info("Assigning formateur {} to classe {}", id, classeId);
        formateurService.assignToClasse(id, classeId);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur assigné à la classe avec succès", null));
    }

    @DeleteMapping("/{id}/classes")
    @ApiOperation("Retirer un formateur d'une classe")
    public ResponseEntity<ApiResponse> removeFromClasse(@PathVariable Long id) {
        logger.info("Removing formateur {} from classe", id);
        formateurService.removeFromClasse(id);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur retiré de la classe avec succès", null));
    }

    @PostMapping("/{id}/formations/{formationId}")
    @ApiOperation("Assigner un formateur à une formation")
    public ResponseEntity<ApiResponse> assignToFormation(@PathVariable Long id, @PathVariable Long formationId) {
        logger.info("Assigning formateur {} to formation {}", id, formationId);
        formateurService.assignToFormation(id, formationId);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur assigné à la formation avec succès", null));
    }

    @DeleteMapping("/{id}/formations/{formationId}")
    @ApiOperation("Retirer un formateur d'une formation")
    public ResponseEntity<ApiResponse> removeFromFormation(@PathVariable Long id, @PathVariable Long formationId) {
        logger.info("Removing formateur {} from formation {}", id, formationId);
        formateurService.removeFromFormation(id, formationId);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur retiré de la formation avec succès", null));
    }
}
