package com.formation.services;

import com.formation.dto.ApprenantDTO;
import com.formation.models.Apprenant;
import com.formation.models.Classe;
import com.formation.models.Formation;
import com.formation.models.FormationStatus;
import com.formation.models.NiveauFormation;
import com.formation.repositories.ApprenantRepository;
import com.formation.repositories.ClasseRepository;
import com.formation.repositories.FormationRepository;
import com.formation.services.impl.ApprenantServiceImpl;
import com.formation.utils.ApprenantMapper;
import com.formation.validation.ApprenantValidator;
import com.formation.validation.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprenantServiceImplTest {
    @Mock
    private ApprenantRepository apprenantRepository;
    @Mock
    private ClasseRepository classeRepository;
    @Mock
    private FormationRepository formationRepository;
    @Mock
    private ApprenantMapper apprenantMapper;
    @Mock
    private ApprenantValidator apprenantValidator;

    @InjectMocks
    private ApprenantServiceImpl apprenantService;

    @Test
    void save_ShouldCreateApprenant_WhenValidInput() {
        // Given
        ApprenantDTO inputDto = createValidApprenantDTO();
        Apprenant apprenant = new Apprenant();
        Apprenant savedApprenant = new Apprenant();
        ApprenantDTO expectedDto = new ApprenantDTO();

        when(apprenantMapper.toEntity(inputDto)).thenReturn(apprenant);
        when(apprenantRepository.save(apprenant)).thenReturn(savedApprenant);
        when(apprenantMapper.toDTO(savedApprenant)).thenReturn(expectedDto);

        // When
        ApprenantDTO result = apprenantService.save(inputDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(apprenantValidator).validateForCreate(inputDto);
    }

    @Test
    void assignToFormation_ShouldThrowException_WhenFormationIsFull() {
        // Given
        Long apprenantId = 1L;
        Long formationId = 1L;
        Apprenant apprenant = new Apprenant();
        Formation formation = Formation.builder()
                .capaciteMax(1)
                .apprenants(new HashSet<>(Collections.singleton(new Apprenant())))
                .statut(FormationStatus.PLANIFIEE)
                .build();

        when(apprenantRepository.findById(apprenantId)).thenReturn(Optional.of(apprenant));
        when(formationRepository.findById(formationId)).thenReturn(Optional.of(formation));

        // When/Then
        assertThrows(ValidationException.class, () -> apprenantService.assignToFormation(apprenantId, formationId));
    }

    @Test
    void update_ShouldUpdateApprenant_WhenValidInput() {
        // Given
        Long id = 1L;
        ApprenantDTO inputDto = createValidApprenantDTO();
        Apprenant existingApprenant = new Apprenant();
        Apprenant updatedApprenant = new Apprenant();
        ApprenantDTO expectedDto = new ApprenantDTO();

        when(apprenantRepository.findById(id)).thenReturn(Optional.of(existingApprenant));
        when(apprenantRepository.save(existingApprenant)).thenReturn(updatedApprenant);
        when(apprenantMapper.toDTO(updatedApprenant)).thenReturn(expectedDto);

        // When
        ApprenantDTO result = apprenantService.update(id, inputDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(apprenantValidator).validateForUpdate(id, inputDto);
        verify(apprenantMapper).updateApprenantFromDTO(inputDto, existingApprenant);
    }

    @Test
    void assignToClasse_ShouldThrowException_WhenApprenantAlreadyHasClasse() {
        // Given
        Long apprenantId = 1L;
        Long classeId = 1L;
        Apprenant apprenant = new Apprenant();
        Classe existingClasse = new Classe();
        apprenant.setClasse(existingClasse);
        Classe newClasse = new Classe();

        when(apprenantRepository.findById(apprenantId)).thenReturn(Optional.of(apprenant));
        when(classeRepository.findById(classeId)).thenReturn(Optional.of(newClasse));

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> apprenantService.assignToClasse(apprenantId, classeId));

        assertThat(exception.getMessage()).isEqualTo("L'apprenant est déjà assigné à une classe");
    }

    @Test
    void removeFromClasse_ShouldRemoveClasseFromApprenant() {
        // Given
        Long apprenantId = 1L;
        Apprenant apprenant = new Apprenant();
        Classe classe = new Classe();
        apprenant.setClasse(classe);

        when(apprenantRepository.findById(apprenantId)).thenReturn(Optional.of(apprenant));
        when(apprenantRepository.save(apprenant)).thenReturn(apprenant);

        // When
        apprenantService.removeFromClasse(apprenantId);

        // Then
        assertThat(apprenant.getClasse()).isNull();
        verify(apprenantRepository).save(apprenant);
    }

    @Test
    void findByNiveau_ShouldReturnApprenants() {
        // Given
        String niveau = "Débutant";
        List<Apprenant> apprenants = Collections.singletonList(new Apprenant());
        List<ApprenantDTO> expectedDtos = Collections.singletonList(new ApprenantDTO());

        when(apprenantRepository.findByNiveau(niveau)).thenReturn(apprenants);
        when(apprenantMapper.toDTO(apprenants.get(0))).thenReturn(expectedDtos.get(0));

        // When
        List<ApprenantDTO> result = apprenantService.findByNiveau(niveau);

        // Then
        assertThat(result).isEqualTo(expectedDtos);
    }

    @Test
    void searchByNomOrPrenom_ShouldReturnPageOfApprenants() {
        // Given
        String searchTerm = "Dupont";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Apprenant> apprenantPage = new PageImpl<>(Collections.singletonList(new Apprenant()));
        ApprenantDTO expectedDto = new ApprenantDTO();

        when(apprenantRepository.findByNomContainingOrPrenomContaining(searchTerm, searchTerm, pageable))
                .thenReturn(apprenantPage);
        when(apprenantMapper.toDTO(any(Apprenant.class))).thenReturn(expectedDto);

        // When
        Page<ApprenantDTO> result = apprenantService.searchByNomOrPrenom(searchTerm, pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0)).isEqualTo(expectedDto);
    }

    @Test
    void removeFromFormation_ShouldRemoveApprenantFromFormation() {
        // Given
        Long apprenantId = 1L;
        Long formationId = 1L;
        Apprenant apprenant = new Apprenant();
        Formation formation = new Formation();
        apprenant.setFormations(new HashSet<>(Collections.singleton(formation)));
        formation.setApprenants(new HashSet<>(Collections.singleton(apprenant)));

        when(apprenantRepository.findById(apprenantId)).thenReturn(Optional.of(apprenant));
        when(formationRepository.findById(formationId)).thenReturn(Optional.of(formation));
        when(apprenantRepository.save(any(Apprenant.class))).thenReturn(apprenant);

        // When
        apprenantService.removeFromFormation(apprenantId, formationId);

        // Then
        assertThat(apprenant.getFormations()).isEmpty();
        assertThat(formation.getApprenants()).isEmpty();
        verify(apprenantRepository).save(apprenant);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Given
        String email = "test@example.com";
        when(apprenantRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean result = apprenantService.existsByEmail(email);

        // Then
        assertThat(result).isTrue();
        verify(apprenantRepository).existsByEmail(email);
    }

    private ApprenantDTO createValidApprenantDTO() {
        return ApprenantDTO.builder()
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@email.com")
                .niveau(NiveauFormation.DEBUTANT)
                .build();
    }
}