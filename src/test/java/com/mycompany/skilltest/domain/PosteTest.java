package com.mycompany.skilltest.domain;

import static com.mycompany.skilltest.domain.CompetenceTestSamples.*;
import static com.mycompany.skilltest.domain.PosteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PosteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Poste.class);
        Poste poste1 = getPosteSample1();
        Poste poste2 = new Poste();
        assertThat(poste1).isNotEqualTo(poste2);

        poste2.setId(poste1.getId());
        assertThat(poste1).isEqualTo(poste2);

        poste2 = getPosteSample2();
        assertThat(poste1).isNotEqualTo(poste2);
    }

    @Test
    void competencesTest() {
        Poste poste = getPosteRandomSampleGenerator();
        Competence competenceBack = getCompetenceRandomSampleGenerator();

        poste.addCompetences(competenceBack);
        assertThat(poste.getCompetenceses()).containsOnly(competenceBack);

        poste.removeCompetences(competenceBack);
        assertThat(poste.getCompetenceses()).doesNotContain(competenceBack);

        poste.competenceses(new HashSet<>(Set.of(competenceBack)));
        assertThat(poste.getCompetenceses()).containsOnly(competenceBack);

        poste.setCompetenceses(new HashSet<>());
        assertThat(poste.getCompetenceses()).doesNotContain(competenceBack);
    }
}
