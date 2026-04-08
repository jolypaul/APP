package com.mycompany.skilltest.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EvaluationCriteriaTest {

    @Test
    void newEvaluationCriteriaHasAllFiltersNullTest() {
        var evaluationCriteria = new EvaluationCriteria();
        assertThat(evaluationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void evaluationCriteriaFluentMethodsCreatesFiltersTest() {
        var evaluationCriteria = new EvaluationCriteria();

        setAllFilters(evaluationCriteria);

        assertThat(evaluationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void evaluationCriteriaCopyCreatesNullFilterTest() {
        var evaluationCriteria = new EvaluationCriteria();
        var copy = evaluationCriteria.copy();

        assertThat(evaluationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(evaluationCriteria)
        );
    }

    @Test
    void evaluationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var evaluationCriteria = new EvaluationCriteria();
        setAllFilters(evaluationCriteria);

        var copy = evaluationCriteria.copy();

        assertThat(evaluationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(evaluationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var evaluationCriteria = new EvaluationCriteria();

        assertThat(evaluationCriteria).hasToString("EvaluationCriteria{}");
    }

    private static void setAllFilters(EvaluationCriteria evaluationCriteria) {
        evaluationCriteria.id();
        evaluationCriteria.dateEvaluation();
        evaluationCriteria.status();
        evaluationCriteria.mode();
        evaluationCriteria.scoreTotal();
        evaluationCriteria.statut();
        evaluationCriteria.employeeId();
        evaluationCriteria.testId();
        evaluationCriteria.managerId();
        evaluationCriteria.distinct();
    }

    private static Condition<EvaluationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDateEvaluation()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getMode()) &&
                condition.apply(criteria.getScoreTotal()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getEmployeeId()) &&
                condition.apply(criteria.getTestId()) &&
                condition.apply(criteria.getManagerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EvaluationCriteria> copyFiltersAre(EvaluationCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDateEvaluation(), copy.getDateEvaluation()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getMode(), copy.getMode()) &&
                condition.apply(criteria.getScoreTotal(), copy.getScoreTotal()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getEmployeeId(), copy.getEmployeeId()) &&
                condition.apply(criteria.getTestId(), copy.getTestId()) &&
                condition.apply(criteria.getManagerId(), copy.getManagerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
