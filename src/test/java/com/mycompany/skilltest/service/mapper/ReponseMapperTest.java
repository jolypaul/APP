package com.mycompany.skilltest.service.mapper;

import static com.mycompany.skilltest.domain.ReponseAsserts.*;
import static com.mycompany.skilltest.domain.ReponseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReponseMapperTest {

    private ReponseMapper reponseMapper;

    @BeforeEach
    void setUp() {
        reponseMapper = new ReponseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReponseSample1();
        var actual = reponseMapper.toEntity(reponseMapper.toDto(expected));
        assertReponseAllPropertiesEquals(expected, actual);
    }
}
