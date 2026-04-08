package com.mycompany.skilltest.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TestDTO.class);
        TestDTO testDTO1 = new TestDTO();
        testDTO1.setId(1L);
        TestDTO testDTO2 = new TestDTO();
        assertThat(testDTO1).isNotEqualTo(testDTO2);
        testDTO2.setId(testDTO1.getId());
        assertThat(testDTO1).isEqualTo(testDTO2);
        testDTO2.setId(2L);
        assertThat(testDTO1).isNotEqualTo(testDTO2);
        testDTO1.setId(null);
        assertThat(testDTO1).isNotEqualTo(testDTO2);
    }
}
