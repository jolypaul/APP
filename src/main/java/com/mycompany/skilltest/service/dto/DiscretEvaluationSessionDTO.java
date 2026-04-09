package com.mycompany.skilltest.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Payload returned when starting a discreet evaluation.
 */
public class DiscretEvaluationSessionDTO implements Serializable {

    private EvaluationDTO evaluation;

    private EmployeeDTO manager;

    private List<DiscretQuestionDTO> questions = new ArrayList<>();

    private int resumeIndex = 0;

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public EmployeeDTO getManager() {
        return manager;
    }

    public void setManager(EmployeeDTO manager) {
        this.manager = manager;
    }

    public List<DiscretQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<DiscretQuestionDTO> questions) {
        this.questions = questions;
    }

    public int getResumeIndex() {
        return resumeIndex;
    }

    public void setResumeIndex(int resumeIndex) {
        this.resumeIndex = resumeIndex;
    }
}
