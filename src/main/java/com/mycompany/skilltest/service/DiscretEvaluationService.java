package com.mycompany.skilltest.service;

import com.mycompany.skilltest.domain.*;
import com.mycompany.skilltest.domain.enumeration.EvaluationStatus;
import com.mycompany.skilltest.domain.enumeration.Statut;
import com.mycompany.skilltest.domain.enumeration.TestMode;
import com.mycompany.skilltest.domain.service.ScoringService;
import com.mycompany.skilltest.repository.*;
import com.mycompany.skilltest.service.dto.DashboardDTO;
import com.mycompany.skilltest.service.dto.EvaluationDTO;
import com.mycompany.skilltest.service.dto.StartDiscretEvaluationDTO;
import com.mycompany.skilltest.service.dto.SubmitAnswerManagerDTO;
import com.mycompany.skilltest.service.mapper.EvaluationMapper;
import com.mycompany.skilltest.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.List;
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

    public DiscretEvaluationService(
        EvaluationRepository evaluationRepository,
        EmployeeRepository employeeRepository,
        TestRepository testRepository,
        QuestionRepository questionRepository,
        ReponseRepository reponseRepository,
        ScoreRepository scoreRepository,
        ScoringService scoringService,
        EvaluationMapper evaluationMapper
    ) {
        this.evaluationRepository = evaluationRepository;
        this.employeeRepository = employeeRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.reponseRepository = reponseRepository;
        this.scoreRepository = scoreRepository;
        this.scoringService = scoringService;
        this.evaluationMapper = evaluationMapper;
    }

    public EvaluationDTO startEvaluationDiscret(StartDiscretEvaluationDTO dto) {
        LOG.debug("Starting discreet evaluation for employee: {}", dto.getEmployeeId());

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
            .orElseThrow(() -> new BadRequestAlertException("Employee not found", "evaluation", "employeenotfound"));

        Test test = testRepository.findById(dto.getTestId())
            .orElseThrow(() -> new BadRequestAlertException("Test not found", "evaluation", "testnotfound"));

        Employee manager = employeeRepository.findById(dto.getManagerId())
            .orElseThrow(() -> new BadRequestAlertException("Manager not found", "evaluation", "managernotfound"));

        Evaluation evaluation = new Evaluation();
        evaluation.setDateEvaluation(Instant.now());
        evaluation.setStatus(EvaluationStatus.EN_COURS);
        evaluation.setMode(TestMode.DISCRET);
        evaluation.setEmployee(employee);
        evaluation.setTest(test);
        evaluation.setManager(manager);

        evaluation = evaluationRepository.save(evaluation);
        return evaluationMapper.toDto(evaluation);
    }

    public void submitAnswerManager(SubmitAnswerManagerDTO dto) {
        LOG.debug("Manager submitting answer for evaluation: {}", dto.getEvaluationId());

        Evaluation evaluation = evaluationRepository.findById(dto.getEvaluationId())
            .orElseThrow(() -> new BadRequestAlertException("Evaluation not found", "evaluation", "evaluationnotfound"));

        if (evaluation.getStatus() != EvaluationStatus.EN_COURS) {
            throw new BadRequestAlertException("Evaluation is not in progress", "evaluation", "evaluationnotinprogress");
        }

        Question question = questionRepository.findById(dto.getQuestionId())
            .orElseThrow(() -> new BadRequestAlertException("Question not found", "evaluation", "questionnotfound"));

        Reponse reponse = new Reponse();
        reponse.setContenu(dto.getContenu());
        reponse.setEstCorrecte(dto.getEstCorrecte());
        reponse.setDateReponse(Instant.now());
        reponse.setCommentaireManager(dto.getCommentaireManager());
        reponse.setQuestion(question);
        reponse.setEvaluation(evaluation);

        reponseRepository.save(reponse);
    }

    public EvaluationDTO finalizeEvaluation(Long evaluationId) {
        LOG.debug("Finalizing evaluation: {}", evaluationId);

        Evaluation evaluation = evaluationRepository.findById(evaluationId)
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

        long conformes = allEvaluations.stream().filter(e -> e.getStatut() == Statut.CONFORME).count();
        long aAmeliorer = allEvaluations.stream().filter(e -> e.getStatut() == Statut.A_AMELIORER).count();
        long nonConformes = allEvaluations.stream().filter(e -> e.getStatut() == Statut.NON_CONFORME).count();

        double conformitePourcentage = totalEvaluations > 0 ? ((double) conformes / totalEvaluations) * 100 : 0;

        dashboard.setTotalEmployees(totalEmployees);
        dashboard.setTotalEvaluations(totalEvaluations);
        dashboard.setEvaluationsConformes(conformes);
        dashboard.setEvaluationsAAmeliorer(aAmeliorer);
        dashboard.setEvaluationsNonConformes(nonConformes);
        dashboard.setConformitePourcentage(conformitePourcentage);

        return dashboard;
    }
}
