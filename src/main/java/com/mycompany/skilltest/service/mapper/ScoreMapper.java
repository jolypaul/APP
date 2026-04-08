package com.mycompany.skilltest.service.mapper;

import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.domain.Evaluation;
import com.mycompany.skilltest.domain.Score;
import com.mycompany.skilltest.service.dto.CompetenceDTO;
import com.mycompany.skilltest.service.dto.EvaluationDTO;
import com.mycompany.skilltest.service.dto.ScoreDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Score} and its DTO {@link ScoreDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScoreMapper extends EntityMapper<ScoreDTO, Score> {
    @Mapping(target = "evaluation", source = "evaluation", qualifiedByName = "evaluationId")
    @Mapping(target = "competence", source = "competence", qualifiedByName = "competenceNom")
    ScoreDTO toDto(Score s);

    @Named("evaluationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EvaluationDTO toDtoEvaluationId(Evaluation evaluation);

    @Named("competenceNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    CompetenceDTO toDtoCompetenceNom(Competence competence);
}
