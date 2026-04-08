package com.mycompany.skilltest.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReponseCriteriaTest {

    @Test
    void newReponseCriteriaHasAllFiltersNullTest() {
        var reponseCriteria = new ReponseCriteria();
        assertThat(reponseCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void reponseCriteriaFluentMethodsCreatesFiltersTest() {
        var reponseCriteria = new ReponseCriteria();

        setAllFilters(reponseCriteria);

        assertThat(reponseCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void reponseCriteriaCopyCreatesNullFilterTest() {
        var reponseCriteria = new ReponseCriteria();
        var copy = reponseCriteria.copy();

        assertThat(reponseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(reponseCriteria)
        );
    }

    @Test
    void reponseCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var reponseCriteria = new ReponseCriteria();
        setAllFilters(reponseCriteria);

        var copy = reponseCriteria.copy();

        assertThat(reponseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(reponseCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var reponseCriteria = new ReponseCriteria();

        assertThat(reponseCriteria).hasToString("ReponseCriteria{}");
    }

    private static void setAllFilters(ReponseCriteria reponseCriteria) {
        reponseCriteria.id();
        reponseCriteria.estCorrecte();
        reponseCriteria.dateReponse();
        reponseCriteria.questionId();
        reponseCriteria.evaluationId();
        reponseCriteria.distinct();
    }

    private static Condition<ReponseCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEstCorrecte()) &&
                condition.apply(criteria.getDateReponse()) &&
                condition.apply(criteria.getQuestionId()) &&
                condition.apply(criteria.getEvaluationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReponseCriteria> copyFiltersAre(ReponseCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEstCorrecte(), copy.getEstCorrecte()) &&
                condition.apply(criteria.getDateReponse(), copy.getDateReponse()) &&
                condition.apply(criteria.getQuestionId(), copy.getQuestionId()) &&
                condition.apply(criteria.getEvaluationId(), copy.getEvaluationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
