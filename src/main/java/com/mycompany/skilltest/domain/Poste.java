package com.mycompany.skilltest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.skilltest.domain.enumeration.Level;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Poste.
 */
@Entity
@Table(name = "poste")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Poste implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "intitule", nullable = false, unique = true)
    private String intitule;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_requis", nullable = false)
    private Level niveauRequis;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_poste__competences",
        joinColumns = @JoinColumn(name = "poste_id"),
        inverseJoinColumns = @JoinColumn(name = "competences_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "posteses", "testses" }, allowSetters = true)
    private Set<Competence> competenceses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Poste id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntitule() {
        return this.intitule;
    }

    public Poste intitule(String intitule) {
        this.setIntitule(intitule);
        return this;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getDescription() {
        return this.description;
    }

    public Poste description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Level getNiveauRequis() {
        return this.niveauRequis;
    }

    public Poste niveauRequis(Level niveauRequis) {
        this.setNiveauRequis(niveauRequis);
        return this;
    }

    public void setNiveauRequis(Level niveauRequis) {
        this.niveauRequis = niveauRequis;
    }

    public Set<Competence> getCompetenceses() {
        return this.competenceses;
    }

    public void setCompetenceses(Set<Competence> competences) {
        this.competenceses = competences;
    }

    public Poste competenceses(Set<Competence> competences) {
        this.setCompetenceses(competences);
        return this;
    }

    public Poste addCompetences(Competence competence) {
        this.competenceses.add(competence);
        return this;
    }

    public Poste removeCompetences(Competence competence) {
        this.competenceses.remove(competence);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Poste)) {
            return false;
        }
        return getId() != null && getId().equals(((Poste) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Poste{" +
            "id=" + getId() +
            ", intitule='" + getIntitule() + "'" +
            ", description='" + getDescription() + "'" +
            ", niveauRequis='" + getNiveauRequis() + "'" +
            "}";
    }
}
