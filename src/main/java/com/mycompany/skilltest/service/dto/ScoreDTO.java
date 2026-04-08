package com.mycompany.skilltest.service.dto;

import com.mycompany.skilltest.domain.enumeration.Statut;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.skilltest.domain.Score} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScoreDTO implements Serializable {

    private Long id;

    @NotNull
    private Double valeur;

    @NotNull
    private Double pourcentage;

    @NotNull
    private Statut statut;

    @NotNull
    private Instant dateCalcul;

    private EvaluationDTO evaluation;

    private CompetenceDTO competence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValeur() {
        return valeur;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
    }

    public Double getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(Double pourcentage) {
        this.pourcentage = pourcentage;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Instant getDateCalcul() {
        return dateCalcul;
    }

    public void setDateCalcul(Instant dateCalcul) {
        this.dateCalcul = dateCalcul;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public CompetenceDTO getCompetence() {
        return competence;
    }

    public void setCompetence(CompetenceDTO competence) {
        this.competence = competence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScoreDTO)) {
            return false;
        }

        ScoreDTO scoreDTO = (ScoreDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scoreDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScoreDTO{" +
            "id=" + getId() +
            ", valeur=" + getValeur() +
            ", pourcentage=" + getPourcentage() +
            ", statut='" + getStatut() + "'" +
            ", dateCalcul='" + getDateCalcul() + "'" +
            ", evaluation=" + getEvaluation() +
            ", competence=" + getCompetence() +
            "}";
    }
}
