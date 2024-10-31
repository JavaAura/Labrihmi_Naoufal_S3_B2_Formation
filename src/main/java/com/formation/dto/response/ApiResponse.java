package com.formation.dto.response;

import com.formation.dto.ApprenantDTO;
import com.formation.dto.ClasseDTO;
import com.formation.dto.FormateurDTO;
import com.formation.dto.FormationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Format de réponse standard de l'API")
public class ApiResponse<T> {
    @Schema(description = "Indique si l'opération a réussi", example = "true")
    private boolean success;

    @Schema(description = "Message descriptif du résultat", example = "Opération réussie")
    private String message;

    @Schema(description = "Données de la réponse", anyOf = {
            ApprenantDTO.class,
            FormateurDTO.class,
            FormationDTO.class,
            ClasseDTO.class
    })
    private T data;
}