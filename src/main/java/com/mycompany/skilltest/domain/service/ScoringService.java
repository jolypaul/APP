package com.mycompany.skilltest.domain.service;

import com.mycompany.skilltest.domain.Evaluation;
import com.mycompany.skilltest.domain.Reponse;
import com.mycompany.skilltest.domain.Score;
import com.mycompany.skilltest.domain.enumeration.EvaluationStatus;
import com.mycompany.skilltest.domain.enumeration.Statut;
import com.mycompany.skilltest.repository.ReponseRepository;
import com.mycompany.skilltest.repository.ScoreRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Domain Service — Scoring logic.
 * score = (bonnes réponses / total) * 100
 * >= 80 → CONFORME
 * 50–79 → A_AMELIORER
 * < 50 → NON_CONFORME
 */
@Service
@Transactional
public class ScoringService {

    private final ReponseRepository reponseRepository;
    private final ScoreRepository scoreRepository;

    public ScoringService(ReponseRepository reponseRepository, ScoreRepository scoreRepository) {
        this.reponseRepository = reponseRepository;
        this.scoreRepository = scoreRepository;
    }

    public double calculateScore(List<Reponse> reponses) {
        if (reponses == null || reponses.isEmpty()) {
            return 0.0;
        }
        long total = reponses.size();
        long correct = reponses.stream().filter(r -> Boolean.TRUE.equals(r.getEstCorrecte())).count();
        return ((double) correct / total) * 100;
    }

    public Statut determineStatut(double pourcentage) {
        if (pourcentage >= 80) {
            return Statut.CONFORME;
        } else if (pourcentage >= 50) {
            return Statut.A_AMELIORER;
        } else {
            return Statut.NON_CONFORME;
        }
    }

    public Score createScore(Evaluation evaluation, com.mycompany.skilltest.domain.Competence competence, List<Reponse> reponses) {
        double pourcentage = calculateScore(reponses);
        Statut statut = determineStatut(pourcentage);

        Score score = new Score();
        score.setValeur((double) reponses.stream().filter(r -> Boolean.TRUE.equals(r.getEstCorrecte())).count());
        score.setPourcentage(pourcentage);
        score.setStatut(statut);
        score.setDateCalcul(Instant.now());
        score.setEvaluation(evaluation);
        score.setCompetence(competence);

        return scoreRepository.save(score);
    }

    public Evaluation finalizeEvaluation(Evaluation evaluation, List<Reponse> allReponses) {
        double pourcentage = calculateScore(allReponses);
        evaluation.setScoreTotal(pourcentage);
        evaluation.setStatut(determineStatut(pourcentage));
        evaluation.setStatus(EvaluationStatus.TERMINEE);
        return evaluation;
    }
}
