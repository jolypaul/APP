package com.mycompany.skilltest.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PosteCriteriaTest {

    @Test
    void newPosteCriteriaHasAllFiltersNullTest() {
        var posteCriteria = new PosteCriteria();
        assertThat(posteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void posteCriteriaFluentMethodsCreatesFiltersTest() {
        var posteCriteria = new PosteCriteria();

        setAllFilters(posteCriteria);

        assertThat(posteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void posteCriteriaCopyCreatesNullFilterTest() {
        var posteCriteria = new PosteCriteria();
        var copy = posteCriteria.copy();

        assertThat(posteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(posteCriteria)
        );
    }

    @Test
    void posteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var posteCriteria = new PosteCriteria();
        setAllFilters(posteCriteria);

        var copy = posteCriteria.copy();

        assertThat(posteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(posteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var posteCriteria = new PosteCriteria();

        assertThat(posteCriteria).hasToString("PosteCriteria{}");
    }

    private static void setAllFilters(PosteCriteria posteCriteria) {
        posteCriteria.id();
        posteCriteria.intitule();
        posteCriteria.niveauRequis();
        posteCriteria.competencesId();
        posteCriteria.distinct();
    }

    private static Condition<PosteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getIntitule()) &&
                condition.apply(criteria.getNiveauRequis()) &&
                condition.apply(criteria.getCompetencesId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PosteCriteria> copyFiltersAre(PosteCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getIntitule(), copy.getIntitule()) &&
                condition.apply(criteria.getNiveauRequis(), copy.getNiveauRequis()) &&
                condition.apply(criteria.getCompetencesId(), copy.getCompetencesId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
