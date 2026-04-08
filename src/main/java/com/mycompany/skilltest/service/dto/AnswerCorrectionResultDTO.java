package com.mycompany.skilltest.service.dto;

import java.io.Serializable;

/**
 * Feedback returned after automatic answer correction.
 */
public class AnswerCorrectionResultDTO implements Serializable {

    private Long questionId;

    private boolean estCorrecte;

    private double confidence;

    private String commentaireIa;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public boolean isEstCorrecte() {
        return estCorrecte;
    }

    public void setEstCorrecte(boolean estCorrecte) {
        this.estCorrecte = estCorrecte;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getCommentaireIa() {
        return commentaireIa;
    }

    public void setCommentaireIa(String commentaireIa) {
        this.commentaireIa = commentaireIa;
    }
}
