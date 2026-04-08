package com.mycompany.skilltest.service.mapper;

import static com.mycompany.skilltest.domain.ScoreAsserts.*;
import static com.mycompany.skilltest.domain.ScoreTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScoreMapperTest {

    private ScoreMapper scoreMapper;

    @BeforeEach
    void setUp() {
        scoreMapper = new ScoreMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getScoreSample1();
        var actual = scoreMapper.toEntity(scoreMapper.toDto(expected));
        assertScoreAllPropertiesEquals(expected, actual);
    }
}
