package com.mycompany.skilltest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.skilltest.domain.enumeration.Statut;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Score.
 */
@Entity
@Table(name = "score")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Score implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "valeur", nullable = false)
    private Double valeur;

    @NotNull
    @Column(name = "pourcentage", nullable = false)
    private Double pourcentage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private Statut statut;

    @NotNull
    @Column(name = "date_calcul", nullable = false)
    private Instant dateCalcul;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employee", "test", "manager" }, allowSetters = true)
    private Evaluation evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "posteses", "testses" }, allowSetters = true)
    private Competence competence;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Score id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValeur() {
        return this.valeur;
    }

    public Score valeur(Double valeur) {
        this.setValeur(valeur);
        return this;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
    }

    public Double getPourcentage() {
        return this.pourcentage;
    }

    public Score pourcentage(Double pourcentage) {
        this.setPourcentage(pourcentage);
        return this;
    }

    public void setPourcentage(Double pourcentage) {
        this.pourcentage = pourcentage;
    }

    public Statut getStatut() {
        return this.statut;
    }

    public Score statut(Statut statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Instant getDateCalcul() {
        return this.dateCalcul;
    }

    public Score dateCalcul(Instant dateCalcul) {
        this.setDateCalcul(dateCalcul);
        return this;
    }

    public void setDateCalcul(Instant dateCalcul) {
        this.dateCalcul = dateCalcul;
    }

    public Evaluation getEvaluation() {
        return this.evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public Score evaluation(Evaluation evaluation) {
        this.setEvaluation(evaluation);
        return this;
    }

    public Competence getCompetence() {
        return this.competence;
    }

    public void setCompetence(Competence competence) {
        this.competence = competence;
    }

    public Score competence(Competence competence) {
        this.setCompetence(competence);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Score)) {
            return false;
        }
        return getId() != null && getId().equals(((Score) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Score{" +
            "id=" + getId() +
            ", valeur=" + getValeur() +
            ", pourcentage=" + getPourcentage() +
            ", statut='" + getStatut() + "'" +
            ", dateCalcul='" + getDateCalcul() + "'" +
            "}";
    }
}
