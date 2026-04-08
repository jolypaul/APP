package com.mycompany.skilltest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.skilltest.domain.enumeration.EvaluationStatus;
import com.mycompany.skilltest.domain.enumeration.Statut;
import com.mycompany.skilltest.domain.enumeration.TestMode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Evaluation.
 */
@Entity
@Table(name = "evaluation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Evaluation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date_evaluation", nullable = false)
    private Instant dateEvaluation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EvaluationStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private TestMode mode;

    @Column(name = "score_total")
    private Double scoreTotal;

    @Lob
    @Column(name = "remarques")
    private String remarques;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private Statut statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "poste" }, allowSetters = true)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "competenceses" }, allowSetters = true)
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "poste" }, allowSetters = true)
    private Employee manager;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Evaluation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateEvaluation() {
        return this.dateEvaluation;
    }

    public Evaluation dateEvaluation(Instant dateEvaluation) {
        this.setDateEvaluation(dateEvaluation);
        return this;
    }

    public void setDateEvaluation(Instant dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public EvaluationStatus getStatus() {
        return this.status;
    }

    public Evaluation status(EvaluationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EvaluationStatus status) {
        this.status = status;
    }

    public TestMode getMode() {
        return this.mode;
    }

    public Evaluation mode(TestMode mode) {
        this.setMode(mode);
        return this;
    }

    public void setMode(TestMode mode) {
        this.mode = mode;
    }

    public Double getScoreTotal() {
        return this.scoreTotal;
    }

    public Evaluation scoreTotal(Double scoreTotal) {
        this.setScoreTotal(scoreTotal);
        return this;
    }

    public void setScoreTotal(Double scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public String getRemarques() {
        return this.remarques;
    }

    public Evaluation remarques(String remarques) {
        this.setRemarques(remarques);
        return this;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }

    public Statut getStatut() {
        return this.statut;
    }

    public Evaluation statut(Statut statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Evaluation employee(Employee employee) {
        this.setEmployee(employee);
        return this;
    }

    public Test getTest() {
        return this.test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Evaluation test(Test test) {
        this.setTest(test);
        return this;
    }

    public Employee getManager() {
        return this.manager;
    }

    public void setManager(Employee employee) {
        this.manager = employee;
    }

    public Evaluation manager(Employee employee) {
        this.setManager(employee);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evaluation)) {
            return false;
        }
        return getId() != null && getId().equals(((Evaluation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Evaluation{" +
            "id=" + getId() +
            ", dateEvaluation='" + getDateEvaluation() + "'" +
            ", status='" + getStatus() + "'" +
            ", mode='" + getMode() + "'" +
            ", scoreTotal=" + getScoreTotal() +
            ", remarques='" + getRemarques() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
