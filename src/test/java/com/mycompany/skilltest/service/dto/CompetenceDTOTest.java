package com.mycompany.skilltest.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompetenceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompetenceDTO.class);
        CompetenceDTO competenceDTO1 = new CompetenceDTO();
        competenceDTO1.setId(1L);
        CompetenceDTO competenceDTO2 = new CompetenceDTO();
        assertThat(competenceDTO1).isNotEqualTo(competenceDTO2);
        competenceDTO2.setId(competenceDTO1.getId());
        assertThat(competenceDTO1).isEqualTo(competenceDTO2);
        competenceDTO2.setId(2L);
        assertThat(competenceDTO1).isNotEqualTo(competenceDTO2);
        competenceDTO1.setId(null);
        assertThat(competenceDTO1).isNotEqualTo(competenceDTO2);
    }
}
