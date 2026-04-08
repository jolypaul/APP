package com.mycompany.skilltest.service.dto;

import com.mycompany.skilltest.domain.enumeration.Level;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.skilltest.domain.Competence} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompetenceDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @Lob
    private String description;

    private String categorie;

    @NotNull
    private Level niveauAttendu;

    private Set<PosteDTO> posteses = new HashSet<>();

    private Set<TestDTO> testses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Level getNiveauAttendu() {
        return niveauAttendu;
    }

    public void setNiveauAttendu(Level niveauAttendu) {
        this.niveauAttendu = niveauAttendu;
    }

    public Set<PosteDTO> getPosteses() {
        return posteses;
    }

    public void setPosteses(Set<PosteDTO> posteses) {
        this.posteses = posteses;
    }

    public Set<TestDTO> getTestses() {
        return testses;
    }

    public void setTestses(Set<TestDTO> testses) {
        this.testses = testses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompetenceDTO)) {
            return false;
        }

        CompetenceDTO competenceDTO = (CompetenceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, competenceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompetenceDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", categorie='" + getCategorie() + "'" +
            ", niveauAttendu='" + getNiveauAttendu() + "'" +
            ", posteses=" + getPosteses() +
            ", testses=" + getTestses() +
            "}";
    }
}
