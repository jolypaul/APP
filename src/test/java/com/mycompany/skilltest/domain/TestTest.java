package com.mycompany.skilltest.domain;

import static com.mycompany.skilltest.domain.CompetenceTestSamples.*;
import static com.mycompany.skilltest.domain.TestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Test.class);
        Test test1 = getTestSample1();
        Test test2 = new Test();
        assertThat(test1).isNotEqualTo(test2);

        test2.setId(test1.getId());
        assertThat(test1).isEqualTo(test2);

        test2 = getTestSample2();
        assertThat(test1).isNotEqualTo(test2);
    }

    @Test
    void competencesTest() {
        Test test = getTestRandomSampleGenerator();
        Competence competenceBack = getCompetenceRandomSampleGenerator();

        test.addCompetences(competenceBack);
        assertThat(test.getCompetenceses()).containsOnly(competenceBack);

        test.removeCompetences(competenceBack);
        assertThat(test.getCompetenceses()).doesNotContain(competenceBack);

        test.competenceses(new HashSet<>(Set.of(competenceBack)));
        assertThat(test.getCompetenceses()).containsOnly(competenceBack);

        test.setCompetenceses(new HashSet<>());
        assertThat(test.getCompetenceses()).doesNotContain(competenceBack);
    }
}
