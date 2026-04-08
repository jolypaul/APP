package com.mycompany.skilltest.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReponseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReponseDTO.class);
        ReponseDTO reponseDTO1 = new ReponseDTO();
        reponseDTO1.setId(1L);
        ReponseDTO reponseDTO2 = new ReponseDTO();
        assertThat(reponseDTO1).isNotEqualTo(reponseDTO2);
        reponseDTO2.setId(reponseDTO1.getId());
        assertThat(reponseDTO1).isEqualTo(reponseDTO2);
        reponseDTO2.setId(2L);
        assertThat(reponseDTO1).isNotEqualTo(reponseDTO2);
        reponseDTO1.setId(null);
        assertThat(reponseDTO1).isNotEqualTo(reponseDTO2);
    }
}
