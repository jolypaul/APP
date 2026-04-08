package com.mycompany.skilltest.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.skilltest.domain.Reponse} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReponseDTO implements Serializable {

    private Long id;

    @Lob
    private String contenu;

    private Boolean estCorrecte;

    @NotNull
    private Instant dateReponse;

    @Lob
    private String commentaireManager;

    private QuestionDTO question;

    private EvaluationDTO evaluation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Boolean getEstCorrecte() {
        return estCorrecte;
    }

    public void setEstCorrecte(Boolean estCorrecte) {
        this.estCorrecte = estCorrecte;
    }

    public Instant getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(Instant dateReponse) {
        this.dateReponse = dateReponse;
    }

    public String getCommentaireManager() {
        return commentaireManager;
    }

    public void setCommentaireManager(String commentaireManager) {
        this.commentaireManager = commentaireManager;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReponseDTO)) {
            return false;
        }

        ReponseDTO reponseDTO = (ReponseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reponseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReponseDTO{" +
            "id=" + getId() +
            ", contenu='" + getContenu() + "'" +
            ", estCorrecte='" + getEstCorrecte() + "'" +
            ", dateReponse='" + getDateReponse() + "'" +
            ", commentaireManager='" + getCommentaireManager() + "'" +
            ", question=" + getQuestion() +
            ", evaluation=" + getEvaluation() +
            "}";
    }
}
