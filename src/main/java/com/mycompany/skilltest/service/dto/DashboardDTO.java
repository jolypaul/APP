package com.mycompany.skilltest.service.dto;

import java.io.Serializable;

/**
 * DTO for dashboard statistics.
 */
public class DashboardDTO implements Serializable {

    private double conformitePourcentage;
    private long totalEmployees;
    private long totalEvaluations;
    private long evaluationsConformes;
    private long evaluationsAAmeliorer;
    private long evaluationsNonConformes;

    public double getConformitePourcentage() {
        return conformitePourcentage;
    }

    public void setConformitePourcentage(double conformitePourcentage) {
        this.conformitePourcentage = conformitePourcentage;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getTotalEvaluations() {
        return totalEvaluations;
    }

    public void setTotalEvaluations(long totalEvaluations) {
        this.totalEvaluations = totalEvaluations;
    }

    public long getEvaluationsConformes() {
        return evaluationsConformes;
    }

    public void setEvaluationsConformes(long evaluationsConformes) {
        this.evaluationsConformes = evaluationsConformes;
    }

    public long getEvaluationsAAmeliorer() {
        return evaluationsAAmeliorer;
    }

    public void setEvaluationsAAmeliorer(long evaluationsAAmeliorer) {
        this.evaluationsAAmeliorer = evaluationsAAmeliorer;
    }

    public long getEvaluationsNonConformes() {
        return evaluationsNonConformes;
    }

    public void setEvaluationsNonConformes(long evaluationsNonConformes) {
        this.evaluationsNonConformes = evaluationsNonConformes;
    }
}
