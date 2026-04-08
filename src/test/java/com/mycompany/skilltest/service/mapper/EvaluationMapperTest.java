package com.mycompany.skilltest.service.mapper;

import static com.mycompany.skilltest.domain.EvaluationAsserts.*;
import static com.mycompany.skilltest.domain.EvaluationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EvaluationMapperTest {

    private EvaluationMapper evaluationMapper;

    @BeforeEach
    void setUp() {
        evaluationMapper = new EvaluationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEvaluationSample1();
        var actual = evaluationMapper.toEntity(evaluationMapper.toDto(expected));
        assertEvaluationAllPropertiesEquals(expected, actual);
    }
}
