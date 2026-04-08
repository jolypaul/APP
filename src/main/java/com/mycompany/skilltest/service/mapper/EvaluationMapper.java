package com.mycompany.skilltest.service.mapper;

import com.mycompany.skilltest.domain.Employee;
import com.mycompany.skilltest.domain.Evaluation;
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.service.dto.EmployeeDTO;
import com.mycompany.skilltest.service.dto.EvaluationDTO;
import com.mycompany.skilltest.service.dto.TestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Evaluation} and its DTO {@link EvaluationDTO}.
 */
@Mapper(componentModel = "spring")
public interface EvaluationMapper extends EntityMapper<EvaluationDTO, Evaluation> {
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeNom")
    @Mapping(target = "test", source = "test", qualifiedByName = "testTitre")
    @Mapping(target = "manager", source = "manager", qualifiedByName = "employeeNom")
    EvaluationDTO toDto(Evaluation s);

    @Named("employeeNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    EmployeeDTO toDtoEmployeeNom(Employee employee);

    @Named("testTitre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titre", source = "titre")
    TestDTO toDtoTestTitre(Test test);
}
