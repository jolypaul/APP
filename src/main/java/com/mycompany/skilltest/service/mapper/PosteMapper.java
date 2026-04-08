package com.mycompany.skilltest.service.mapper;

import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.domain.Poste;
import com.mycompany.skilltest.service.dto.CompetenceDTO;
import com.mycompany.skilltest.service.dto.PosteDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Poste} and its DTO {@link PosteDTO}.
 */
@Mapper(componentModel = "spring")
public interface PosteMapper extends EntityMapper<PosteDTO, Poste> {
    @Mapping(target = "competenceses", source = "competenceses", qualifiedByName = "competenceNomSet")
    PosteDTO toDto(Poste s);

    @Mapping(target = "removeCompetences", ignore = true)
    Poste toEntity(PosteDTO posteDTO);

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
