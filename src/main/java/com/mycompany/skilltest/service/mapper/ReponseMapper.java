package com.mycompany.skilltest.service.mapper;

import com.mycompany.skilltest.domain.Evaluation;
import com.mycompany.skilltest.domain.Question;
import com.mycompany.skilltest.domain.Reponse;
import com.mycompany.skilltest.service.dto.EvaluationDTO;
import com.mycompany.skilltest.service.dto.QuestionDTO;
import com.mycompany.skilltest.service.dto.ReponseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reponse} and its DTO {@link ReponseDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReponseMapper extends EntityMapper<ReponseDTO, Reponse> {
    @Mapping(target = "question", source = "question", qualifiedByName = "questionEnonce")
    @Mapping(target = "evaluation", source = "evaluation", qualifiedByName = "evaluationId")
    ReponseDTO toDto(Reponse s);

    @Named("questionEnonce")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "enonce", source = "enonce")
    QuestionDTO toDtoQuestionEnonce(Question question);

    @Named("evaluationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EvaluationDTO toDtoEvaluationId(Evaluation evaluation);

    default String map(byte[] value) {
        return new String(value);
    }
}
