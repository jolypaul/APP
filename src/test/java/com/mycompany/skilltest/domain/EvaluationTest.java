package com.mycompany.skilltest.domain;

import static com.mycompany.skilltest.domain.EmployeeTestSamples.*;
import static com.mycompany.skilltest.domain.EvaluationTestSamples.*;
import static com.mycompany.skilltest.domain.TestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EvaluationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Evaluation.class);
        Evaluation evaluation1 = getEvaluationSample1();
        Evaluation evaluation2 = new Evaluation();
        assertThat(evaluation1).isNotEqualTo(evaluation2);

        evaluation2.setId(evaluation1.getId());
        assertThat(evaluation1).isEqualTo(evaluation2);

        evaluation2 = getEvaluationSample2();
        assertThat(evaluation1).isNotEqualTo(evaluation2);
    }

    @Test
    void employeeTest() {
        Evaluation evaluation = getEvaluationRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        evaluation.setEmployee(employeeBack);
        assertThat(evaluation.getEmployee()).isEqualTo(employeeBack);

        evaluation.employee(null);
        assertThat(evaluation.getEmployee()).isNull();
    }

    @Test
    void testTest() {
        Evaluation evaluation = getEvaluationRandomSampleGenerator();
        Test testBack = getTestRandomSampleGenerator();

        evaluation.setTest(testBack);
        assertThat(evaluation.getTest()).isEqualTo(testBack);

        evaluation.test(null);
        assertThat(evaluation.getTest()).isNull();
    }

    @Test
    void managerTest() {
        Evaluation evaluation = getEvaluationRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        evaluation.setManager(employeeBack);
        assertThat(evaluation.getManager()).isEqualTo(employeeBack);

        evaluation.manager(null);
        assertThat(evaluation.getManager()).isNull();
    }
}
