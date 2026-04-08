package com.mycompany.skilltest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Reponse.
 */
@Entity
@Table(name = "reponse")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "contenu", nullable = false)
    private String contenu;

    @Column(name = "est_correcte")
    private Boolean estCorrecte;

    @NotNull
    @Column(name = "date_reponse", nullable = false)
    private Instant dateReponse;

    @Lob
    @Column(name = "commentaire_manager")
    private String commentaireManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "test" }, allowSetters = true)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employee", "test", "manager" }, allowSetters = true)
    private Evaluation evaluation;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reponse id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return this.contenu;
    }

    public Reponse contenu(String contenu) {
        this.setContenu(contenu);
        return this;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Boolean getEstCorrecte() {
        return this.estCorrecte;
    }

    public Reponse estCorrecte(Boolean estCorrecte) {
        this.setEstCorrecte(estCorrecte);
        return this;
    }

    public void setEstCorrecte(Boolean estCorrecte) {
        this.estCorrecte = estCorrecte;
    }

    public Instant getDateReponse() {
        return this.dateReponse;
    }

    public Reponse dateReponse(Instant dateReponse) {
        this.setDateReponse(dateReponse);
        return this;
    }

    public void setDateReponse(Instant dateReponse) {
        this.dateReponse = dateReponse;
    }

    public String getCommentaireManager() {
        return this.commentaireManager;
    }

    public Reponse commentaireManager(String commentaireManager) {
        this.setCommentaireManager(commentaireManager);
        return this;
    }

    public void setCommentaireManager(String commentaireManager) {
        this.commentaireManager = commentaireManager;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Reponse question(Question question) {
        this.setQuestion(question);
        return this;
    }

    public Evaluation getEvaluation() {
        return this.evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public Reponse evaluation(Evaluation evaluation) {
        this.setEvaluation(evaluation);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reponse)) {
            return false;
        }
        return getId() != null && getId().equals(((Reponse) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reponse{" +
            "id=" + getId() +
            ", contenu='" + getContenu() + "'" +
            ", estCorrecte='" + getEstCorrecte() + "'" +
            ", dateReponse='" + getDateReponse() + "'" +
            ", commentaireManager='" + getCommentaireManager() + "'" +
            "}";
    }
}
