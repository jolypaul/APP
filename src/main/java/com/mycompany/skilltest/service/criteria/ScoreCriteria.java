package com.mycompany.skilltest.service.criteria;

import com.mycompany.skilltest.domain.enumeration.Statut;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.skilltest.domain.Score} entity. This class is used
 * in {@link com.mycompany.skilltest.web.rest.ScoreResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /scores?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScoreCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Statut
     */
    public static class StatutFilter extends Filter<Statut> {

        public StatutFilter() {}

        public StatutFilter(StatutFilter filter) {
            super(filter);
        }

        @Override
        public StatutFilter copy() {
            return new StatutFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter valeur;

    private DoubleFilter pourcentage;

    private StatutFilter statut;

    private InstantFilter dateCalcul;

    private LongFilter evaluationId;

    private LongFilter competenceId;

    private Boolean distinct;

    public ScoreCriteria() {}

    public ScoreCriteria(ScoreCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.valeur = other.optionalValeur().map(DoubleFilter::copy).orElse(null);
        this.pourcentage = other.optionalPourcentage().map(DoubleFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutFilter::copy).orElse(null);
        this.dateCalcul = other.optionalDateCalcul().map(InstantFilter::copy).orElse(null);
        this.evaluationId = other.optionalEvaluationId().map(LongFilter::copy).orElse(null);
        this.competenceId = other.optionalCompetenceId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ScoreCriteria copy() {
        return new ScoreCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public DoubleFilter getValeur() {
        return valeur;
    }

    public Optional<DoubleFilter> optionalValeur() {
        return Optional.ofNullable(valeur);
    }

    public DoubleFilter valeur() {
        if (valeur == null) {
            setValeur(new DoubleFilter());
        }
        return valeur;
    }

    public void setValeur(DoubleFilter valeur) {
        this.valeur = valeur;
    }

    public DoubleFilter getPourcentage() {
        return pourcentage;
    }

    public Optional<DoubleFilter> optionalPourcentage() {
        return Optional.ofNullable(pourcentage);
    }

    public DoubleFilter pourcentage() {
        if (pourcentage == null) {
            setPourcentage(new DoubleFilter());
        }
        return pourcentage;
    }

    public void setPourcentage(DoubleFilter pourcentage) {
        this.pourcentage = pourcentage;
    }

    public StatutFilter getStatut() {
        return statut;
    }

    public Optional<StatutFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutFilter statut() {
        if (statut == null) {
            setStatut(new StatutFilter());
        }
        return statut;
    }

    public void setStatut(StatutFilter statut) {
        this.statut = statut;
    }

    public InstantFilter getDateCalcul() {
        return dateCalcul;
    }

    public Optional<InstantFilter> optionalDateCalcul() {
        return Optional.ofNullable(dateCalcul);
    }

    public InstantFilter dateCalcul() {
        if (dateCalcul == null) {
            setDateCalcul(new InstantFilter());
        }
        return dateCalcul;
    }

    public void setDateCalcul(InstantFilter dateCalcul) {
        this.dateCalcul = dateCalcul;
    }

    public LongFilter getEvaluationId() {
        return evaluationId;
    }

    public Optional<LongFilter> optionalEvaluationId() {
        return Optional.ofNullable(evaluationId);
    }

    public LongFilter evaluationId() {
        if (evaluationId == null) {
            setEvaluationId(new LongFilter());
        }
        return evaluationId;
    }

    public void setEvaluationId(LongFilter evaluationId) {
        this.evaluationId = evaluationId;
    }

    public LongFilter getCompetenceId() {
        return competenceId;
    }

    public Optional<LongFilter> optionalCompetenceId() {
        return Optional.ofNullable(competenceId);
    }

    public LongFilter competenceId() {
        if (competenceId == null) {
            setCompetenceId(new LongFilter());
        }
        return competenceId;
    }

    public void setCompetenceId(LongFilter competenceId) {
        this.competenceId = competenceId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ScoreCriteria that = (ScoreCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(valeur, that.valeur) &&
            Objects.equals(pourcentage, that.pourcentage) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(dateCalcul, that.dateCalcul) &&
            Objects.equals(evaluationId, that.evaluationId) &&
            Objects.equals(competenceId, that.competenceId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, valeur, pourcentage, statut, dateCalcul, evaluationId, competenceId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScoreCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalValeur().map(f -> "valeur=" + f + ", ").orElse("") +
            optionalPourcentage().map(f -> "pourcentage=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDateCalcul().map(f -> "dateCalcul=" + f + ", ").orElse("") +
            optionalEvaluationId().map(f -> "evaluationId=" + f + ", ").orElse("") +
            optionalCompetenceId().map(f -> "competenceId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
