package com.mycompany.skilltest.domain;

import static com.mycompany.skilltest.domain.QuestionTestSamples.*;
import static com.mycompany.skilltest.domain.TestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question.class);
        Question question1 = getQuestionSample1();
        Question question2 = new Question();
        assertThat(question1).isNotEqualTo(question2);

        question2.setId(question1.getId());
        assertThat(question1).isEqualTo(question2);

        question2 = getQuestionSample2();
        assertThat(question1).isNotEqualTo(question2);
    }

    @Test
    void testTest() {
        Question question = getQuestionRandomSampleGenerator();
        Test testBack = getTestRandomSampleGenerator();

        question.setTest(testBack);
        assertThat(question.getTest()).isEqualTo(testBack);

        question.test(null);
        assertThat(question.getTest()).isNull();
    }
}
