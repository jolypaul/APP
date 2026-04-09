package com.mycompany.skilltest.service.dto;

import com.mycompany.skilltest.domain.enumeration.Level;
import com.mycompany.skilltest.domain.enumeration.QuestionType;
import com.mycompany.skilltest.domain.enumeration.TestMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestBuilderDTO {

    private Long id;

    @NotNull
    private String titre;

    private String description;

    @NotNull
    private TestMode mode;

    private Integer duree;

    @NotNull
    private Boolean actif;

    private Set<Long> competenceIds = new HashSet<>();

    @Valid
    private List<QuestionItem> questions = new ArrayList<>();

    public static class QuestionItem {

        private Long id;

        @NotNull
        private String enonce;

        @NotNull
        private QuestionType type;

        @NotNull
        private Level niveau;

        @NotNull
        private Integer points;

        private String choixMultiple;

        private String reponseAttendue;

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
    }

    // -- getters & setters --

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TestMode getMode() {
        return mode;
    }

    public void setMode(TestMode mode) {
        this.mode = mode;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Set<Long> getCompetenceIds() {
        return competenceIds;
    }

    public void setCompetenceIds(Set<Long> competenceIds) {
        this.competenceIds = competenceIds;
    }

    public List<QuestionItem> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionItem> questions) {
        this.questions = questions;
    }
}
