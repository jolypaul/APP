package com.mycompany.skilltest.web.rest;

import static com.mycompany.skilltest.domain.QuestionAsserts.*;
import static com.mycompany.skilltest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.skilltest.IntegrationTest;
import com.mycompany.skilltest.domain.Question;
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.domain.enumeration.Level;
import com.mycompany.skilltest.domain.enumeration.QuestionType;
import com.mycompany.skilltest.repository.QuestionRepository;
import com.mycompany.skilltest.service.QuestionService;
import com.mycompany.skilltest.service.dto.QuestionDTO;
import com.mycompany.skilltest.service.mapper.QuestionMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link QuestionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuestionResourceIT {

    private static final String DEFAULT_ENONCE = "AAAAAAAAAA";
    private static final String UPDATED_ENONCE = "BBBBBBBBBB";

    private static final QuestionType DEFAULT_TYPE = QuestionType.QCM;
    private static final QuestionType UPDATED_TYPE = QuestionType.OUVERTE;

    private static final Level DEFAULT_NIVEAU = Level.DEBUTANT;
    private static final Level UPDATED_NIVEAU = Level.INTERMEDIAIRE;

    private static final Integer DEFAULT_POINTS = 1;
    private static final Integer UPDATED_POINTS = 2;
    private static final Integer SMALLER_POINTS = 1 - 1;

    private static final String DEFAULT_CHOIX_MULTIPLE = "AAAAAAAAAA";
    private static final String UPDATED_CHOIX_MULTIPLE = "BBBBBBBBBB";

    private static final String DEFAULT_REPONSE_ATTENDUE = "AAAAAAAAAA";
    private static final String UPDATED_REPONSE_ATTENDUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuestionRepository questionRepository;

    @Mock
    private QuestionRepository questionRepositoryMock;

    @Autowired
    private QuestionMapper questionMapper;

    @Mock
    private QuestionService questionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuestionMockMvc;

    private Question question;

    private Question insertedQuestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Question createEntity() {
        return new Question()
            .enonce(DEFAULT_ENONCE)
            .type(DEFAULT_TYPE)
            .niveau(DEFAULT_NIVEAU)
            .points(DEFAULT_POINTS)
            .choixMultiple(DEFAULT_CHOIX_MULTIPLE)
            .reponseAttendue(DEFAULT_REPONSE_ATTENDUE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Question createUpdatedEntity() {
        return new Question()
            .enonce(UPDATED_ENONCE)
            .type(UPDATED_TYPE)
            .niveau(UPDATED_NIVEAU)
            .points(UPDATED_POINTS)
            .choixMultiple(UPDATED_CHOIX_MULTIPLE)
            .reponseAttendue(UPDATED_REPONSE_ATTENDUE);
    }

    @BeforeEach
    void initTest() {
        question = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedQuestion != null) {
            questionRepository.delete(insertedQuestion);
            insertedQuestion = null;
        }
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void createQuestion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);
        var returnedQuestionDTO = om.readValue(
            restQuestionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuestionDTO.class
        );

        // Validate the Question in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuestion = questionMapper.toEntity(returnedQuestionDTO);
        assertQuestionUpdatableFieldsEquals(returnedQuestion, getPersistedQuestion(returnedQuestion));

        insertedQuestion = returnedQuestion;
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void createQuestionWithExistingId() throws Exception {
        // Create the Question with an existing ID
        question.setId(1L);
        QuestionDTO questionDTO = questionMapper.toDto(question);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        question.setType(null);

        // Create the Question, which fails.
        QuestionDTO questionDTO = questionMapper.toDto(question);

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void checkNiveauIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        question.setNiveau(null);

        // Create the Question, which fails.
        QuestionDTO questionDTO = questionMapper.toDto(question);

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void checkPointsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        question.setPoints(null);

        // Create the Question, which fails.
        QuestionDTO questionDTO = questionMapper.toDto(question);

        restQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestions() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId().intValue())))
            .andExpect(jsonPath("$.[*].enonce").value(hasItem(DEFAULT_ENONCE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].niveau").value(hasItem(DEFAULT_NIVEAU.toString())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)))
            .andExpect(jsonPath("$.[*].choixMultiple").value(hasItem(DEFAULT_CHOIX_MULTIPLE)))
            .andExpect(jsonPath("$.[*].reponseAttendue").value(hasItem(DEFAULT_REPONSE_ATTENDUE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuestionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(questionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(questionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuestionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(questionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(questionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getQuestion() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get the question
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL_ID, question.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(question.getId().intValue()))
            .andExpect(jsonPath("$.enonce").value(DEFAULT_ENONCE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.niveau").value(DEFAULT_NIVEAU.toString()))
            .andExpect(jsonPath("$.points").value(DEFAULT_POINTS))
            .andExpect(jsonPath("$.choixMultiple").value(DEFAULT_CHOIX_MULTIPLE))
            .andExpect(jsonPath("$.reponseAttendue").value(DEFAULT_REPONSE_ATTENDUE));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getQuestionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        Long id = question.getId();

        defaultQuestionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultQuestionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultQuestionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where type equals to
        defaultQuestionFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where type in
        defaultQuestionFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where type is not null
        defaultQuestionFiltering("type.specified=true", "type.specified=false");
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByNiveauIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where niveau equals to
        defaultQuestionFiltering("niveau.equals=" + DEFAULT_NIVEAU, "niveau.equals=" + UPDATED_NIVEAU);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByNiveauIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where niveau in
        defaultQuestionFiltering("niveau.in=" + DEFAULT_NIVEAU + "," + UPDATED_NIVEAU, "niveau.in=" + UPDATED_NIVEAU);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByNiveauIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where niveau is not null
        defaultQuestionFiltering("niveau.specified=true", "niveau.specified=false");
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where points equals to
        defaultQuestionFiltering("points.equals=" + DEFAULT_POINTS, "points.equals=" + UPDATED_POINTS);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByPointsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where points in
        defaultQuestionFiltering("points.in=" + DEFAULT_POINTS + "," + UPDATED_POINTS, "points.in=" + UPDATED_POINTS);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where points is not null
        defaultQuestionFiltering("points.specified=true", "points.specified=false");
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where points is greater than or equal to
        defaultQuestionFiltering("points.greaterThanOrEqual=" + DEFAULT_POINTS, "points.greaterThanOrEqual=" + UPDATED_POINTS);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByPointsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where points is less than or equal to
        defaultQuestionFiltering("points.lessThanOrEqual=" + DEFAULT_POINTS, "points.lessThanOrEqual=" + SMALLER_POINTS);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where points is less than
        defaultQuestionFiltering("points.lessThan=" + UPDATED_POINTS, "points.lessThan=" + DEFAULT_POINTS);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByPointsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        // Get all the questionList where points is greater than
        defaultQuestionFiltering("points.greaterThan=" + SMALLER_POINTS, "points.greaterThan=" + DEFAULT_POINTS);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getAllQuestionsByTestIsEqualToSomething() throws Exception {
        Test test;
        if (TestUtil.findAll(em, Test.class).isEmpty()) {
            questionRepository.saveAndFlush(question);
            test = TestResourceIT.createEntity();
        } else {
            test = TestUtil.findAll(em, Test.class).get(0);
        }
        em.persist(test);
        em.flush();
        question.setTest(test);
        questionRepository.saveAndFlush(question);
        Long testId = test.getId();
        // Get all the questionList where test equals to testId
        defaultQuestionShouldBeFound("testId.equals=" + testId);

        // Get all the questionList where test equals to (testId + 1)
        defaultQuestionShouldNotBeFound("testId.equals=" + (testId + 1));
    }

    private void defaultQuestionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultQuestionShouldBeFound(shouldBeFound);
        defaultQuestionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQuestionShouldBeFound(String filter) throws Exception {
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId().intValue())))
            .andExpect(jsonPath("$.[*].enonce").value(hasItem(DEFAULT_ENONCE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].niveau").value(hasItem(DEFAULT_NIVEAU.toString())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)))
            .andExpect(jsonPath("$.[*].choixMultiple").value(hasItem(DEFAULT_CHOIX_MULTIPLE)))
            .andExpect(jsonPath("$.[*].reponseAttendue").value(hasItem(DEFAULT_REPONSE_ATTENDUE)));

        // Check, that the count call also returns 1
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQuestionShouldNotBeFound(String filter) throws Exception {
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQuestionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void getNonExistingQuestion() throws Exception {
        // Get the question
        restQuestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void putExistingQuestion() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the question
        Question updatedQuestion = questionRepository.findById(question.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuestion are not directly saved in db
        em.detach(updatedQuestion);
        updatedQuestion
            .enonce(UPDATED_ENONCE)
            .type(UPDATED_TYPE)
            .niveau(UPDATED_NIVEAU)
            .points(UPDATED_POINTS)
            .choixMultiple(UPDATED_CHOIX_MULTIPLE)
            .reponseAttendue(UPDATED_REPONSE_ATTENDUE);
        QuestionDTO questionDTO = questionMapper.toDto(updatedQuestion);

        restQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuestionToMatchAllProperties(updatedQuestion);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void putNonExistingQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void putWithIdMismatchQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void putWithMissingIdPathParamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void partialUpdateQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the question using partial update
        Question partialUpdatedQuestion = new Question();
        partialUpdatedQuestion.setId(question.getId());

        partialUpdatedQuestion.niveau(UPDATED_NIVEAU).reponseAttendue(UPDATED_REPONSE_ATTENDUE);

        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestion))
            )
            .andExpect(status().isOk());

        // Validate the Question in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedQuestion, question), getPersistedQuestion(question));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void fullUpdateQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the question using partial update
        Question partialUpdatedQuestion = new Question();
        partialUpdatedQuestion.setId(question.getId());

        partialUpdatedQuestion
            .enonce(UPDATED_ENONCE)
            .type(UPDATED_TYPE)
            .niveau(UPDATED_NIVEAU)
            .points(UPDATED_POINTS)
            .choixMultiple(UPDATED_CHOIX_MULTIPLE)
            .reponseAttendue(UPDATED_REPONSE_ATTENDUE);

        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestion))
            )
            .andExpect(status().isOk());

        // Validate the Question in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionUpdatableFieldsEquals(partialUpdatedQuestion, getPersistedQuestion(partialUpdatedQuestion));
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void patchNonExistingQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, questionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void patchWithIdMismatchQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void patchWithMissingIdPathParamQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        question.setId(longCount.incrementAndGet());

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(questionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Question in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @org.junit.jupiter.api.Test
    @Transactional
    void deleteQuestion() throws Exception {
        // Initialize the database
        insertedQuestion = questionRepository.saveAndFlush(question);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the question
        restQuestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, question.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return questionRepository.count();
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

    protected Question getPersistedQuestion(Question question) {
        return questionRepository.findById(question.getId()).orElseThrow();
    }

    protected void assertPersistedQuestionToMatchAllProperties(Question expectedQuestion) {
        assertQuestionAllPropertiesEquals(expectedQuestion, getPersistedQuestion(expectedQuestion));
    }

    protected void assertPersistedQuestionToMatchUpdatableProperties(Question expectedQuestion) {
        assertQuestionAllUpdatablePropertiesEquals(expectedQuestion, getPersistedQuestion(expectedQuestion));
    }
}
