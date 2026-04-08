package com.mycompany.skilltest.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CompetenceCriteriaTest {

    @Test
    void newCompetenceCriteriaHasAllFiltersNullTest() {
        var competenceCriteria = new CompetenceCriteria();
        assertThat(competenceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void competenceCriteriaFluentMethodsCreatesFiltersTest() {
        var competenceCriteria = new CompetenceCriteria();

        setAllFilters(competenceCriteria);

        assertThat(competenceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void competenceCriteriaCopyCreatesNullFilterTest() {
        var competenceCriteria = new CompetenceCriteria();
        var copy = competenceCriteria.copy();

        assertThat(competenceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(competenceCriteria)
        );
    }

    @Test
    void competenceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var competenceCriteria = new CompetenceCriteria();
        setAllFilters(competenceCriteria);

        var copy = competenceCriteria.copy();

        assertThat(competenceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(competenceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var competenceCriteria = new CompetenceCriteria();

        assertThat(competenceCriteria).hasToString("CompetenceCriteria{}");
    }

    private static void setAllFilters(CompetenceCriteria competenceCriteria) {
        competenceCriteria.id();
        competenceCriteria.nom();
        competenceCriteria.categorie();
        competenceCriteria.niveauAttendu();
        competenceCriteria.postesId();
        competenceCriteria.testsId();
        competenceCriteria.distinct();
    }

    private static Condition<CompetenceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getCategorie()) &&
                condition.apply(criteria.getNiveauAttendu()) &&
                condition.apply(criteria.getPostesId()) &&
                condition.apply(criteria.getTestsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CompetenceCriteria> copyFiltersAre(CompetenceCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getCategorie(), copy.getCategorie()) &&
                condition.apply(criteria.getNiveauAttendu(), copy.getNiveauAttendu()) &&
                condition.apply(criteria.getPostesId(), copy.getPostesId()) &&
                condition.apply(criteria.getTestsId(), copy.getTestsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
