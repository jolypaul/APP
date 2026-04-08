package com.mycompany.skilltest.service.mapper;

import static com.mycompany.skilltest.domain.TestAsserts.*;
import static com.mycompany.skilltest.domain.TestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMapperTest {

    private TestMapper testMapper;

    @BeforeEach
    void setUp() {
        testMapper = new TestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTestSample1();
        var actual = testMapper.toEntity(testMapper.toDto(expected));
        assertTestAllPropertiesEquals(expected, actual);
    }
}
