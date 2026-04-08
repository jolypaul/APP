package com.mycompany.skilltest.service.impl;

import com.mycompany.skilltest.domain.Employee;
import com.mycompany.skilltest.repository.EmployeeRepository;
import com.mycompany.skilltest.service.EmployeeService;
import com.mycompany.skilltest.service.UserService;
import com.mycompany.skilltest.service.dto.EmployeeDTO;
import com.mycompany.skilltest.service.mapper.EmployeeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.skilltest.domain.Employee}.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final UserService userService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, UserService userService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.userService = userService;
    }

    @Override
    public EmployeeDTO save(EmployeeDTO employeeDTO) {
        LOG.debug("Request to save Employee : {}", employeeDTO);
        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee = employeeRepository.save(employee);
        EmployeeDTO result = employeeMapper.toDto(employee);
        boolean hasUserAccount = userService.syncUserForEmployee(result, null);
        enrichAccessInfo(result, hasUserAccount);
        return result;
    }

    @Override
    public EmployeeDTO update(EmployeeDTO employeeDTO) {
        LOG.debug("Request to update Employee : {}", employeeDTO);
        String previousEmail = employeeRepository.findById(employeeDTO.getId()).map(Employee::getEmail).orElse(null);
        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee = employeeRepository.save(employee);
        EmployeeDTO result = employeeMapper.toDto(employee);
        boolean hasUserAccount = userService.syncUserForEmployee(result, previousEmail);
        enrichAccessInfo(result, hasUserAccount);
        return result;
    }

    @Override
    public Optional<EmployeeDTO> partialUpdate(EmployeeDTO employeeDTO) {
        LOG.debug("Request to partially update Employee : {}", employeeDTO);

        return employeeRepository
            .findById(employeeDTO.getId())
            .map(existingEmployee -> {
                String previousEmail = existingEmployee.getEmail();
                employeeMapper.partialUpdate(existingEmployee, employeeDTO);

                return new Object[] { existingEmployee, previousEmail };
            })
            .map(tuple -> {
                Employee savedEmployee = employeeRepository.save((Employee) tuple[0]);
                EmployeeDTO result = employeeMapper.toDto(savedEmployee);
                boolean hasUserAccount = userService.syncUserForEmployee(result, (String) tuple[1]);
                enrichAccessInfo(result, hasUserAccount);
                return result;
            });
    }

    public Page<EmployeeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return employeeRepository.findAllWithEagerRelationships(pageable).map(employeeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> findOne(Long id) {
        LOG.debug("Request to get Employee : {}", id);
        return employeeRepository.findOneWithEagerRelationships(id).map(employeeMapper::toDto).map(this::enrichAccessInfo);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Employee : {}", id);
        employeeRepository.findById(id).map(Employee::getEmail).ifPresent(userService::removeUserByLoginOrEmail);
        employeeRepository.deleteById(id);
    }

    private EmployeeDTO enrichAccessInfo(EmployeeDTO employeeDTO) {
        return enrichAccessInfo(
            employeeDTO,
            employeeDTO.getRole() != null &&
                employeeDTO.getRole().name() != null &&
                employeeDTO.getRole() != com.mycompany.skilltest.domain.enumeration.Role.EMPLOYEE
        );
    }

    private EmployeeDTO enrichAccessInfo(EmployeeDTO employeeDTO, boolean hasUserAccount) {
        boolean canLogin = employeeDTO.getRole() != com.mycompany.skilltest.domain.enumeration.Role.EMPLOYEE;
        employeeDTO.setCanLogin(canLogin);
        employeeDTO.setHasUserAccount(hasUserAccount && canLogin);
        employeeDTO.setDefaultPasswordHint(canLogin ? UserService.DEFAULT_EMPLOYEE_PASSWORD : null);
        return employeeDTO;
    }
}
