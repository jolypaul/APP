package com.mycompany.skilltest.service.dto;

import com.mycompany.skilltest.domain.enumeration.EvaluationStatus;
import com.mycompany.skilltest.domain.enumeration.Statut;
import com.mycompany.skilltest.domain.enumeration.TestMode;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.skilltest.domain.Evaluation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EvaluationDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant dateEvaluation;

    @NotNull
    private EvaluationStatus status;

    @NotNull
    private TestMode mode;

    private Double scoreTotal;

    @Lob
    private String remarques;

    private Statut statut;

    private EmployeeDTO employee;

    private TestDTO test;

    private EmployeeDTO manager;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(Instant dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public EvaluationStatus getStatus() {
        return status;
    }

    public void setStatus(EvaluationStatus status) {
        this.status = status;
    }

    public TestMode getMode() {
        return mode;
    }

    public void setMode(TestMode mode) {
        this.mode = mode;
    }

    public Double getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(Double scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public String getRemarques() {
        return remarques;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

    public TestDTO getTest() {
        return test;
    }

    public void setTest(TestDTO test) {
        this.test = test;
    }

    public EmployeeDTO getManager() {
        return manager;
    }

    public void setManager(EmployeeDTO manager) {
        this.manager = manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EvaluationDTO)) {
            return false;
        }

        EvaluationDTO evaluationDTO = (EvaluationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, evaluationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EvaluationDTO{" +
            "id=" + getId() +
            ", dateEvaluation='" + getDateEvaluation() + "'" +
            ", status='" + getStatus() + "'" +
            ", mode='" + getMode() + "'" +
            ", scoreTotal=" + getScoreTotal() +
            ", remarques='" + getRemarques() + "'" +
            ", statut='" + getStatut() + "'" +
            ", employee=" + getEmployee() +
            ", test=" + getTest() +
            ", manager=" + getManager() +
            "}";
    }
}
