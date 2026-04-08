package com.mycompany.skilltest.service.dto;

import com.mycompany.skilltest.domain.enumeration.TestMode;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.skilltest.domain.Test} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TestDTO implements Serializable {

    private Long id;

    @NotNull
    private String titre;

    @Lob
    private String description;

    @NotNull
    private TestMode mode;

    private Integer duree;

    @NotNull
    private Instant dateCreation;

    @NotNull
    private Boolean actif;

    private Set<CompetenceDTO> competenceses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TestMode getMode() {
        return mode;
    }

    public void setMode(TestMode mode) {
        this.mode = mode;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Set<CompetenceDTO> getCompetenceses() {
        return competenceses;
    }

    public void setCompetenceses(Set<CompetenceDTO> competenceses) {
        this.competenceses = competenceses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestDTO)) {
            return false;
        }

        TestDTO testDTO = (TestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, testDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", mode='" + getMode() + "'" +
            ", duree=" + getDuree() +
            ", dateCreation='" + getDateCreation() + "'" +
            ", actif='" + getActif() + "'" +
            ", competenceses=" + getCompetenceses() +
            "}";
    }
}
