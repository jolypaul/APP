package com.mycompany.skilltest.domain;

import static com.mycompany.skilltest.domain.CompetenceTestSamples.*;
import static com.mycompany.skilltest.domain.PosteTestSamples.*;
import static com.mycompany.skilltest.domain.TestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CompetenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Competence.class);
        Competence competence1 = getCompetenceSample1();
        Competence competence2 = new Competence();
        assertThat(competence1).isNotEqualTo(competence2);

        competence2.setId(competence1.getId());
        assertThat(competence1).isEqualTo(competence2);

        competence2 = getCompetenceSample2();
        assertThat(competence1).isNotEqualTo(competence2);
    }

    @Test
    void postesTest() {
        Competence competence = getCompetenceRandomSampleGenerator();
        Poste posteBack = getPosteRandomSampleGenerator();

        competence.addPostes(posteBack);
        assertThat(competence.getPosteses()).containsOnly(posteBack);
        assertThat(posteBack.getCompetenceses()).containsOnly(competence);

        competence.removePostes(posteBack);
        assertThat(competence.getPosteses()).doesNotContain(posteBack);
        assertThat(posteBack.getCompetenceses()).doesNotContain(competence);

        competence.posteses(new HashSet<>(Set.of(posteBack)));
        assertThat(competence.getPosteses()).containsOnly(posteBack);
        assertThat(posteBack.getCompetenceses()).containsOnly(competence);

        competence.setPosteses(new HashSet<>());
        assertThat(competence.getPosteses()).doesNotContain(posteBack);
        assertThat(posteBack.getCompetenceses()).doesNotContain(competence);
    }

    @Test
    void testsTest() {
        Competence competence = getCompetenceRandomSampleGenerator();
        Test testBack = getTestRandomSampleGenerator();

        competence.addTests(testBack);
        assertThat(competence.getTestses()).containsOnly(testBack);
        assertThat(testBack.getCompetenceses()).containsOnly(competence);

        competence.removeTests(testBack);
        assertThat(competence.getTestses()).doesNotContain(testBack);
        assertThat(testBack.getCompetenceses()).doesNotContain(competence);

        competence.testses(new HashSet<>(Set.of(testBack)));
        assertThat(competence.getTestses()).containsOnly(testBack);
        assertThat(testBack.getCompetenceses()).containsOnly(competence);

        competence.setTestses(new HashSet<>());
        assertThat(competence.getTestses()).doesNotContain(testBack);
        assertThat(testBack.getCompetenceses()).doesNotContain(competence);
    }
}
