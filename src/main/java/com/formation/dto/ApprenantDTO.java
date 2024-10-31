package com.formation.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Représentation d'un apprenant dans le système")
public class ApprenantDTO {
    @ApiModelProperty(value = "Identifiant unique de l'apprenant", example = "1", position = 1)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @ApiModelProperty(value = "Nom de l'apprenant", example = "Dupont", required = true, position = 2)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @ApiModelProperty(value = "Prénom de l'apprenant", example = "Jean", required = true, position = 3)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @ApiModelProperty(value = "Email de l'apprenant", example = "jean.dupont@email.com", required = true, position = 4)
    private String email;

    @NotBlank(message = "Le niveau est obligatoire")
    @ApiModelProperty(value = "Niveau d'étude", example = "DEBUTANT", required = true, allowableValues = "DEBUTANT,INTERMEDIAIRE,AVANCE", position = 5)
    private String niveau;

    @ApiModelProperty(value = "IDs des formations auxquelles l'apprenant est inscrit", example = "[1, 2, 3]", position = 6)
    private Set<Long> formationIds;

    @ApiModelProperty(value = "ID de la classe de l'apprenant", example = "1", position = 7)
    private Long classeId;
}