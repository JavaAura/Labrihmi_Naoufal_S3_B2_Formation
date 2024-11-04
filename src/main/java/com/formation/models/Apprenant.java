package com.formation.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apprenants")
public class Apprenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private NiveauFormation niveau;

    @ManyToMany(mappedBy = "apprenants", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Formation> formations = new HashSet<>();

    @PreRemove
    private void removeFormationAssociations() {
        for (Formation formation : formations) {
            formation.getApprenants().remove(this);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Classe classe;

    public void addFormation(Formation formation) {
        this.formations.add(formation);
        formation.getApprenants().add(this);
    }

    public void removeFormation(Formation formation) {
        this.formations.remove(formation);
        formation.getApprenants().remove(this);
    }
}
