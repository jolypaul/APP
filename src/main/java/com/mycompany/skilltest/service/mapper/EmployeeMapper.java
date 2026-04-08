package com.mycompany.skilltest.service.mapper;

import com.mycompany.skilltest.domain.Employee;
import com.mycompany.skilltest.domain.Poste;
import com.mycompany.skilltest.service.dto.EmployeeDTO;
import com.mycompany.skilltest.service.dto.PosteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Employee} and its DTO {@link EmployeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {
    @Mapping(target = "poste", source = "poste", qualifiedByName = "posteIntitule")
    EmployeeDTO toDto(Employee s);

    @Named("posteIntitule")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "intitule", source = "intitule")
    PosteDTO toDtoPosteIntitule(Poste poste);
}
