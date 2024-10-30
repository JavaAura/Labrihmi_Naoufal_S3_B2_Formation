package com.formation.dto;

import com.formation.models.FormationStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationDTO {
    private Long id;


    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le niveau est obligatoire")
    private String niveau;

    private String prerequis;

    @Min(value = 1, message = "La capacité minimale doit être supérieure à 0")
    private int capaciteMin;

    @Min(value = 1, message = "La capacité maximale doit être supérieure à 0")
    private int capaciteMax;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    private LocalDateTime dateFin;

    private Long formateurId;
    private Set<Long> apprenantIds;
    private FormationStatus statut;
}
