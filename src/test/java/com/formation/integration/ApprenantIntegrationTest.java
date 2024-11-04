package com.formation.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.dto.ApprenantDTO;
import com.formation.models.Apprenant;
import com.formation.models.Formation;
import com.formation.models.FormationStatus;
import com.formation.models.NiveauFormation;
import com.formation.repositories.ApprenantRepository;
import com.formation.repositories.ClasseRepository;
import com.formation.repositories.FormationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApprenantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApprenantRepository apprenantRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        formationRepository.deleteAll();
        classeRepository.deleteAll();
        apprenantRepository.deleteAll();
    }

    @Test
    void createApprenant_ShouldReturnCreated() throws Exception {
        ApprenantDTO apprenantDTO = createValidApprenantDTO();

        mockMvc.perform(post("/api/apprenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(apprenantDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(apprenantDTO.getEmail()));
    }

    @Test
    void updateApprenant_ShouldReturnUpdated() throws Exception {
        Apprenant existingApprenant = createAndSaveApprenant();
        ApprenantDTO updateDTO = createValidApprenantDTO();
        updateDTO.setNiveau(NiveauFormation.INTERMEDIAIRE);

        mockMvc.perform(put("/api/apprenants/{id}", existingApprenant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.niveau").value("INTERMEDIAIRE"));
    }

    @Test
    void findByNiveau_ShouldReturnApprenants() throws Exception {
        Apprenant apprenant = createAndSaveApprenant();

        mockMvc.perform(get("/api/apprenants/niveau/{niveau}", NiveauFormation.DEBUTANT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].niveau").value(NiveauFormation.DEBUTANT.toString()));
    }

    @Test
    void searchByNomOrPrenom_ShouldReturnPageOfApprenants() throws Exception {
        Apprenant apprenant = createAndSaveApprenant();

        mockMvc.perform(get("/api/apprenants/search")
                .param("term", "Dupont")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nom").value(apprenant.getNom()));
    }

    @Test
    void assignToFormation_ShouldSucceed() throws Exception {
        Apprenant apprenant = createAndSaveApprenant();
        Formation formation = createAndSaveFormation();

        mockMvc.perform(post("/api/apprenants/{id}/formations/{formationId}",
                apprenant.getId(), formation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void removeFromFormation_ShouldSucceed() throws Exception {
        Apprenant apprenant = createAndSaveApprenant();
        Formation formation = createAndSaveFormation();

        // First assign to formation
        mockMvc.perform(post("/api/apprenants/{id}/formations/{formationId}",
                apprenant.getId(), formation.getId()));

        // Then remove from formation
        mockMvc.perform(delete("/api/apprenants/{id}/formations/{formationId}",
                apprenant.getId(), formation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private ApprenantDTO createValidApprenantDTO() {
        return ApprenantDTO.builder()
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@email.com")
                .niveau(NiveauFormation.DEBUTANT)
                .build();
    }

    private Apprenant createAndSaveApprenant() {
        Apprenant apprenant = Apprenant.builder()
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@email.com")
                .niveau(NiveauFormation.DEBUTANT)
                .formations(new HashSet<>())
                .build();
        return apprenantRepository.save(apprenant);
    }

    private Formation createAndSaveFormation() {
        Formation formation = Formation.builder()
                .titre("Formation Test")
                .niveau(NiveauFormation.DEBUTANT)
                .capaciteMin(1)
                .capaciteMax(20)
                .dateDebut(LocalDateTime.now().plusDays(1))
                .dateFin(LocalDateTime.now().plusDays(5))
                .statut(FormationStatus.PLANIFIEE)
                .apprenants(new HashSet<>())
                .build();
        return formationRepository.save(formation);
    }

}