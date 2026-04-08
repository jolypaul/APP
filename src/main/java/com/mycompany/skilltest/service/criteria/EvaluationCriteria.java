package com.mycompany.skilltest.service.criteria;

import com.mycompany.skilltest.domain.enumeration.EvaluationStatus;
import com.mycompany.skilltest.domain.enumeration.Statut;
import com.mycompany.skilltest.domain.enumeration.TestMode;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.skilltest.domain.Evaluation} entity. This class is used
 * in {@link com.mycompany.skilltest.web.rest.EvaluationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /evaluations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EvaluationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EvaluationStatus
     */
    public static class EvaluationStatusFilter extends Filter<EvaluationStatus> {

        public EvaluationStatusFilter() {}

        public EvaluationStatusFilter(EvaluationStatusFilter filter) {
            super(filter);
        }

        @Override
        public EvaluationStatusFilter copy() {
            return new EvaluationStatusFilter(this);
        }
    }

    /**
     * Class for filtering TestMode
     */
    public static class TestModeFilter extends Filter<TestMode> {

        public TestModeFilter() {}

        public TestModeFilter(TestModeFilter filter) {
            super(filter);
        }

        @Override
        public TestModeFilter copy() {
            return new TestModeFilter(this);
        }
    }

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

    private InstantFilter dateEvaluation;

    private EvaluationStatusFilter status;

    private TestModeFilter mode;

    private DoubleFilter scoreTotal;

    private StatutFilter statut;

    private LongFilter employeeId;

    private LongFilter testId;

    private LongFilter managerId;

    private Boolean distinct;

    public EvaluationCriteria() {}

    public EvaluationCriteria(EvaluationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.dateEvaluation = other.optionalDateEvaluation().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(EvaluationStatusFilter::copy).orElse(null);
        this.mode = other.optionalMode().map(TestModeFilter::copy).orElse(null);
        this.scoreTotal = other.optionalScoreTotal().map(DoubleFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutFilter::copy).orElse(null);
        this.employeeId = other.optionalEmployeeId().map(LongFilter::copy).orElse(null);
        this.testId = other.optionalTestId().map(LongFilter::copy).orElse(null);
        this.managerId = other.optionalManagerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EvaluationCriteria copy() {
        return new EvaluationCriteria(this);
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

    public InstantFilter getDateEvaluation() {
        return dateEvaluation;
    }

    public Optional<InstantFilter> optionalDateEvaluation() {
        return Optional.ofNullable(dateEvaluation);
    }

    public InstantFilter dateEvaluation() {
        if (dateEvaluation == null) {
            setDateEvaluation(new InstantFilter());
        }
        return dateEvaluation;
    }

    public void setDateEvaluation(InstantFilter dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public EvaluationStatusFilter getStatus() {
        return status;
    }

    public Optional<EvaluationStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public EvaluationStatusFilter status() {
        if (status == null) {
            setStatus(new EvaluationStatusFilter());
        }
        return status;
    }

    public void setStatus(EvaluationStatusFilter status) {
        this.status = status;
    }

    public TestModeFilter getMode() {
        return mode;
    }

    public Optional<TestModeFilter> optionalMode() {
        return Optional.ofNullable(mode);
    }

    public TestModeFilter mode() {
        if (mode == null) {
            setMode(new TestModeFilter());
        }
        return mode;
    }

    public void setMode(TestModeFilter mode) {
        this.mode = mode;
    }

    public DoubleFilter getScoreTotal() {
        return scoreTotal;
    }

    public Optional<DoubleFilter> optionalScoreTotal() {
        return Optional.ofNullable(scoreTotal);
    }

    public DoubleFilter scoreTotal() {
        if (scoreTotal == null) {
            setScoreTotal(new DoubleFilter());
        }
        return scoreTotal;
    }

    public void setScoreTotal(DoubleFilter scoreTotal) {
        this.scoreTotal = scoreTotal;
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

    public LongFilter getEmployeeId() {
        return employeeId;
    }

    public Optional<LongFilter> optionalEmployeeId() {
        return Optional.ofNullable(employeeId);
    }

    public LongFilter employeeId() {
        if (employeeId == null) {
            setEmployeeId(new LongFilter());
        }
        return employeeId;
    }

    public void setEmployeeId(LongFilter employeeId) {
        this.employeeId = employeeId;
    }

    public LongFilter getTestId() {
        return testId;
    }

    public Optional<LongFilter> optionalTestId() {
        return Optional.ofNullable(testId);
    }

    public LongFilter testId() {
        if (testId == null) {
            setTestId(new LongFilter());
        }
        return testId;
    }

    public void setTestId(LongFilter testId) {
        this.testId = testId;
    }

    public LongFilter getManagerId() {
        return managerId;
    }

    public Optional<LongFilter> optionalManagerId() {
        return Optional.ofNullable(managerId);
    }

    public LongFilter managerId() {
        if (managerId == null) {
            setManagerId(new LongFilter());
        }
        return managerId;
    }

    public void setManagerId(LongFilter managerId) {
        this.managerId = managerId;
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
        final EvaluationCriteria that = (EvaluationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(dateEvaluation, that.dateEvaluation) &&
            Objects.equals(status, that.status) &&
            Objects.equals(mode, that.mode) &&
            Objects.equals(scoreTotal, that.scoreTotal) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(employeeId, that.employeeId) &&
            Objects.equals(testId, that.testId) &&
            Objects.equals(managerId, that.managerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateEvaluation, status, mode, scoreTotal, statut, employeeId, testId, managerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EvaluationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDateEvaluation().map(f -> "dateEvaluation=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalMode().map(f -> "mode=" + f + ", ").orElse("") +
            optionalScoreTotal().map(f -> "scoreTotal=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalEmployeeId().map(f -> "employeeId=" + f + ", ").orElse("") +
            optionalTestId().map(f -> "testId=" + f + ", ").orElse("") +
            optionalManagerId().map(f -> "managerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
