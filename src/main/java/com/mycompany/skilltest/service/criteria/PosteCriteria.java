package com.mycompany.skilltest.service.criteria;

import com.mycompany.skilltest.domain.enumeration.Level;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.skilltest.domain.Poste} entity. This class is used
 * in {@link com.mycompany.skilltest.web.rest.PosteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /postes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PosteCriteria implements Serializable, Criteria {

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

    private StringFilter intitule;

    private LevelFilter niveauRequis;

    private LongFilter competencesId;

    private Boolean distinct;

    public PosteCriteria() {}

    public PosteCriteria(PosteCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.intitule = other.optionalIntitule().map(StringFilter::copy).orElse(null);
        this.niveauRequis = other.optionalNiveauRequis().map(LevelFilter::copy).orElse(null);
        this.competencesId = other.optionalCompetencesId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PosteCriteria copy() {
        return new PosteCriteria(this);
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

    public StringFilter getIntitule() {
        return intitule;
    }

    public Optional<StringFilter> optionalIntitule() {
        return Optional.ofNullable(intitule);
    }

    public StringFilter intitule() {
        if (intitule == null) {
            setIntitule(new StringFilter());
        }
        return intitule;
    }

    public void setIntitule(StringFilter intitule) {
        this.intitule = intitule;
    }

    public LevelFilter getNiveauRequis() {
        return niveauRequis;
    }

    public Optional<LevelFilter> optionalNiveauRequis() {
        return Optional.ofNullable(niveauRequis);
    }

    public LevelFilter niveauRequis() {
        if (niveauRequis == null) {
            setNiveauRequis(new LevelFilter());
        }
        return niveauRequis;
    }

    public void setNiveauRequis(LevelFilter niveauRequis) {
        this.niveauRequis = niveauRequis;
    }

    public LongFilter getCompetencesId() {
        return competencesId;
    }

    public Optional<LongFilter> optionalCompetencesId() {
        return Optional.ofNullable(competencesId);
    }

    public LongFilter competencesId() {
        if (competencesId == null) {
            setCompetencesId(new LongFilter());
        }
        return competencesId;
    }

    public void setCompetencesId(LongFilter competencesId) {
        this.competencesId = competencesId;
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
        final PosteCriteria that = (PosteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(intitule, that.intitule) &&
            Objects.equals(niveauRequis, that.niveauRequis) &&
            Objects.equals(competencesId, that.competencesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, intitule, niveauRequis, competencesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PosteCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIntitule().map(f -> "intitule=" + f + ", ").orElse("") +
            optionalNiveauRequis().map(f -> "niveauRequis=" + f + ", ").orElse("") +
            optionalCompetencesId().map(f -> "competencesId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
