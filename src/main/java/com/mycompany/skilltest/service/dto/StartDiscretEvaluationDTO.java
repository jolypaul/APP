package com.mycompany.skilltest.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for starting a discreet evaluation.
 */
public class StartDiscretEvaluationDTO implements Serializable {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long testId;

    private Long managerId;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
}
