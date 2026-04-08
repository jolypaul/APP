package com.mycompany.skilltest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.skilltest.domain.enumeration.Level;
import com.mycompany.skilltest.domain.enumeration.QuestionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Question.
 */
@Entity
@Table(name = "question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "enonce", nullable = false)
    private String enonce;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private QuestionType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau", nullable = false)
    private Level niveau;

    @NotNull
    @Column(name = "points", nullable = false)
    private Integer points;

    @Lob
    @Column(name = "choix_multiple")
    private String choixMultiple;

    @Lob
    @Column(name = "reponse_attendue")
    private String reponseAttendue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "competenceses" }, allowSetters = true)
    private Test test;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnonce() {
        return this.enonce;
    }

    public Question enonce(String enonce) {
        this.setEnonce(enonce);
        return this;
    }

    public void setEnonce(String enonce) {
        this.enonce = enonce;
    }

    public QuestionType getType() {
        return this.type;
    }

    public Question type(QuestionType type) {
        this.setType(type);
        return this;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public Level getNiveau() {
        return this.niveau;
    }

    public Question niveau(Level niveau) {
        this.setNiveau(niveau);
        return this;
    }

    public void setNiveau(Level niveau) {
        this.niveau = niveau;
    }

    public Integer getPoints() {
        return this.points;
    }

    public Question points(Integer points) {
        this.setPoints(points);
        return this;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getChoixMultiple() {
        return this.choixMultiple;
    }

    public Question choixMultiple(String choixMultiple) {
        this.setChoixMultiple(choixMultiple);
        return this;
    }

    public void setChoixMultiple(String choixMultiple) {
        this.choixMultiple = choixMultiple;
    }

    public String getReponseAttendue() {
        return this.reponseAttendue;
    }

    public Question reponseAttendue(String reponseAttendue) {
        this.setReponseAttendue(reponseAttendue);
        return this;
    }

    public void setReponseAttendue(String reponseAttendue) {
        this.reponseAttendue = reponseAttendue;
    }

    public Test getTest() {
        return this.test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Question test(Test test) {
        this.setTest(test);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", enonce='" + getEnonce() + "'" +
            ", type='" + getType() + "'" +
            ", niveau='" + getNiveau() + "'" +
            ", points=" + getPoints() +
            ", choixMultiple='" + getChoixMultiple() + "'" +
            ", reponseAttendue='" + getReponseAttendue() + "'" +
            "}";
    }
}
