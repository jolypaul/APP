package com.mycompany.skilltest.service.dto;

import com.mycompany.skilltest.domain.enumeration.Level;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.skilltest.domain.Poste} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PosteDTO implements Serializable {

    private Long id;

    @NotNull
    private String intitule;

    @Lob
    private String description;

    @NotNull
    private Level niveauRequis;

    private Set<CompetenceDTO> competenceses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Level getNiveauRequis() {
        return niveauRequis;
    }

    public void setNiveauRequis(Level niveauRequis) {
        this.niveauRequis = niveauRequis;
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
        if (!(o instanceof PosteDTO)) {
            return false;
        }

        PosteDTO posteDTO = (PosteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, posteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PosteDTO{" +
            "id=" + getId() +
            ", intitule='" + getIntitule() + "'" +
            ", description='" + getDescription() + "'" +
            ", niveauRequis='" + getNiveauRequis() + "'" +
            ", competenceses=" + getCompetenceses() +
            "}";
    }
}
