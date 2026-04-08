package com.mycompany.skilltest.web.rest;

import static com.mycompany.skilltest.domain.EvaluationAsserts.*;
import static com.mycompany.skilltest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.skilltest.IntegrationTest;
import com.mycompany.skilltest.domain.Employee;
import com.mycompany.skilltest.domain.Evaluation;
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.domain.enumeration.EvaluationStatus;
import com.mycompany.skilltest.domain.enumeration.Statut;
import com.mycompany.skilltest.domain.enumeration.TestMode;
import com.mycompany.skilltest.repository.EvaluationRepository;
import com.mycompany.skilltest.service.EvaluationService;
import com.mycompany.skilltest.service.dto.EvaluationDTO;
import com.mycompany.skilltest.service.mapper.EvaluationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EvaluationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EvaluationResourceIT {

    private static final Instant DEFAULT_DATE_EVALUATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_EVALUATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final EvaluationStatus DEFAULT_STATUS = EvaluationStatus.EN_COURS;
    private static final EvaluationStatus UPDATED_STATUS = EvaluationStatus.TERMINEE;

    private static final TestMode DEFAULT_MODE = TestMode.CLASSIQUE;
    private static final TestMode UPDATED_MODE = TestMode.DISCRET;

    private static final Double DEFAULT_SCORE_TOTAL = 1D;
    private static final Double UPDATED_SCORE_TOTAL = 2D;
    private static final Double SMALLER_SCORE_TOTAL = 1D - 1D;

    private static final String DEFAULT_REMARQUES = "AAAAAAAAAA";
    private static final String UPDATED_REMARQUES = "BBBBBBBBBB";

    private static final Statut DEFAULT_STATUT = Statut.CONFORME;
    private static final Statut UPDATED_STATUT = Statut.A_AMELIORER;

    private static final String ENTITY_API_URL = "/api/evaluations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Mock
    private EvaluationRepository evaluationRepositoryMock;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Mock
    private EvaluationService evaluationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEvaluationMockMvc;

    private Evaluation evaluation;

    private Evaluation insertedEvaluation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evaluation createEntity() {
        return new Evaluation()
            .dateEvaluation(DEFAULT_DATE_EVALUATION)
            .status(DEFAULT_STATUS)
            .mode(DEFAULT_MODE)
            .scoreTotal(DEFAULT_SCORE_TOTAL)
            .remarques(DEFAULT_REMARQUES)
            .statut(DEFAULT_STATUT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evaluation createUpdatedEntity() {
        return new Evaluation()
            .dateEvaluation(UPDATED_DATE_EVALUATION)
            .status(UPDATED_STATUS)
            .mode(UPDATED_MODE)
            .scoreTotal(UPDATED_SCORE_TOTAL)
            .remarques(UPDATED_REMARQUES)
            .statut(UPDATED_STATUT);
    }

    @BeforeEach
    void initTest() {
        evaluation = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEvaluation != null) {
            evaluationRepository.delete(insertedEvaluation);
            insertedEvaluation = null;
        }
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void createEvaluation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);
        var returnedEvaluationDTO = om.readValue(
            restEvaluationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EvaluationDTO.class
        );

        // Validate the Evaluation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEvaluation = evaluationMapper.toEntity(returnedEvaluationDTO);
        assertEvaluationUpdatableFieldsEquals(returnedEvaluation, getPersistedEvaluation(returnedEvaluation));

        insertedEvaluation = returnedEvaluation;
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void createEvaluationWithExistingId() throws Exception {
        // Create the Evaluation with an existing ID
        evaluation.setId(1L);
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEvaluationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void checkDateEvaluationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evaluation.setDateEvaluation(null);

        // Create the Evaluation, which fails.
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        restEvaluationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evaluation.setStatus(null);

        // Create the Evaluation, which fails.
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        restEvaluationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void checkModeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evaluation.setMode(null);

        // Create the Evaluation, which fails.
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        restEvaluationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluations() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList
        restEvaluationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evaluation.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateEvaluation").value(hasItem(DEFAULT_DATE_EVALUATION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE.toString())))
            .andExpect(jsonPath("$.[*].scoreTotal").value(hasItem(DEFAULT_SCORE_TOTAL)))
            .andExpect(jsonPath("$.[*].remarques").value(hasItem(DEFAULT_REMARQUES)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEvaluationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(evaluationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEvaluationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(evaluationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEvaluationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(evaluationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEvaluationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(evaluationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getEvaluation() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get the evaluation
        restEvaluationMockMvc
            .perform(get(ENTITY_API_URL_ID, evaluation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(evaluation.getId().intValue()))
            .andExpect(jsonPath("$.dateEvaluation").value(DEFAULT_DATE_EVALUATION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.mode").value(DEFAULT_MODE.toString()))
            .andExpect(jsonPath("$.scoreTotal").value(DEFAULT_SCORE_TOTAL))
            .andExpect(jsonPath("$.remarques").value(DEFAULT_REMARQUES))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getEvaluationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        Long id = evaluation.getId();

        defaultEvaluationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEvaluationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEvaluationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByDateEvaluationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where dateEvaluation equals to
        defaultEvaluationFiltering("dateEvaluation.equals=" + DEFAULT_DATE_EVALUATION, "dateEvaluation.equals=" + UPDATED_DATE_EVALUATION);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByDateEvaluationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where dateEvaluation in
        defaultEvaluationFiltering(
            "dateEvaluation.in=" + DEFAULT_DATE_EVALUATION + "," + UPDATED_DATE_EVALUATION,
            "dateEvaluation.in=" + UPDATED_DATE_EVALUATION
        );
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByDateEvaluationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where dateEvaluation is not null
        defaultEvaluationFiltering("dateEvaluation.specified=true", "dateEvaluation.specified=false");
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where status equals to
        defaultEvaluationFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where status in
        defaultEvaluationFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where status is not null
        defaultEvaluationFiltering("status.specified=true", "status.specified=false");
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByModeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where mode equals to
        defaultEvaluationFiltering("mode.equals=" + DEFAULT_MODE, "mode.equals=" + UPDATED_MODE);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByModeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where mode in
        defaultEvaluationFiltering("mode.in=" + DEFAULT_MODE + "," + UPDATED_MODE, "mode.in=" + UPDATED_MODE);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where mode is not null
        defaultEvaluationFiltering("mode.specified=true", "mode.specified=false");
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByScoreTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where scoreTotal equals to
        defaultEvaluationFiltering("scoreTotal.equals=" + DEFAULT_SCORE_TOTAL, "scoreTotal.equals=" + UPDATED_SCORE_TOTAL);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByScoreTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where scoreTotal in
        defaultEvaluationFiltering(
            "scoreTotal.in=" + DEFAULT_SCORE_TOTAL + "," + UPDATED_SCORE_TOTAL,
            "scoreTotal.in=" + UPDATED_SCORE_TOTAL
        );
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByScoreTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where scoreTotal is not null
        defaultEvaluationFiltering("scoreTotal.specified=true", "scoreTotal.specified=false");
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByScoreTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where scoreTotal is greater than or equal to
        defaultEvaluationFiltering(
            "scoreTotal.greaterThanOrEqual=" + DEFAULT_SCORE_TOTAL,
            "scoreTotal.greaterThanOrEqual=" + UPDATED_SCORE_TOTAL
        );
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByScoreTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where scoreTotal is less than or equal to
        defaultEvaluationFiltering(
            "scoreTotal.lessThanOrEqual=" + DEFAULT_SCORE_TOTAL,
            "scoreTotal.lessThanOrEqual=" + SMALLER_SCORE_TOTAL
        );
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByScoreTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where scoreTotal is less than
        defaultEvaluationFiltering("scoreTotal.lessThan=" + UPDATED_SCORE_TOTAL, "scoreTotal.lessThan=" + DEFAULT_SCORE_TOTAL);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByScoreTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where scoreTotal is greater than
        defaultEvaluationFiltering("scoreTotal.greaterThan=" + SMALLER_SCORE_TOTAL, "scoreTotal.greaterThan=" + DEFAULT_SCORE_TOTAL);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where statut equals to
        defaultEvaluationFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where statut in
        defaultEvaluationFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList where statut is not null
        defaultEvaluationFiltering("statut.specified=true", "statut.specified=false");
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByEmployeeIsEqualToSomething() throws Exception {
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            evaluationRepository.saveAndFlush(evaluation);
            employee = EmployeeResourceIT.createEntity();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(employee);
        em.flush();
        evaluation.setEmployee(employee);
        evaluationRepository.saveAndFlush(evaluation);
        Long employeeId = employee.getId();
        // Get all the evaluationList where employee equals to employeeId
        defaultEvaluationShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the evaluationList where employee equals to (employeeId + 1)
        defaultEvaluationShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByTestIsEqualToSomething() throws Exception {
        Test test;
        if (TestUtil.findAll(em, Test.class).isEmpty()) {
            evaluationRepository.saveAndFlush(evaluation);
            test = TestResourceIT.createEntity();
        } else {
            test = TestUtil.findAll(em, Test.class).get(0);
        }
        em.persist(test);
        em.flush();
        evaluation.setTest(test);
        evaluationRepository.saveAndFlush(evaluation);
        Long testId = test.getId();
        // Get all the evaluationList where test equals to testId
        defaultEvaluationShouldBeFound("testId.equals=" + testId);

        // Get all the evaluationList where test equals to (testId + 1)
        defaultEvaluationShouldNotBeFound("testId.equals=" + (testId + 1));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllEvaluationsByManagerIsEqualToSomething() throws Exception {
        Employee manager;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            evaluationRepository.saveAndFlush(evaluation);
            manager = EmployeeResourceIT.createEntity();
        } else {
            manager = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(manager);
        em.flush();
        evaluation.setManager(manager);
        evaluationRepository.saveAndFlush(evaluation);
        Long managerId = manager.getId();
        // Get all the evaluationList where manager equals to managerId
        defaultEvaluationShouldBeFound("managerId.equals=" + managerId);

        // Get all the evaluationList where manager equals to (managerId + 1)
        defaultEvaluationShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }

    private void defaultEvaluationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEvaluationShouldBeFound(shouldBeFound);
        defaultEvaluationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEvaluationShouldBeFound(String filter) throws Exception {
        restEvaluationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evaluation.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateEvaluation").value(hasItem(DEFAULT_DATE_EVALUATION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE.toString())))
            .andExpect(jsonPath("$.[*].scoreTotal").value(hasItem(DEFAULT_SCORE_TOTAL)))
            .andExpect(jsonPath("$.[*].remarques").value(hasItem(DEFAULT_REMARQUES)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));

        // Check, that the count call also returns 1
        restEvaluationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEvaluationShouldNotBeFound(String filter) throws Exception {
        restEvaluationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEvaluationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getNonExistingEvaluation() throws Exception {
        // Get the evaluation
        restEvaluationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void putExistingEvaluation() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evaluation
        Evaluation updatedEvaluation = evaluationRepository.findById(evaluation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEvaluation are not directly saved in db
        em.detach(updatedEvaluation);
        updatedEvaluation
            .dateEvaluation(UPDATED_DATE_EVALUATION)
            .status(UPDATED_STATUS)
            .mode(UPDATED_MODE)
            .scoreTotal(UPDATED_SCORE_TOTAL)
            .remarques(UPDATED_REMARQUES)
            .statut(UPDATED_STATUT);
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(updatedEvaluation);

        restEvaluationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, evaluationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEvaluationToMatchAllProperties(updatedEvaluation);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void putNonExistingEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, evaluationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void putWithIdMismatchEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void putWithMissingIdPathParamEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void partialUpdateEvaluationWithPatch() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evaluation using partial update
        Evaluation partialUpdatedEvaluation = new Evaluation();
        partialUpdatedEvaluation.setId(evaluation.getId());

        partialUpdatedEvaluation
            .dateEvaluation(UPDATED_DATE_EVALUATION)
            .mode(UPDATED_MODE)
            .scoreTotal(UPDATED_SCORE_TOTAL)
            .remarques(UPDATED_REMARQUES);

        restEvaluationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvaluation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvaluation))
            )
            .andExpect(status().isOk());

        // Validate the Evaluation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEvaluationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEvaluation, evaluation),
            getPersistedEvaluation(evaluation)
        );
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void fullUpdateEvaluationWithPatch() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evaluation using partial update
        Evaluation partialUpdatedEvaluation = new Evaluation();
        partialUpdatedEvaluation.setId(evaluation.getId());

        partialUpdatedEvaluation
            .dateEvaluation(UPDATED_DATE_EVALUATION)
            .status(UPDATED_STATUS)
            .mode(UPDATED_MODE)
            .scoreTotal(UPDATED_SCORE_TOTAL)
            .remarques(UPDATED_REMARQUES)
            .statut(UPDATED_STATUT);

        restEvaluationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvaluation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvaluation))
            )
            .andExpect(status().isOk());

        // Validate the Evaluation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEvaluationUpdatableFieldsEquals(partialUpdatedEvaluation, getPersistedEvaluation(partialUpdatedEvaluation));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void patchNonExistingEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, evaluationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void patchWithIdMismatchEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void patchWithMissingIdPathParamEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(evaluationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void deleteEvaluation() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the evaluation
        restEvaluationMockMvc
            .perform(delete(ENTITY_API_URL_ID, evaluation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return evaluationRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Evaluation getPersistedEvaluation(Evaluation evaluation) {
        return evaluationRepository.findById(evaluation.getId()).orElseThrow();
    }

    protected void assertPersistedEvaluationToMatchAllProperties(Evaluation expectedEvaluation) {
        assertEvaluationAllPropertiesEquals(expectedEvaluation, getPersistedEvaluation(expectedEvaluation));
    }

    protected void assertPersistedEvaluationToMatchUpdatableProperties(Evaluation expectedEvaluation) {
        assertEvaluationAllUpdatablePropertiesEquals(expectedEvaluation, getPersistedEvaluation(expectedEvaluation));
    }
}
