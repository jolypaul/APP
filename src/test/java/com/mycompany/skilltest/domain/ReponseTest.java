package com.mycompany.skilltest.domain;

import static com.mycompany.skilltest.domain.EvaluationTestSamples.*;
import static com.mycompany.skilltest.domain.QuestionTestSamples.*;
import static com.mycompany.skilltest.domain.ReponseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReponseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reponse.class);
        Reponse reponse1 = getReponseSample1();
        Reponse reponse2 = new Reponse();
        assertThat(reponse1).isNotEqualTo(reponse2);

        reponse2.setId(reponse1.getId());
        assertThat(reponse1).isEqualTo(reponse2);

        reponse2 = getReponseSample2();
        assertThat(reponse1).isNotEqualTo(reponse2);
    }

    @Test
    void questionTest() {
        Reponse reponse = getReponseRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        reponse.setQuestion(questionBack);
        assertThat(reponse.getQuestion()).isEqualTo(questionBack);

        reponse.question(null);
        assertThat(reponse.getQuestion()).isNull();
    }

    @Test
    void evaluationTest() {
        Reponse reponse = getReponseRandomSampleGenerator();
        Evaluation evaluationBack = getEvaluationRandomSampleGenerator();

        reponse.setEvaluation(evaluationBack);
        assertThat(reponse.getEvaluation()).isEqualTo(evaluationBack);

        reponse.evaluation(null);
        assertThat(reponse.getEvaluation()).isNull();
    }
}
