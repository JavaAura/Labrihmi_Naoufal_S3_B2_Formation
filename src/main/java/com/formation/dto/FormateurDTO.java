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
@ApiModel(description = "Représentation d'un formateur dans le système")
public class FormateurDTO {
    @ApiModelProperty(value = "Identifiant unique du formateur", example = "1", position = 1)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @ApiModelProperty(value = "Nom du formateur", example = "Martin", required = true, position = 2)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @ApiModelProperty(value = "Prénom du formateur", example = "Pierre", required = true, position = 3)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @ApiModelProperty(value = "Email du formateur", example = "pierre.martin@formation.com", required = true, position = 4)
    private String email;

    @NotBlank(message = "La spécialité est obligatoire")
    @ApiModelProperty(value = "Spécialité du formateur", example = "JAVA", required = true, position = 5)
    private String specialite;

    @ApiModelProperty(value = "Indique si le formateur est disponible", example = "true", position = 6)
    private boolean disponible;

    @ApiModelProperty(value = "IDs des classes assignées", example = "[1, 2]", position = 7)
    private Set<Long> classeIds;

    @ApiModelProperty(value = "IDs des formations assignées", example = "[1, 2, 3]", position = 8)
    private Set<Long> formationIds;

    @ApiModelProperty(value = "ID de la classe assignée", example = "1", position = 9)
    private Long classeId;

    public Long getClasseId() {
        return classeId;
    }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }
}
