package com.mycompany.skilltest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.skilltest.domain.enumeration.TestMode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Test.
 */
@Entity
@Table(name = "test")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Test implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "titre", nullable = false)
    private String titre;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private TestMode mode;

    @Column(name = "duree")
    private Integer duree;

    @NotNull
    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;

    @NotNull
    @Column(name = "actif", nullable = false)
    private Boolean actif;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_test__competences",
        joinColumns = @JoinColumn(name = "test_id"),
        inverseJoinColumns = @JoinColumn(name = "competences_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "posteses", "testses" }, allowSetters = true)
    private Set<Competence> competenceses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Test id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public Test titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return this.description;
    }

    public Test description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TestMode getMode() {
        return this.mode;
    }

    public Test mode(TestMode mode) {
        this.setMode(mode);
        return this;
    }

    public void setMode(TestMode mode) {
        this.mode = mode;
    }

    public Integer getDuree() {
        return this.duree;
    }

    public Test duree(Integer duree) {
        this.setDuree(duree);
        return this;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public Test dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public Test actif(Boolean actif) {
        this.setActif(actif);
        return this;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Set<Competence> getCompetenceses() {
        return this.competenceses;
    }

    public void setCompetenceses(Set<Competence> competences) {
        this.competenceses = competences;
    }

    public Test competenceses(Set<Competence> competences) {
        this.setCompetenceses(competences);
        return this;
    }

    public Test addCompetences(Competence competence) {
        this.competenceses.add(competence);
        return this;
    }

    public Test removeCompetences(Competence competence) {
        this.competenceses.remove(competence);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Test)) {
            return false;
        }
        return getId() != null && getId().equals(((Test) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Test{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", mode='" + getMode() + "'" +
            ", duree=" + getDuree() +
            ", dateCreation='" + getDateCreation() + "'" +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
