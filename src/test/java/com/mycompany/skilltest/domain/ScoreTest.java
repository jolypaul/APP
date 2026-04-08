package com.mycompany.skilltest.domain;

import static com.mycompany.skilltest.domain.CompetenceTestSamples.*;
import static com.mycompany.skilltest.domain.EvaluationTestSamples.*;
import static com.mycompany.skilltest.domain.ScoreTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScoreTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Score.class);
        Score score1 = getScoreSample1();
        Score score2 = new Score();
        assertThat(score1).isNotEqualTo(score2);

        score2.setId(score1.getId());
        assertThat(score1).isEqualTo(score2);

        score2 = getScoreSample2();
        assertThat(score1).isNotEqualTo(score2);
    }

    @Test
    void evaluationTest() {
        Score score = getScoreRandomSampleGenerator();
        Evaluation evaluationBack = getEvaluationRandomSampleGenerator();

        score.setEvaluation(evaluationBack);
        assertThat(score.getEvaluation()).isEqualTo(evaluationBack);

        score.evaluation(null);
        assertThat(score.getEvaluation()).isNull();
    }

    @Test
    void competenceTest() {
        Score score = getScoreRandomSampleGenerator();
        Competence competenceBack = getCompetenceRandomSampleGenerator();

        score.setCompetence(competenceBack);
        assertThat(score.getCompetence()).isEqualTo(competenceBack);

        score.competence(null);
        assertThat(score.getCompetence()).isNull();
    }
}
