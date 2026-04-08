package com.mycompany.skilltest.service.criteria;

import com.mycompany.skilltest.domain.enumeration.Level;
import com.mycompany.skilltest.domain.enumeration.QuestionType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.skilltest.domain.Question} entity. This class is used
 * in {@link com.mycompany.skilltest.web.rest.QuestionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /questions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering QuestionType
     */
    public static class QuestionTypeFilter extends Filter<QuestionType> {

        public QuestionTypeFilter() {}

        public QuestionTypeFilter(QuestionTypeFilter filter) {
            super(filter);
        }

        @Override
        public QuestionTypeFilter copy() {
            return new QuestionTypeFilter(this);
        }
    }

    /**
     * Class for filtering Level
     */
    public static class LevelFilter extends Filter<Level> {

        public LevelFilter() {}

        public LevelFilter(LevelFilter filter) {
            super(filter);
        }

        @Override
        public LevelFilter copy() {
            return new LevelFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private QuestionTypeFilter type;

    private LevelFilter niveau;

    private IntegerFilter points;

    private LongFilter testId;

    private Boolean distinct;

    public QuestionCriteria() {}

    public QuestionCriteria(QuestionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.type = other.optionalType().map(QuestionTypeFilter::copy).orElse(null);
        this.niveau = other.optionalNiveau().map(LevelFilter::copy).orElse(null);
        this.points = other.optionalPoints().map(IntegerFilter::copy).orElse(null);
        this.testId = other.optionalTestId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public QuestionCriteria copy() {
        return new QuestionCriteria(this);
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

    public QuestionTypeFilter getType() {
        return type;
    }

    public Optional<QuestionTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public QuestionTypeFilter type() {
        if (type == null) {
            setType(new QuestionTypeFilter());
        }
        return type;
    }

    public void setType(QuestionTypeFilter type) {
        this.type = type;
    }

    public LevelFilter getNiveau() {
        return niveau;
    }

    public Optional<LevelFilter> optionalNiveau() {
        return Optional.ofNullable(niveau);
    }

    public LevelFilter niveau() {
        if (niveau == null) {
            setNiveau(new LevelFilter());
        }
        return niveau;
    }

    public void setNiveau(LevelFilter niveau) {
        this.niveau = niveau;
    }

    public IntegerFilter getPoints() {
        return points;
    }

    public Optional<IntegerFilter> optionalPoints() {
        return Optional.ofNullable(points);
    }

    public IntegerFilter points() {
        if (points == null) {
            setPoints(new IntegerFilter());
        }
        return points;
    }

    public void setPoints(IntegerFilter points) {
        this.points = points;
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
        final QuestionCriteria that = (QuestionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(niveau, that.niveau) &&
            Objects.equals(points, that.points) &&
            Objects.equals(testId, that.testId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, niveau, points, testId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalNiveau().map(f -> "niveau=" + f + ", ").orElse("") +
            optionalPoints().map(f -> "points=" + f + ", ").orElse("") +
            optionalTestId().map(f -> "testId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
