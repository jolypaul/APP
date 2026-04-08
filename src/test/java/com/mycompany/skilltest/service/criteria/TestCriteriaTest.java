package com.mycompany.skilltest.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TestCriteriaTest {

    @Test
    void newTestCriteriaHasAllFiltersNullTest() {
        var testCriteria = new TestCriteria();
        assertThat(testCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void testCriteriaFluentMethodsCreatesFiltersTest() {
        var testCriteria = new TestCriteria();

        setAllFilters(testCriteria);

        assertThat(testCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void testCriteriaCopyCreatesNullFilterTest() {
        var testCriteria = new TestCriteria();
        var copy = testCriteria.copy();

        assertThat(testCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(testCriteria)
        );
    }

    @Test
    void testCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var testCriteria = new TestCriteria();
        setAllFilters(testCriteria);

        var copy = testCriteria.copy();

        assertThat(testCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(testCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var testCriteria = new TestCriteria();

        assertThat(testCriteria).hasToString("TestCriteria{}");
    }

    private static void setAllFilters(TestCriteria testCriteria) {
        testCriteria.id();
        testCriteria.titre();
        testCriteria.mode();
        testCriteria.duree();
        testCriteria.dateCreation();
        testCriteria.actif();
        testCriteria.competencesId();
        testCriteria.distinct();
    }

    private static Condition<TestCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitre()) &&
                condition.apply(criteria.getMode()) &&
                condition.apply(criteria.getDuree()) &&
                condition.apply(criteria.getDateCreation()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getCompetencesId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TestCriteria> copyFiltersAre(TestCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitre(), copy.getTitre()) &&
                condition.apply(criteria.getMode(), copy.getMode()) &&
                condition.apply(criteria.getDuree(), copy.getDuree()) &&
                condition.apply(criteria.getDateCreation(), copy.getDateCreation()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getCompetencesId(), copy.getCompetencesId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
