package com.mycompany.skilltest.service.criteria;

import com.mycompany.skilltest.domain.enumeration.TestMode;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.skilltest.domain.Test} entity. This class is used
 * in {@link com.mycompany.skilltest.web.rest.TestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TestCriteria implements Serializable, Criteria {

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

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter titre;

    private TestModeFilter mode;

    private IntegerFilter duree;

    private InstantFilter dateCreation;

    private BooleanFilter actif;

    private LongFilter competencesId;

    private Boolean distinct;

    public TestCriteria() {}

    public TestCriteria(TestCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.titre = other.optionalTitre().map(StringFilter::copy).orElse(null);
        this.mode = other.optionalMode().map(TestModeFilter::copy).orElse(null);
        this.duree = other.optionalDuree().map(IntegerFilter::copy).orElse(null);
        this.dateCreation = other.optionalDateCreation().map(InstantFilter::copy).orElse(null);
        this.actif = other.optionalActif().map(BooleanFilter::copy).orElse(null);
        this.competencesId = other.optionalCompetencesId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TestCriteria copy() {
        return new TestCriteria(this);
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

    public StringFilter getTitre() {
        return titre;
    }

    public Optional<StringFilter> optionalTitre() {
        return Optional.ofNullable(titre);
    }

    public StringFilter titre() {
        if (titre == null) {
            setTitre(new StringFilter());
        }
        return titre;
    }

    public void setTitre(StringFilter titre) {
        this.titre = titre;
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

    public IntegerFilter getDuree() {
        return duree;
    }

    public Optional<IntegerFilter> optionalDuree() {
        return Optional.ofNullable(duree);
    }

    public IntegerFilter duree() {
        if (duree == null) {
            setDuree(new IntegerFilter());
        }
        return duree;
    }

    public void setDuree(IntegerFilter duree) {
        this.duree = duree;
    }

    public InstantFilter getDateCreation() {
        return dateCreation;
    }

    public Optional<InstantFilter> optionalDateCreation() {
        return Optional.ofNullable(dateCreation);
    }

    public InstantFilter dateCreation() {
        if (dateCreation == null) {
            setDateCreation(new InstantFilter());
        }
        return dateCreation;
    }

    public void setDateCreation(InstantFilter dateCreation) {
        this.dateCreation = dateCreation;
    }

    public BooleanFilter getActif() {
        return actif;
    }

    public Optional<BooleanFilter> optionalActif() {
        return Optional.ofNullable(actif);
    }

    public BooleanFilter actif() {
        if (actif == null) {
            setActif(new BooleanFilter());
        }
        return actif;
    }

    public void setActif(BooleanFilter actif) {
        this.actif = actif;
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
        final TestCriteria that = (TestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(titre, that.titre) &&
            Objects.equals(mode, that.mode) &&
            Objects.equals(duree, that.duree) &&
            Objects.equals(dateCreation, that.dateCreation) &&
            Objects.equals(actif, that.actif) &&
            Objects.equals(competencesId, that.competencesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titre, mode, duree, dateCreation, actif, competencesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitre().map(f -> "titre=" + f + ", ").orElse("") +
            optionalMode().map(f -> "mode=" + f + ", ").orElse("") +
            optionalDuree().map(f -> "duree=" + f + ", ").orElse("") +
            optionalDateCreation().map(f -> "dateCreation=" + f + ", ").orElse("") +
            optionalActif().map(f -> "actif=" + f + ", ").orElse("") +
            optionalCompetencesId().map(f -> "competencesId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
