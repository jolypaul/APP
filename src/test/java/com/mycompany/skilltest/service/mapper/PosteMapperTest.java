package com.mycompany.skilltest.service.mapper;

import static com.mycompany.skilltest.domain.PosteAsserts.*;
import static com.mycompany.skilltest.domain.PosteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PosteMapperTest {

    private PosteMapper posteMapper;

    @BeforeEach
    void setUp() {
        posteMapper = new PosteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPosteSample1();
        var actual = posteMapper.toEntity(posteMapper.toDto(expected));
        assertPosteAllPropertiesEquals(expected, actual);
    }
}
