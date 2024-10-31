package com.formation.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Représentation d'une classe dans le système")
public class ClasseDTO {
    @ApiModelProperty(value = "Identifiant unique de la classe", example = "1", position = 1)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @ApiModelProperty(value = "Nom de la classe", example = "Classe Java 2024", required = true, position = 2)
    private String nom;

    @NotBlank(message = "Le numéro de salle est obligatoire")
    @ApiModelProperty(value = "Numéro de la salle de classe", example = "B204", required = true, position = 3)
    private String numSalle;

    @ApiModelProperty(value = "IDs des apprenants dans la classe", example = "[1, 2, 3]", position = 4)
    private Set<Long> apprenantIds;

    @ApiModelProperty(value = "IDs des formateurs assignés à la classe", example = "[1, 2]", position = 5)
    private Set<Long> formateurIds;
}
