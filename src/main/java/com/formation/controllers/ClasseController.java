package com.formation.controllers;

import com.formation.dto.ClasseDTO;
import com.formation.dto.response.ApiResponse;
import com.formation.services.interfaces.IClasseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Api(tags = "Gestion des Classes")
public class ClasseController {
    private static final Logger logger = LoggerFactory.getLogger(ClasseController.class);
    private final IClasseService classeService;

    @PostMapping
    @ApiOperation("Créer une nouvelle classe")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ClasseDTO classeDTO) {
        logger.info("Creating new classe: {}", classeDTO.getNom());
        ClasseDTO created = classeService.save(classeDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Classe créée avec succès", created));
    }

    @PutMapping("/{id}")
    @ApiOperation("Mettre à jour une classe")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ClasseDTO classeDTO) {
        logger.info("Updating classe with id: {}", id);
        ClasseDTO updated = classeService.update(id, classeDTO);
        return ResponseEntity.ok(new ApiResponse(true, "Classe mise à jour avec succès", updated));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Supprimer une classe")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        logger.info("Deleting classe with id: {}", id);
        classeService.delete(id);
        return ResponseEntity.ok(new ApiResponse(true, "Classe supprimée avec succès", null));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obtenir une classe par son ID")
    public ResponseEntity<ClasseDTO> findById(@PathVariable Long id) {
        return classeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation("Obtenir toutes les classes (avec pagination)")
    public ResponseEntity<Page<ClasseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(classeService.findAll(pageable));
    }

    @GetMapping("/search")
    @ApiOperation("Rechercher des classes par nom")
    public ResponseEntity<List<ClasseDTO>> searchByNom(@RequestParam String nom) {
        return ResponseEntity.ok(classeService.findByNomContaining(nom));
    }

    @GetMapping("/available")
    @ApiOperation("Obtenir les classes disponibles")
    public ResponseEntity<List<ClasseDTO>> findAvailableClasses(@RequestParam int maxCapacity) {
        return ResponseEntity.ok(classeService.findAvailableClasses(maxCapacity));
    }

    @PostMapping("/{id}/apprenants/{apprenantId}")
    @ApiOperation("Assigner un apprenant à une classe")
    public ResponseEntity<ApiResponse> assignApprenant(@PathVariable Long id, @PathVariable Long apprenantId) {
        classeService.assignApprenantToClasse(id, apprenantId);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant assigné à la classe avec succès", null));
    }

    @DeleteMapping("/{id}/apprenants/{apprenantId}")
    @ApiOperation("Retirer un apprenant d'une classe")
    public ResponseEntity<ApiResponse> removeApprenant(@PathVariable Long id, @PathVariable Long apprenantId) {
        classeService.removeApprenantFromClasse(id, apprenantId);
        return ResponseEntity.ok(new ApiResponse(true, "Apprenant retiré de la classe avec succès", null));
    }

    @PostMapping("/{id}/formateurs/{formateurId}")
    @ApiOperation("Assigner un formateur à une classe")
    public ResponseEntity<ApiResponse> assignFormateur(@PathVariable Long id, @PathVariable Long formateurId) {
        classeService.assignFormateurToClasse(id, formateurId);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur assigné à la classe avec succès", null));
    }

    @DeleteMapping("/{id}/formateurs/{formateurId}")
    @ApiOperation("Retirer un formateur d'une classe")
    public ResponseEntity<ApiResponse> removeFormateur(@PathVariable Long id, @PathVariable Long formateurId) {
        classeService.removeFormateurFromClasse(id, formateurId);
        return ResponseEntity.ok(new ApiResponse(true, "Formateur retiré de la classe avec succès", null));
    }
}
