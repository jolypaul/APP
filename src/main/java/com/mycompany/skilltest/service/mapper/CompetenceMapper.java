package com.mycompany.skilltest.service.mapper;

import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.domain.Poste;
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.service.dto.CompetenceDTO;
import com.mycompany.skilltest.service.dto.PosteDTO;
import com.mycompany.skilltest.service.dto.TestDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Competence} and its DTO {@link CompetenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompetenceMapper extends EntityMapper<CompetenceDTO, Competence> {
    @Mapping(target = "posteses", source = "posteses", qualifiedByName = "posteIntituleSet")
    @Mapping(target = "testses", source = "testses", qualifiedByName = "testTitreSet")
    CompetenceDTO toDto(Competence s);

    @Mapping(target = "posteses", ignore = true)
    @Mapping(target = "removePostes", ignore = true)
    @Mapping(target = "testses", ignore = true)
    @Mapping(target = "removeTests", ignore = true)
    Competence toEntity(CompetenceDTO competenceDTO);

    @Named("posteIntitule")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "intitule", source = "intitule")
    PosteDTO toDtoPosteIntitule(Poste poste);

    @Named("posteIntituleSet")
    default Set<PosteDTO> toDtoPosteIntituleSet(Set<Poste> poste) {
        return poste.stream().map(this::toDtoPosteIntitule).collect(Collectors.toSet());
    }

    @Named("testTitre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titre", source = "titre")
    TestDTO toDtoTestTitre(Test test);

    @Named("testTitreSet")
    default Set<TestDTO> toDtoTestTitreSet(Set<Test> test) {
        return test.stream().map(this::toDtoTestTitre).collect(Collectors.toSet());
    }
}
