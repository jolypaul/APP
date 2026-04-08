package com.mycompany.skilltest.service.mapper;

import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.service.dto.CompetenceDTO;
import com.mycompany.skilltest.service.dto.TestDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Test} and its DTO {@link TestDTO}.
 */
@Mapper(componentModel = "spring")
public interface TestMapper extends EntityMapper<TestDTO, Test> {
    @Mapping(target = "competenceses", source = "competenceses", qualifiedByName = "competenceNomSet")
    TestDTO toDto(Test s);

    @Mapping(target = "removeCompetences", ignore = true)
    Test toEntity(TestDTO testDTO);

    @Named("competenceNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    CompetenceDTO toDtoCompetenceNom(Competence competence);

    @Named("competenceNomSet")
    default Set<CompetenceDTO> toDtoCompetenceNomSet(Set<Competence> competence) {
        return competence.stream().map(this::toDtoCompetenceNom).collect(Collectors.toSet());
    }
}
