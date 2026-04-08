package com.mycompany.skilltest.service;

import com.mycompany.skilltest.domain.*;
import com.mycompany.skilltest.domain.enumeration.EvaluationStatus;
import com.mycompany.skilltest.domain.enumeration.QuestionType;
import com.mycompany.skilltest.domain.enumeration.Statut;
import com.mycompany.skilltest.domain.enumeration.TestMode;
import com.mycompany.skilltest.domain.service.ScoringService;
import com.mycompany.skilltest.repository.*;
import com.mycompany.skilltest.service.dto.AnswerCorrectionResultDTO;
import com.mycompany.skilltest.service.dto.DashboardDTO;
import com.mycompany.skilltest.service.dto.DiscretEvaluationSessionDTO;
import com.mycompany.skilltest.service.dto.DiscretQuestionDTO;
import com.mycompany.skilltest.service.dto.EvaluationDTO;
import com.mycompany.skilltest.service.dto.StartDiscretEvaluationDTO;
import com.mycompany.skilltest.service.dto.SubmitAnswerManagerDTO;
import com.mycompany.skilltest.service.dto.UserDTO;
import com.mycompany.skilltest.service.mapper.EvaluationMapper;
import com.mycompany.skilltest.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application Service — Manages discreet evaluation workflow.
 */
@Service
@Transactional
public class DiscretEvaluationService {

    private static final Logger LOG = LoggerFactory.getLogger(DiscretEvaluationService.class);

    private final EvaluationRepository evaluationRepository;
    private final EmployeeRepository employeeRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final ReponseRepository reponseRepository;
    private final ScoreRepository scoreRepository;
    private final ScoringService scoringService;
    private final EvaluationMapper evaluationMapper;
    private final AutomaticAnswerCorrectionService automaticAnswerCorrectionService;
    private final UserService userService;

    public DiscretEvaluationService(
        EvaluationRepository evaluationRepository,
        EmployeeRepository employeeRepository,
        TestRepository testRepository,
        QuestionRepository questionRepository,
        ReponseRepository reponseRepository,
        ScoreRepository scoreRepository,
        ScoringService scoringService,
        EvaluationMapper evaluationMapper,
        AutomaticAnswerCorrectionService automaticAnswerCorrectionService,
        UserService userService
    ) {
        this.evaluationRepository = evaluationRepository;
        this.employeeRepository = employeeRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.reponseRepository = reponseRepository;
        this.scoreRepository = scoreRepository;
        this.scoringService = scoringService;
        this.evaluationMapper = evaluationMapper;
        this.automaticAnswerCorrectionService = automaticAnswerCorrectionService;
        this.userService = userService;
    }

    public DiscretEvaluationSessionDTO startEvaluationDiscret(StartDiscretEvaluationDTO dto) {
        LOG.debug("Starting discreet evaluation for employee: {}", dto.getEmployeeId());

        Employee employee = employeeRepository
            .findById(dto.getEmployeeId())
            .orElseThrow(() -> new BadRequestAlertException("Employee not found", "evaluation", "employeenotfound"));

        Test test = testRepository
            .findById(dto.getTestId())
            .orElseThrow(() -> new BadRequestAlertException("Test not found", "evaluation", "testnotfound"));

        Employee manager = resolveManager(dto);

        if (employee.getId().equals(manager.getId())) {
            throw new BadRequestAlertException(
                "The evaluator must be different from the employee being evaluated",
                "evaluation",
                "invalidmanager"
            );
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setDateEvaluation(Instant.now());
        evaluation.setStatus(EvaluationStatus.EN_COURS);
        evaluation.setMode(TestMode.DISCRET);
        evaluation.setEmployee(employee);
        evaluation.setTest(test);
        evaluation.setManager(manager);

        evaluation = evaluationRepository.save(evaluation);
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        DiscretEvaluationSessionDTO session = new DiscretEvaluationSessionDTO();
        session.setEvaluation(evaluationDTO);
        session.setManager(evaluationDTO.getManager());
        session.setQuestions(buildQuestionnaire(test.getId()));
        return session;
    }

    public AnswerCorrectionResultDTO submitAnswerManager(SubmitAnswerManagerDTO dto) {
        LOG.debug("Manager submitting answer for evaluation: {}", dto.getEvaluationId());

        Evaluation evaluation = evaluationRepository
            .findById(dto.getEvaluationId())
            .orElseThrow(() -> new BadRequestAlertException("Evaluation not found", "evaluation", "evaluationnotfound"));

        if (evaluation.getStatus() != EvaluationStatus.EN_COURS) {
            throw new BadRequestAlertException("Evaluation is not in progress", "evaluation", "evaluationnotinprogress");
        }

        Question question = questionRepository
            .findById(dto.getQuestionId())
            .orElseThrow(() -> new BadRequestAlertException("Question not found", "evaluation", "questionnotfound"));

        if (
            !evaluation.getTest().getId().equals(question.getTest() != null ? question.getTest().getId() : evaluation.getTest().getId()) &&
            question.getTest() != null
        ) {
            throw new BadRequestAlertException("Question does not belong to the selected test", "evaluation", "invalidquestion");
        }

        AnswerCorrectionResultDTO correction = automaticAnswerCorrectionService.correct(question, dto.getContenu());
        Reponse reponse = reponseRepository
            .findOneByEvaluationIdAndQuestionId(dto.getEvaluationId(), dto.getQuestionId())
            .orElseGet(Reponse::new);
        reponse.setContenu(dto.getContenu());
        reponse.setEstCorrecte(correction.isEstCorrecte());
        reponse.setDateReponse(Instant.now());
        reponse.setCommentaireManager(dto.getCommentaireManager());
        reponse.setQuestion(question);
        reponse.setEvaluation(evaluation);

        reponseRepository.save(reponse);
        return correction;
    }

    public EvaluationDTO finalizeEvaluation(Long evaluationId) {
        LOG.debug("Finalizing evaluation: {}", evaluationId);

        Evaluation evaluation = evaluationRepository
            .findById(evaluationId)
            .orElseThrow(() -> new BadRequestAlertException("Evaluation not found", "evaluation", "evaluationnotfound"));

        if (evaluation.getStatus() != EvaluationStatus.EN_COURS) {
            throw new BadRequestAlertException("Evaluation is not in progress", "evaluation", "evaluationnotinprogress");
        }

        List<Reponse> reponses = reponseRepository.findByEvaluationId(evaluationId);
        evaluation = scoringService.finalizeEvaluation(evaluation, reponses);
        evaluation = evaluationRepository.save(evaluation);

        return evaluationMapper.toDto(evaluation);
    }

    @Transactional(readOnly = true)
    public DashboardDTO getDashboardStats() {
        DashboardDTO dashboard = new DashboardDTO();

        long totalEmployees = employeeRepository.count();
        List<Evaluation> allEvaluations = evaluationRepository.findAll();
        long totalEvaluations = allEvaluations.size();

        long conformes = allEvaluations
            .stream()
            .filter(e -> e.getStatut() == Statut.CONFORME)
            .count();
        long aAmeliorer = allEvaluations
            .stream()
            .filter(e -> e.getStatut() == Statut.A_AMELIORER)
            .count();
        long nonConformes = allEvaluations
            .stream()
            .filter(e -> e.getStatut() == Statut.NON_CONFORME)
            .count();

        double conformitePourcentage = totalEvaluations > 0 ? ((double) conformes / totalEvaluations) * 100 : 0;

        dashboard.setTotalEmployees(totalEmployees);
        dashboard.setTotalEvaluations(totalEvaluations);
        dashboard.setEvaluationsConformes(conformes);
        dashboard.setEvaluationsAAmeliorer(aAmeliorer);
        dashboard.setEvaluationsNonConformes(nonConformes);
        dashboard.setConformitePourcentage(conformitePourcentage);

        return dashboard;
    }

    private Employee resolveManager(StartDiscretEvaluationDTO dto) {
        // Try to find an employee linked to the current user (by email or login)
        Employee manager = userService
            .getUserWithAuthorities()
            .flatMap(user -> findEmployeeForUser(user.getLogin(), user.getEmail()))
            .orElse(null);

        // Fallback to the managerId provided by the frontend (manual selection)
        if (manager == null && dto.getManagerId() != null) {
            manager = findManagerById(dto.getManagerId());
        }

        if (manager == null) {
            throw new BadRequestAlertException("Manager not found", "evaluation", "managernotfound");
        }

        return manager;
    }

    private java.util.Optional<Employee> findEmployeeForUser(String login, String email) {
        if (email != null && !email.isBlank()) {
            java.util.Optional<Employee> found = employeeRepository.findOneByEmailIgnoreCase(email);
            if (found.isPresent()) {
                return found;
            }
        }

        if (login != null && !login.isBlank()) {
            return employeeRepository.findOneByEmailIgnoreCase(login.toLowerCase(Locale.ROOT));
        }

        return java.util.Optional.empty();
    }

    private Employee findManagerById(Long managerId) {
        return employeeRepository
            .findById(managerId)
            .orElseThrow(() -> new BadRequestAlertException("Manager not found", "evaluation", "managernotfound"));
    }

    private List<DiscretQuestionDTO> buildQuestionnaire(Long testId) {
        List<Question> testQuestions = questionRepository.findByTestId(testId);
        List<Question> fallbackQuestions = questionRepository.findAll();
        List<Question> allowedQuestions = selectAllowedQuestions(testQuestions, fallbackQuestions);

        if (allowedQuestions.size() < 7) {
            throw new BadRequestAlertException(
                "A discreet evaluation requires at least 7 questions",
                "evaluation",
                "insufficientquestions"
            );
        }

        return allowedQuestions.stream().limit(7).map(this::toDiscretQuestionDto).toList();
    }

    private List<Question> selectAllowedQuestions(List<Question> testQuestions, List<Question> fallbackQuestions) {
        List<Question> aggregated = new ArrayList<>();
        aggregated.addAll(testQuestions);
        aggregated.addAll(fallbackQuestions);

        Map<Long, Question> uniqueQuestions = aggregated
            .stream()
            .filter(
                question ->
                    question.getType() == QuestionType.QCM ||
                    question.getType() == QuestionType.VRAI_FAUX ||
                    question.getType() == QuestionType.OUVERTE
            )
            .collect(Collectors.toMap(Question::getId, question -> question, (left, right) -> left));

        List<Question> ordered = uniqueQuestions.values().stream().sorted(Comparator.comparing(Question::getId)).toList();
        List<Question> selection = new ArrayList<>();

        selection.addAll(takeByType(ordered, selection, question -> question.getType() == QuestionType.QCM, 2));
        selection.addAll(takeByType(ordered, selection, question -> question.getType() == QuestionType.VRAI_FAUX, 2));
        selection.addAll(takeByType(ordered, selection, question -> question.getType() == QuestionType.OUVERTE, 3));

        if (selection.size() < 7) {
            selection.addAll(takeByType(ordered, selection, question -> true, 7 - selection.size()));
        }

        return selection;
    }

    private List<Question> takeByType(List<Question> source, List<Question> current, Predicate<Question> predicate, int limit) {
        return source
            .stream()
            .filter(predicate)
            .filter(question -> current.stream().noneMatch(selected -> selected.getId().equals(question.getId())))
            .limit(limit)
            .toList();
    }

    private DiscretQuestionDTO toDiscretQuestionDto(Question question) {
        DiscretQuestionDTO dto = new DiscretQuestionDTO();
        dto.setId(question.getId());
        dto.setEnonce(question.getEnonce());
        dto.setType(question.getType());
        dto.setNiveau(question.getNiveau());
        dto.setPoints(question.getPoints());
        dto.setOptions(parseOptions(question.getChoixMultiple()));
        return dto;
    }

    private List<String> parseOptions(String rawOptions) {
        if (rawOptions == null || rawOptions.isBlank()) {
            return List.of();
        }
        return List.of(rawOptions.split("\\s+-\\s+"));
    }
}
