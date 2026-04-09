package com.mycompany.skilltest.service;

import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.domain.Question;
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.repository.CompetenceRepository;
import com.mycompany.skilltest.repository.QuestionRepository;
import com.mycompany.skilltest.repository.TestRepository;
import com.mycompany.skilltest.service.dto.TestBuilderDTO;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TestBuilderService {

    private static final Logger LOG = LoggerFactory.getLogger(TestBuilderService.class);

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final CompetenceRepository competenceRepository;

    public TestBuilderService(
        TestRepository testRepository,
        QuestionRepository questionRepository,
        CompetenceRepository competenceRepository
    ) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.competenceRepository = competenceRepository;
    }

    public TestBuilderDTO save(TestBuilderDTO dto) {
        LOG.debug("REST request to save Test with questions via builder: {}", dto.getTitre());

        Test test;
        if (dto.getId() != null) {
            test = testRepository
                .findOneWithEagerRelationships(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + dto.getId()));
        } else {
            test = new Test();
            test.setDateCreation(Instant.now());
        }

        test.setTitre(dto.getTitre());
        test.setDescription(dto.getDescription());
        test.setMode(dto.getMode());
        test.setDuree(dto.getDuree());
        test.setActif(dto.getActif());

        // Competences
        if (dto.getCompetenceIds() != null && !dto.getCompetenceIds().isEmpty()) {
            Set<Competence> competences = new HashSet<>(competenceRepository.findAllById(dto.getCompetenceIds()));
            test.setCompetenceses(competences);
        } else {
            test.setCompetenceses(new HashSet<>());
        }

        test = testRepository.save(test);

        // Manage questions
        List<Question> existingQuestions = questionRepository.findByTestId(test.getId());
        Set<Long> incomingIds = dto
            .getQuestions()
            .stream()
            .map(TestBuilderDTO.QuestionItem::getId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());

        // Delete questions removed by the user
        for (Question existing : existingQuestions) {
            if (!incomingIds.contains(existing.getId())) {
                questionRepository.delete(existing);
            }
        }

        // Create or update questions
        for (int i = 0; i < dto.getQuestions().size(); i++) {
            TestBuilderDTO.QuestionItem item = dto.getQuestions().get(i);
            Question question;
            if (item.getId() != null) {
                question = questionRepository.findById(item.getId()).orElse(new Question());
            } else {
                question = new Question();
            }
            question.setEnonce(item.getEnonce());
            question.setType(item.getType());
            question.setNiveau(item.getNiveau());
            question.setPoints(item.getPoints());
            question.setChoixMultiple(item.getChoixMultiple());
            question.setReponseAttendue(item.getReponseAttendue());
            question.setTest(test);
            questionRepository.save(question);
        }

        // Return the saved state
        dto.setId(test.getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public TestBuilderDTO load(Long testId) {
        Test test = testRepository
            .findOneWithEagerRelationships(testId)
            .orElseThrow(() -> new IllegalArgumentException("Test not found: " + testId));

        TestBuilderDTO dto = new TestBuilderDTO();
        dto.setId(test.getId());
        dto.setTitre(test.getTitre());
        dto.setDescription(test.getDescription());
        dto.setMode(test.getMode());
        dto.setDuree(test.getDuree());
        dto.setActif(test.getActif());

        if (test.getCompetenceses() != null) {
            dto.setCompetenceIds(test.getCompetenceses().stream().map(Competence::getId).collect(Collectors.toSet()));
        }

        List<Question> questions = questionRepository.findByTestId(testId);
        List<TestBuilderDTO.QuestionItem> items = questions
            .stream()
            .map(q -> {
                TestBuilderDTO.QuestionItem item = new TestBuilderDTO.QuestionItem();
                item.setId(q.getId());
                item.setEnonce(q.getEnonce());
                item.setType(q.getType());
                item.setNiveau(q.getNiveau());
                item.setPoints(q.getPoints());
                item.setChoixMultiple(q.getChoixMultiple());
                item.setReponseAttendue(q.getReponseAttendue());
                return item;
            })
            .collect(Collectors.toList());
        dto.setQuestions(items);

        return dto;
    }
}
