package com.mycompany.skilltest.service.dto;

import com.mycompany.skilltest.domain.enumeration.Level;
import com.mycompany.skilltest.domain.enumeration.QuestionType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Question exposed by the discreet evaluation session.
 */
public class DiscretQuestionDTO implements Serializable {

    private Long id;

    private String enonce;

    private QuestionType type;

    private Level niveau;

    private Integer points;

    private List<String> options = new ArrayList<>();

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

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
