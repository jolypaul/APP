package com.mycompany.skilltest.service;

import com.mycompany.skilltest.domain.Question;
import com.mycompany.skilltest.domain.enumeration.QuestionType;
import com.mycompany.skilltest.service.dto.AnswerCorrectionResultDTO;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Lightweight automatic correction used by discreet evaluations.
 */
@Service
public class AutomaticAnswerCorrectionService {

    private static final Pattern SPLIT_OPTIONS = Pattern.compile("\\s+-\\s+");
    private static final Pattern NON_ALPHANUM = Pattern.compile("[^a-z0-9\\s]");
    private static final Set<String> STOP_WORDS = Set.of(
        "avec",
        "dans",
        "pour",
        "sans",
        "plus",
        "moins",
        "dont",
        "une",
        "des",
        "les",
        "est",
        "sur",
        "par",
        "que",
        "qui",
        "aux",
        "the",
        "and"
    );

    public AnswerCorrectionResultDTO correct(Question question, String rawAnswer) {
        String normalizedAnswer = normalize(rawAnswer);
        String normalizedExpected = normalize(question.getReponseAttendue());

        AnswerCorrectionResultDTO result = new AnswerCorrectionResultDTO();
        result.setQuestionId(question.getId());

        switch (question.getType()) {
            case VRAI_FAUX -> fillTrueFalseResult(result, normalizedAnswer, normalizedExpected);
            case QCM -> fillQcmResult(result, question, normalizedAnswer, normalizedExpected);
            case OUVERTE, PRATIQUE -> fillOpenResult(result, normalizedAnswer, normalizedExpected);
            default -> fillOpenResult(result, normalizedAnswer, normalizedExpected);
        }

        return result;
    }

    private void fillTrueFalseResult(AnswerCorrectionResultDTO result, String normalizedAnswer, String normalizedExpected) {
        boolean answerValue = toBoolean(normalizedAnswer);
        boolean expectedValue = toBoolean(normalizedExpected);
        boolean isCorrect = normalizedAnswer.length() > 0 && answerValue == expectedValue;

        result.setEstCorrecte(isCorrect);
        result.setConfidence(isCorrect ? 1.0 : 0.2);
        result.setCommentaireIa(
            isCorrect
                ? "Réponse cohérente avec l'attendu."
                : "Réponse différente de l'attendu. Vérifier la reformulation donnée par l'employé."
        );
    }

    private void fillQcmResult(AnswerCorrectionResultDTO result, Question question, String normalizedAnswer, String normalizedExpected) {
        List<String> options = parseOptions(question.getChoixMultiple()).stream().map(this::normalize).toList();
        int expectedIndex = findBestMatchingOptionIndex(options, normalizedExpected);
        int answerIndex = findAnswerOptionIndex(options, normalizedAnswer);

        boolean isCorrect = answerIndex >= 0 && answerIndex == expectedIndex;
        if (!isCorrect && normalizedExpected.equals(normalizedAnswer)) {
            isCorrect = true;
        }

        result.setEstCorrecte(isCorrect);
        result.setConfidence(isCorrect ? 0.95 : 0.35);
        result.setCommentaireIa(
            isCorrect ? "Choix aligné avec la bonne réponse attendue." : "Le choix saisi ne correspond pas à la bonne option attendue."
        );
    }

    private void fillOpenResult(AnswerCorrectionResultDTO result, String normalizedAnswer, String normalizedExpected) {
        Set<String> expectedTokens = tokenize(normalizedExpected);
        Set<String> answerTokens = tokenize(normalizedAnswer);

        if (expectedTokens.isEmpty()) {
            boolean isCorrect = normalizedExpected.equals(normalizedAnswer) || normalizedAnswer.contains(normalizedExpected);
            result.setEstCorrecte(isCorrect);
            result.setConfidence(isCorrect ? 0.8 : 0.2);
            result.setCommentaireIa(isCorrect ? "Réponse acceptable." : "Réponse insuffisamment proche de l'attendu.");
            return;
        }

        long matched = expectedTokens.stream().filter(answerTokens::contains).count();
        double ratio = (double) matched / expectedTokens.size();
        boolean isCorrect = ratio >= 0.5 || normalizedAnswer.contains(normalizedExpected);

        result.setEstCorrecte(isCorrect);
        result.setConfidence(Math.min(0.98, Math.max(0.2, ratio)));

        if (isCorrect) {
            result.setCommentaireIa("Réponse suffisamment proche des éléments attendus.");
        } else {
            String missing = expectedTokens
                .stream()
                .filter(token -> !answerTokens.contains(token))
                .sorted(Comparator.naturalOrder())
                .limit(3)
                .collect(Collectors.joining(", "));
            result.setCommentaireIa(
                missing.isBlank() ? "Réponse trop éloignée de l'attendu." : "Éléments attendus manquants: " + missing + "."
            );
        }
    }

    private List<String> parseOptions(String choixMultiple) {
        if (choixMultiple == null || choixMultiple.isBlank()) {
            return List.of();
        }
        return Arrays.stream(SPLIT_OPTIONS.split(choixMultiple.trim()))
            .filter(option -> !option.isBlank())
            .toList();
    }

    private int findBestMatchingOptionIndex(List<String> options, String candidate) {
        if (candidate.isBlank()) {
            return -1;
        }
        for (int index = 0; index < options.size(); index++) {
            if (options.get(index).equals(candidate) || options.get(index).contains(candidate) || candidate.contains(options.get(index))) {
                return index;
            }
        }
        return -1;
    }

    private int findAnswerOptionIndex(List<String> options, String normalizedAnswer) {
        if (normalizedAnswer.isBlank()) {
            return -1;
        }

        if (normalizedAnswer.length() == 1 && Character.isLetter(normalizedAnswer.charAt(0))) {
            int index = Character.toUpperCase(normalizedAnswer.charAt(0)) - 'A';
            return index >= 0 && index < options.size() ? index : -1;
        }

        if (normalizedAnswer.matches("\\d+")) {
            int index = Integer.parseInt(normalizedAnswer) - 1;
            return index >= 0 && index < options.size() ? index : -1;
        }

        return findBestMatchingOptionIndex(options, normalizedAnswer);
    }

    private boolean toBoolean(String value) {
        return value.startsWith("vrai") || value.startsWith("true") || value.startsWith("oui");
    }

    private Set<String> tokenize(String value) {
        if (value.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(value.split("\\s+"))
            .map(String::trim)
            .filter(token -> token.length() > 2)
            .filter(token -> !STOP_WORDS.contains(token))
            .collect(Collectors.toCollection(HashSet::new));
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        normalized = NON_ALPHANUM.matcher(normalized.toLowerCase(Locale.ROOT)).replaceAll(" ");
        return normalized.trim().replaceAll("\\s+", " ");
    }
}
