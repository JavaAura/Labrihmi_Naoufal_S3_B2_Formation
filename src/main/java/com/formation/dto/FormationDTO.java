package com.formation.dto;

import com.formation.models.FormationStatus;
import com.formation.models.NiveauFormation;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Représentation d'une formation dans le système")
public class FormationDTO {
    @ApiModelProperty(value = "Identifiant unique de la formation", example = "1", position = 1)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caractères")
    @ApiModelProperty(value = "Titre de la formation", example = "Formation Java Spring", required = true, position = 2)
    private String titre;

    @NotNull(message = "Le niveau est obligatoire")
    @ApiModelProperty(value = "Niveau de la formation", example = "INTERMEDIAIRE", required = true, allowableValues = "DEBUTANT,INTERMEDIAIRE,AVANCE", position = 3)
    private NiveauFormation niveau;

    @NotBlank(message = "Les prérequis sont obligatoires")
    @ApiModelProperty(value = "Prérequis pour suivre la formation", example = "Connaissances en Java", required = true, position = 4)
    private String prerequis;

    @Min(value = 1, message = "La capacité minimale doit être d'au moins 1")
    @ApiModelProperty(value = "Capacité minimale de participants", example = "5", required = true, position = 5)
    private int capaciteMin;

    @Min(value = 1, message = "La capacité maximale doit être d'au moins 1")
    @ApiModelProperty(value = "Capacité maximale de participants", example = "20", required = true, position = 6)
    private int capaciteMax;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    @ApiModelProperty(value = "Date de début de la formation", example = "2024-12-31T09:00:00", required = true, position = 7)
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    @ApiModelProperty(value = "Date de fin de la formation", example = "2025-01-31T17:00:00", required = true, position = 8)
    private LocalDateTime dateFin;

    @ApiModelProperty(value = "ID du formateur responsable", example = "1", required = false, position = 9)
    private Long formateurId;

    @Builder.Default
    @ApiModelProperty(value = "IDs des apprenants inscrits à la formation")
    private Set<Long> apprenantIds = new HashSet<>();

    @NotNull(message = "Le statut est obligatoire")
    @ApiModelProperty(value = "Statut de la formation", example = "EN_COURS", required = true, allowableValues = "PLANIFIEE,EN_COURS,TERMINEE,ANNULEE", position = 11)
    private FormationStatus statut;
}
