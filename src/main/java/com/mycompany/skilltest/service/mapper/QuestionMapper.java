package com.mycompany.skilltest.service.mapper;

import com.mycompany.skilltest.domain.Question;
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.service.dto.QuestionDTO;
import com.mycompany.skilltest.service.dto.TestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Question} and its DTO {@link QuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuestionMapper extends EntityMapper<QuestionDTO, Question> {
    @Mapping(target = "test", source = "test", qualifiedByName = "testTitre")
    QuestionDTO toDto(Question s);

    @Named("testTitre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titre", source = "titre")
    TestDTO toDtoTestTitre(Test test);
}
