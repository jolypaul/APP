package com.mycompany.skilltest.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.skilltest.domain.Question;
import com.mycompany.skilltest.domain.enumeration.Level;
import com.mycompany.skilltest.domain.enumeration.QuestionType;
import org.junit.jupiter.api.Test;

class AutomaticAnswerCorrectionServiceTest {

    private final AutomaticAnswerCorrectionService service = new AutomaticAnswerCorrectionService();

    @Test
    void shouldCorrectTrueFalseAnswers() {
        Question question = baseQuestion(QuestionType.VRAI_FAUX, "Vrai", null);

        var result = service.correct(question, "vrai");

        assertThat(result.isEstCorrecte()).isTrue();
        assertThat(result.getConfidence()).isEqualTo(1.0);
    }

    @Test
    void shouldCorrectQcmByOptionText() {
        Question question = baseQuestion(QuestionType.QCM, "useEffect", "useState - useEffect - useContext - useRef");

        var result = service.correct(question, "useEffect");

        assertThat(result.isEstCorrecte()).isTrue();
        assertThat(result.getCommentaireIa()).contains("aligné");
    }

    @Test
    void shouldAcceptOpenAnswerWhenKeywordsMatch() {
        Question question = baseQuestion(QuestionType.OUVERTE, "mecanisme ioc gerant les dependances entre beans", null);

        var result = service.correct(question, "C'est un mécanisme IoC qui gère les dépendances entre les beans Spring.");

        assertThat(result.isEstCorrecte()).isTrue();
        assertThat(result.getConfidence()).isGreaterThanOrEqualTo(0.5);
    }

    private Question baseQuestion(QuestionType type, String expectedAnswer, String options) {
        Question question = new Question();
        question.setId(1L);
        question.setType(type);
        question.setNiveau(Level.INTERMEDIAIRE);
        question.setPoints(10);
        question.setReponseAttendue(expectedAnswer);
        question.setChoixMultiple(options);
        question.setEnonce("Question de test");
        return question;
    }
}
