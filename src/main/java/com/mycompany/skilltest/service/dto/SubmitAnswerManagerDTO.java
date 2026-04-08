package com.mycompany.skilltest.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for submitting an answer in discreet mode (manager enters on behalf of employee).
 */
public class SubmitAnswerManagerDTO implements Serializable {

    @NotNull
    private Long evaluationId;

    @NotNull
    private Long questionId;

    @NotNull
    private String contenu;

    @NotNull
    private Boolean estCorrecte;

    private String commentaireManager;

    public Long getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

    public String getCommentaireManager() {
        return commentaireManager;
    }

    public void setCommentaireManager(String commentaireManager) {
        this.commentaireManager = commentaireManager;
    }
}
