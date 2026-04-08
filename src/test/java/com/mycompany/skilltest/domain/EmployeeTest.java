package com.mycompany.skilltest.domain;

import static com.mycompany.skilltest.domain.EmployeeTestSamples.*;
import static com.mycompany.skilltest.domain.PosteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employee.class);
        Employee employee1 = getEmployeeSample1();
        Employee employee2 = new Employee();
        assertThat(employee1).isNotEqualTo(employee2);

        employee2.setId(employee1.getId());
        assertThat(employee1).isEqualTo(employee2);

        employee2 = getEmployeeSample2();
        assertThat(employee1).isNotEqualTo(employee2);
    }

    @Test
    void posteTest() {
        Employee employee = getEmployeeRandomSampleGenerator();
        Poste posteBack = getPosteRandomSampleGenerator();

        employee.setPoste(posteBack);
        assertThat(employee.getPoste()).isEqualTo(posteBack);

        employee.poste(null);
        assertThat(employee.getPoste()).isNull();
    }
}
