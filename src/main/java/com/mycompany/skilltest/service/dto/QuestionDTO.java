package com.mycompany.skilltest.service.dto;

import com.mycompany.skilltest.domain.enumeration.Level;
import com.mycompany.skilltest.domain.enumeration.QuestionType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.skilltest.domain.Question} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionDTO implements Serializable {

    private Long id;

    @Lob
    private String enonce;

    @NotNull
    private QuestionType type;

    @NotNull
    private Level niveau;

    @NotNull
    private Integer points;

    @Lob
    private String choixMultiple;

    @Lob
    private String reponseAttendue;

    private TestDTO test;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnonce() {
        return enonce;
    }

    public void setEnonce(String enonce) {
        this.enonce = enonce;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public Level getNiveau() {
        return niveau;
    }

    public void setNiveau(Level niveau) {
        this.niveau = niveau;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getChoixMultiple() {
        return choixMultiple;
    }

    public void setChoixMultiple(String choixMultiple) {
        this.choixMultiple = choixMultiple;
    }

    public String getReponseAttendue() {
        return reponseAttendue;
    }

    public void setReponseAttendue(String reponseAttendue) {
        this.reponseAttendue = reponseAttendue;
    }

    public TestDTO getTest() {
        return test;
    }

    public void setTest(TestDTO test) {
        this.test = test;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionDTO)) {
            return false;
        }

        QuestionDTO questionDTO = (QuestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, questionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionDTO{" +
            "id=" + getId() +
            ", enonce='" + getEnonce() + "'" +
            ", type='" + getType() + "'" +
            ", niveau='" + getNiveau() + "'" +
            ", points=" + getPoints() +
            ", choixMultiple='" + getChoixMultiple() + "'" +
            ", reponseAttendue='" + getReponseAttendue() + "'" +
            ", test=" + getTest() +
            "}";
    }
}
