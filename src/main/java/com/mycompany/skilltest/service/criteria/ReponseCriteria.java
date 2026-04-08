package com.mycompany.skilltest.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.skilltest.domain.Reponse} entity. This class is used
 * in {@link com.mycompany.skilltest.web.rest.ReponseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reponses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReponseCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter estCorrecte;

    private InstantFilter dateReponse;

    private LongFilter questionId;

    private LongFilter evaluationId;

    private Boolean distinct;

    public ReponseCriteria() {}

    public ReponseCriteria(ReponseCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.estCorrecte = other.optionalEstCorrecte().map(BooleanFilter::copy).orElse(null);
        this.dateReponse = other.optionalDateReponse().map(InstantFilter::copy).orElse(null);
        this.questionId = other.optionalQuestionId().map(LongFilter::copy).orElse(null);
        this.evaluationId = other.optionalEvaluationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReponseCriteria copy() {
        return new ReponseCriteria(this);
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

    public BooleanFilter getEstCorrecte() {
        return estCorrecte;
    }

    public Optional<BooleanFilter> optionalEstCorrecte() {
        return Optional.ofNullable(estCorrecte);
    }

    public BooleanFilter estCorrecte() {
        if (estCorrecte == null) {
            setEstCorrecte(new BooleanFilter());
        }
        return estCorrecte;
    }

    public void setEstCorrecte(BooleanFilter estCorrecte) {
        this.estCorrecte = estCorrecte;
    }

    public InstantFilter getDateReponse() {
        return dateReponse;
    }

    public Optional<InstantFilter> optionalDateReponse() {
        return Optional.ofNullable(dateReponse);
    }

    public InstantFilter dateReponse() {
        if (dateReponse == null) {
            setDateReponse(new InstantFilter());
        }
        return dateReponse;
    }

    public void setDateReponse(InstantFilter dateReponse) {
        this.dateReponse = dateReponse;
    }

    public LongFilter getQuestionId() {
        return questionId;
    }

    public Optional<LongFilter> optionalQuestionId() {
        return Optional.ofNullable(questionId);
    }

    public LongFilter questionId() {
        if (questionId == null) {
            setQuestionId(new LongFilter());
        }
        return questionId;
    }

    public void setQuestionId(LongFilter questionId) {
        this.questionId = questionId;
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
        final ReponseCriteria that = (ReponseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(estCorrecte, that.estCorrecte) &&
            Objects.equals(dateReponse, that.dateReponse) &&
            Objects.equals(questionId, that.questionId) &&
            Objects.equals(evaluationId, that.evaluationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, estCorrecte, dateReponse, questionId, evaluationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReponseCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEstCorrecte().map(f -> "estCorrecte=" + f + ", ").orElse("") +
            optionalDateReponse().map(f -> "dateReponse=" + f + ", ").orElse("") +
            optionalQuestionId().map(f -> "questionId=" + f + ", ").orElse("") +
            optionalEvaluationId().map(f -> "evaluationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
