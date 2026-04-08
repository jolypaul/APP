package com.mycompany.skilltest.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ScoreCriteriaTest {

    @Test
    void newScoreCriteriaHasAllFiltersNullTest() {
        var scoreCriteria = new ScoreCriteria();
        assertThat(scoreCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void scoreCriteriaFluentMethodsCreatesFiltersTest() {
        var scoreCriteria = new ScoreCriteria();

        setAllFilters(scoreCriteria);

        assertThat(scoreCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void scoreCriteriaCopyCreatesNullFilterTest() {
        var scoreCriteria = new ScoreCriteria();
        var copy = scoreCriteria.copy();

        assertThat(scoreCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(scoreCriteria)
        );
    }

    @Test
    void scoreCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var scoreCriteria = new ScoreCriteria();
        setAllFilters(scoreCriteria);

        var copy = scoreCriteria.copy();

        assertThat(scoreCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(scoreCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var scoreCriteria = new ScoreCriteria();

        assertThat(scoreCriteria).hasToString("ScoreCriteria{}");
    }

    private static void setAllFilters(ScoreCriteria scoreCriteria) {
        scoreCriteria.id();
        scoreCriteria.valeur();
        scoreCriteria.pourcentage();
        scoreCriteria.statut();
        scoreCriteria.dateCalcul();
        scoreCriteria.evaluationId();
        scoreCriteria.competenceId();
        scoreCriteria.distinct();
    }

    private static Condition<ScoreCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getValeur()) &&
                condition.apply(criteria.getPourcentage()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getDateCalcul()) &&
                condition.apply(criteria.getEvaluationId()) &&
                condition.apply(criteria.getCompetenceId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ScoreCriteria> copyFiltersAre(ScoreCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getValeur(), copy.getValeur()) &&
                condition.apply(criteria.getPourcentage(), copy.getPourcentage()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getDateCalcul(), copy.getDateCalcul()) &&
                condition.apply(criteria.getEvaluationId(), copy.getEvaluationId()) &&
                condition.apply(criteria.getCompetenceId(), copy.getCompetenceId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
