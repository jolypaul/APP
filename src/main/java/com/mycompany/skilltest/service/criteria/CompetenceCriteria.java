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
 * Criteria class for the {@link com.mycompany.skilltest.domain.Competence} entity. This class is used
 * in {@link com.mycompany.skilltest.web.rest.CompetenceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /competences?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompetenceCriteria implements Serializable, Criteria {

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

    private StringFilter nom;

    private StringFilter categorie;

    private LevelFilter niveauAttendu;

    private LongFilter postesId;

    private LongFilter testsId;

    private Boolean distinct;

    public CompetenceCriteria() {}

    public CompetenceCriteria(CompetenceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nom = other.optionalNom().map(StringFilter::copy).orElse(null);
        this.categorie = other.optionalCategorie().map(StringFilter::copy).orElse(null);
        this.niveauAttendu = other.optionalNiveauAttendu().map(LevelFilter::copy).orElse(null);
        this.postesId = other.optionalPostesId().map(LongFilter::copy).orElse(null);
        this.testsId = other.optionalTestsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CompetenceCriteria copy() {
        return new CompetenceCriteria(this);
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

    public StringFilter getNom() {
        return nom;
    }

    public Optional<StringFilter> optionalNom() {
        return Optional.ofNullable(nom);
    }

    public StringFilter nom() {
        if (nom == null) {
            setNom(new StringFilter());
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public StringFilter getCategorie() {
        return categorie;
    }

    public Optional<StringFilter> optionalCategorie() {
        return Optional.ofNullable(categorie);
    }

    public StringFilter categorie() {
        if (categorie == null) {
            setCategorie(new StringFilter());
        }
        return categorie;
    }

    public void setCategorie(StringFilter categorie) {
        this.categorie = categorie;
    }

    public LevelFilter getNiveauAttendu() {
        return niveauAttendu;
    }

    public Optional<LevelFilter> optionalNiveauAttendu() {
        return Optional.ofNullable(niveauAttendu);
    }

    public LevelFilter niveauAttendu() {
        if (niveauAttendu == null) {
            setNiveauAttendu(new LevelFilter());
        }
        return niveauAttendu;
    }

    public void setNiveauAttendu(LevelFilter niveauAttendu) {
        this.niveauAttendu = niveauAttendu;
    }

    public LongFilter getPostesId() {
        return postesId;
    }

    public Optional<LongFilter> optionalPostesId() {
        return Optional.ofNullable(postesId);
    }

    public LongFilter postesId() {
        if (postesId == null) {
            setPostesId(new LongFilter());
        }
        return postesId;
    }

    public void setPostesId(LongFilter postesId) {
        this.postesId = postesId;
    }

    public LongFilter getTestsId() {
        return testsId;
    }

    public Optional<LongFilter> optionalTestsId() {
        return Optional.ofNullable(testsId);
    }

    public LongFilter testsId() {
        if (testsId == null) {
            setTestsId(new LongFilter());
        }
        return testsId;
    }

    public void setTestsId(LongFilter testsId) {
        this.testsId = testsId;
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
        final CompetenceCriteria that = (CompetenceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(categorie, that.categorie) &&
            Objects.equals(niveauAttendu, that.niveauAttendu) &&
            Objects.equals(postesId, that.postesId) &&
            Objects.equals(testsId, that.testsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, categorie, niveauAttendu, postesId, testsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompetenceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNom().map(f -> "nom=" + f + ", ").orElse("") +
            optionalCategorie().map(f -> "categorie=" + f + ", ").orElse("") +
            optionalNiveauAttendu().map(f -> "niveauAttendu=" + f + ", ").orElse("") +
            optionalPostesId().map(f -> "postesId=" + f + ", ").orElse("") +
            optionalTestsId().map(f -> "testsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
