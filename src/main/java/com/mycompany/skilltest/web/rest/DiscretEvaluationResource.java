package com.mycompany.skilltest.web.rest;

import com.mycompany.skilltest.security.AuthoritiesConstants;
import com.mycompany.skilltest.service.DiscretEvaluationService;
import com.mycompany.skilltest.service.dto.AnswerCorrectionResultDTO;
import com.mycompany.skilltest.service.dto.DashboardDTO;
import com.mycompany.skilltest.service.dto.DiscretEvaluationSessionDTO;
import com.mycompany.skilltest.service.dto.EvaluationDTO;
import com.mycompany.skilltest.service.dto.StartDiscretEvaluationDTO;
import com.mycompany.skilltest.service.dto.SubmitAnswerManagerDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for discreet evaluation mode.
 */
@RestController
@RequestMapping("/api/discret-evaluation")
public class DiscretEvaluationResource {

    private static final Logger LOG = LoggerFactory.getLogger(DiscretEvaluationResource.class);

    private final DiscretEvaluationService discretEvaluationService;

    public DiscretEvaluationResource(DiscretEvaluationService discretEvaluationService) {
        this.discretEvaluationService = discretEvaluationService;
    }

    @PostMapping("/start")
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.RH, AuthoritiesConstants.MANAGER })
    public ResponseEntity<DiscretEvaluationSessionDTO> startEvaluationDiscret(@Valid @RequestBody StartDiscretEvaluationDTO dto) {
        LOG.debug("REST request to start discreet evaluation");
        DiscretEvaluationSessionDTO result = discretEvaluationService.startEvaluationDiscret(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/submit-answer")
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.MANAGER })
    public ResponseEntity<AnswerCorrectionResultDTO> submitAnswerManager(@Valid @RequestBody SubmitAnswerManagerDTO dto) {
        LOG.debug("REST request to submit answer by manager");
        AnswerCorrectionResultDTO result = discretEvaluationService.submitAnswerManager(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/finalize/{evaluationId}")
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.RH, AuthoritiesConstants.MANAGER })
    public ResponseEntity<EvaluationDTO> finalizeEvaluation(@PathVariable Long evaluationId) {
        LOG.debug("REST request to finalize evaluation: {}", evaluationId);
        EvaluationDTO result = discretEvaluationService.finalizeEvaluation(evaluationId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/suspend/{evaluationId}")
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.RH, AuthoritiesConstants.MANAGER })
    public ResponseEntity<EvaluationDTO> suspendEvaluation(@PathVariable Long evaluationId) {
        LOG.debug("REST request to suspend evaluation: {}", evaluationId);
        EvaluationDTO result = discretEvaluationService.suspendEvaluation(evaluationId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/resume/{evaluationId}")
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.RH, AuthoritiesConstants.MANAGER })
    public ResponseEntity<DiscretEvaluationSessionDTO> resumeEvaluation(@PathVariable Long evaluationId) {
        LOG.debug("REST request to resume evaluation: {}", evaluationId);
        DiscretEvaluationSessionDTO result = discretEvaluationService.resumeEvaluation(evaluationId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/dashboard")
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.RH, AuthoritiesConstants.MANAGER })
    public ResponseEntity<DashboardDTO> getDashboard() {
        LOG.debug("REST request to get dashboard stats");
        DashboardDTO result = discretEvaluationService.getDashboardStats();
        return ResponseEntity.ok(result);
    }
}
