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
 * A Competence.
 */
@Entity
@Table(name = "competence")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Competence implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false, unique = true)
    private String nom;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "categorie")
    private String categorie;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_attendu", nullable = false)
    private Level niveauAttendu;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "competenceses")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "competenceses" }, allowSetters = true)
    private Set<Poste> posteses = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "competenceses")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "competenceses" }, allowSetters = true)
    private Set<Test> testses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Competence id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Competence nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return this.description;
    }

    public Competence description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return this.categorie;
    }

    public Competence categorie(String categorie) {
        this.setCategorie(categorie);
        return this;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Level getNiveauAttendu() {
        return this.niveauAttendu;
    }

    public Competence niveauAttendu(Level niveauAttendu) {
        this.setNiveauAttendu(niveauAttendu);
        return this;
    }

    public void setNiveauAttendu(Level niveauAttendu) {
        this.niveauAttendu = niveauAttendu;
    }

    public Set<Poste> getPosteses() {
        return this.posteses;
    }

    public void setPosteses(Set<Poste> postes) {
        if (this.posteses != null) {
            this.posteses.forEach(i -> i.removeCompetences(this));
        }
        if (postes != null) {
            postes.forEach(i -> i.addCompetences(this));
        }
        this.posteses = postes;
    }

    public Competence posteses(Set<Poste> postes) {
        this.setPosteses(postes);
        return this;
    }

    public Competence addPostes(Poste poste) {
        this.posteses.add(poste);
        poste.getCompetenceses().add(this);
        return this;
    }

    public Competence removePostes(Poste poste) {
        this.posteses.remove(poste);
        poste.getCompetenceses().remove(this);
        return this;
    }

    public Set<Test> getTestses() {
        return this.testses;
    }

    public void setTestses(Set<Test> tests) {
        if (this.testses != null) {
            this.testses.forEach(i -> i.removeCompetences(this));
        }
        if (tests != null) {
            tests.forEach(i -> i.addCompetences(this));
        }
        this.testses = tests;
    }

    public Competence testses(Set<Test> tests) {
        this.setTestses(tests);
        return this;
    }

    public Competence addTests(Test test) {
        this.testses.add(test);
        test.getCompetenceses().add(this);
        return this;
    }

    public Competence removeTests(Test test) {
        this.testses.remove(test);
        test.getCompetenceses().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Competence)) {
            return false;
        }
        return getId() != null && getId().equals(((Competence) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Competence{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", categorie='" + getCategorie() + "'" +
            ", niveauAttendu='" + getNiveauAttendu() + "'" +
            "}";
    }
}
