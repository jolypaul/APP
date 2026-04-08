package com.mycompany.skilltest.web.rest;

import static com.mycompany.skilltest.domain.ScoreAsserts.*;
import static com.mycompany.skilltest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.skilltest.IntegrationTest;
import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.domain.Evaluation;
import com.mycompany.skilltest.domain.Score;
import com.mycompany.skilltest.domain.enumeration.Statut;
import com.mycompany.skilltest.repository.ScoreRepository;
import com.mycompany.skilltest.service.ScoreService;
import com.mycompany.skilltest.service.dto.ScoreDTO;
import com.mycompany.skilltest.service.mapper.ScoreMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
 * Integration tests for the {@link ScoreResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ScoreResourceIT {

    private static final Double DEFAULT_VALEUR = 1D;
    private static final Double UPDATED_VALEUR = 2D;
    private static final Double SMALLER_VALEUR = 1D - 1D;

    private static final Double DEFAULT_POURCENTAGE = 1D;
    private static final Double UPDATED_POURCENTAGE = 2D;
    private static final Double SMALLER_POURCENTAGE = 1D - 1D;

    private static final Statut DEFAULT_STATUT = Statut.CONFORME;
    private static final Statut UPDATED_STATUT = Statut.A_AMELIORER;

    private static final Instant DEFAULT_DATE_CALCUL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CALCUL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/scores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScoreRepository scoreRepository;

    @Mock
    private ScoreRepository scoreRepositoryMock;

    @Autowired
    private ScoreMapper scoreMapper;

    @Mock
    private ScoreService scoreServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScoreMockMvc;

    private Score score;

    private Score insertedScore;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Score createEntity() {
        return new Score().valeur(DEFAULT_VALEUR).pourcentage(DEFAULT_POURCENTAGE).statut(DEFAULT_STATUT).dateCalcul(DEFAULT_DATE_CALCUL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Score createUpdatedEntity() {
        return new Score().valeur(UPDATED_VALEUR).pourcentage(UPDATED_POURCENTAGE).statut(UPDATED_STATUT).dateCalcul(UPDATED_DATE_CALCUL);
    }

    @BeforeEach
    void initTest() {
        score = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedScore != null) {
            scoreRepository.delete(insertedScore);
            insertedScore = null;
        }
    }

    @Test
    @Transactional
    void createScore() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Score
        ScoreDTO scoreDTO = scoreMapper.toDto(score);
        var returnedScoreDTO = om.readValue(
            restScoreMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scoreDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScoreDTO.class
        );

        // Validate the Score in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedScore = scoreMapper.toEntity(returnedScoreDTO);
        assertScoreUpdatableFieldsEquals(returnedScore, getPersistedScore(returnedScore));

        insertedScore = returnedScore;
    }

    @Test
    @Transactional
    void createScoreWithExistingId() throws Exception {
        // Create the Score with an existing ID
        score.setId(1L);
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scoreDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Score in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkValeurIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        score.setValeur(null);

        // Create the Score, which fails.
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        restScoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scoreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPourcentageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        score.setPourcentage(null);

        // Create the Score, which fails.
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        restScoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scoreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        score.setStatut(null);

        // Create the Score, which fails.
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        restScoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scoreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCalculIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        score.setDateCalcul(null);

        // Create the Score, which fails.
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        restScoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scoreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScores() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList
        restScoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(score.getId().intValue())))
            .andExpect(jsonPath("$.[*].valeur").value(hasItem(DEFAULT_VALEUR)))
            .andExpect(jsonPath("$.[*].pourcentage").value(hasItem(DEFAULT_POURCENTAGE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCalcul").value(hasItem(DEFAULT_DATE_CALCUL.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScoresWithEagerRelationshipsIsEnabled() throws Exception {
        when(scoreServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScoreMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(scoreServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScoresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(scoreServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScoreMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(scoreRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getScore() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get the score
        restScoreMockMvc
            .perform(get(ENTITY_API_URL_ID, score.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(score.getId().intValue()))
            .andExpect(jsonPath("$.valeur").value(DEFAULT_VALEUR))
            .andExpect(jsonPath("$.pourcentage").value(DEFAULT_POURCENTAGE))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.dateCalcul").value(DEFAULT_DATE_CALCUL.toString()));
    }

    @Test
    @Transactional
    void getScoresByIdFiltering() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        Long id = score.getId();

        defaultScoreFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultScoreFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultScoreFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllScoresByValeurIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where valeur equals to
        defaultScoreFiltering("valeur.equals=" + DEFAULT_VALEUR, "valeur.equals=" + UPDATED_VALEUR);
    }

    @Test
    @Transactional
    void getAllScoresByValeurIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where valeur in
        defaultScoreFiltering("valeur.in=" + DEFAULT_VALEUR + "," + UPDATED_VALEUR, "valeur.in=" + UPDATED_VALEUR);
    }

    @Test
    @Transactional
    void getAllScoresByValeurIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where valeur is not null
        defaultScoreFiltering("valeur.specified=true", "valeur.specified=false");
    }

    @Test
    @Transactional
    void getAllScoresByValeurIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where valeur is greater than or equal to
        defaultScoreFiltering("valeur.greaterThanOrEqual=" + DEFAULT_VALEUR, "valeur.greaterThanOrEqual=" + UPDATED_VALEUR);
    }

    @Test
    @Transactional
    void getAllScoresByValeurIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where valeur is less than or equal to
        defaultScoreFiltering("valeur.lessThanOrEqual=" + DEFAULT_VALEUR, "valeur.lessThanOrEqual=" + SMALLER_VALEUR);
    }

    @Test
    @Transactional
    void getAllScoresByValeurIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where valeur is less than
        defaultScoreFiltering("valeur.lessThan=" + UPDATED_VALEUR, "valeur.lessThan=" + DEFAULT_VALEUR);
    }

    @Test
    @Transactional
    void getAllScoresByValeurIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where valeur is greater than
        defaultScoreFiltering("valeur.greaterThan=" + SMALLER_VALEUR, "valeur.greaterThan=" + DEFAULT_VALEUR);
    }

    @Test
    @Transactional
    void getAllScoresByPourcentageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where pourcentage equals to
        defaultScoreFiltering("pourcentage.equals=" + DEFAULT_POURCENTAGE, "pourcentage.equals=" + UPDATED_POURCENTAGE);
    }

    @Test
    @Transactional
    void getAllScoresByPourcentageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where pourcentage in
        defaultScoreFiltering("pourcentage.in=" + DEFAULT_POURCENTAGE + "," + UPDATED_POURCENTAGE, "pourcentage.in=" + UPDATED_POURCENTAGE);
    }

    @Test
    @Transactional
    void getAllScoresByPourcentageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where pourcentage is not null
        defaultScoreFiltering("pourcentage.specified=true", "pourcentage.specified=false");
    }

    @Test
    @Transactional
    void getAllScoresByPourcentageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where pourcentage is greater than or equal to
        defaultScoreFiltering(
            "pourcentage.greaterThanOrEqual=" + DEFAULT_POURCENTAGE,
            "pourcentage.greaterThanOrEqual=" + UPDATED_POURCENTAGE
        );
    }

    @Test
    @Transactional
    void getAllScoresByPourcentageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where pourcentage is less than or equal to
        defaultScoreFiltering("pourcentage.lessThanOrEqual=" + DEFAULT_POURCENTAGE, "pourcentage.lessThanOrEqual=" + SMALLER_POURCENTAGE);
    }

    @Test
    @Transactional
    void getAllScoresByPourcentageIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where pourcentage is less than
        defaultScoreFiltering("pourcentage.lessThan=" + UPDATED_POURCENTAGE, "pourcentage.lessThan=" + DEFAULT_POURCENTAGE);
    }

    @Test
    @Transactional
    void getAllScoresByPourcentageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where pourcentage is greater than
        defaultScoreFiltering("pourcentage.greaterThan=" + SMALLER_POURCENTAGE, "pourcentage.greaterThan=" + DEFAULT_POURCENTAGE);
    }

    @Test
    @Transactional
    void getAllScoresByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where statut equals to
        defaultScoreFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllScoresByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where statut in
        defaultScoreFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllScoresByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where statut is not null
        defaultScoreFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllScoresByDateCalculIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where dateCalcul equals to
        defaultScoreFiltering("dateCalcul.equals=" + DEFAULT_DATE_CALCUL, "dateCalcul.equals=" + UPDATED_DATE_CALCUL);
    }

    @Test
    @Transactional
    void getAllScoresByDateCalculIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where dateCalcul in
        defaultScoreFiltering("dateCalcul.in=" + DEFAULT_DATE_CALCUL + "," + UPDATED_DATE_CALCUL, "dateCalcul.in=" + UPDATED_DATE_CALCUL);
    }

    @Test
    @Transactional
    void getAllScoresByDateCalculIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        // Get all the scoreList where dateCalcul is not null
        defaultScoreFiltering("dateCalcul.specified=true", "dateCalcul.specified=false");
    }

    @Test
    @Transactional
    void getAllScoresByEvaluationIsEqualToSomething() throws Exception {
        Evaluation evaluation;
        if (TestUtil.findAll(em, Evaluation.class).isEmpty()) {
            scoreRepository.saveAndFlush(score);
            evaluation = EvaluationResourceIT.createEntity();
        } else {
            evaluation = TestUtil.findAll(em, Evaluation.class).get(0);
        }
        em.persist(evaluation);
        em.flush();
        score.setEvaluation(evaluation);
        scoreRepository.saveAndFlush(score);
        Long evaluationId = evaluation.getId();
        // Get all the scoreList where evaluation equals to evaluationId
        defaultScoreShouldBeFound("evaluationId.equals=" + evaluationId);

        // Get all the scoreList where evaluation equals to (evaluationId + 1)
        defaultScoreShouldNotBeFound("evaluationId.equals=" + (evaluationId + 1));
    }

    @Test
    @Transactional
    void getAllScoresByCompetenceIsEqualToSomething() throws Exception {
        Competence competence;
        if (TestUtil.findAll(em, Competence.class).isEmpty()) {
            scoreRepository.saveAndFlush(score);
            competence = CompetenceResourceIT.createEntity();
        } else {
            competence = TestUtil.findAll(em, Competence.class).get(0);
        }
        em.persist(competence);
        em.flush();
        score.setCompetence(competence);
        scoreRepository.saveAndFlush(score);
        Long competenceId = competence.getId();
        // Get all the scoreList where competence equals to competenceId
        defaultScoreShouldBeFound("competenceId.equals=" + competenceId);

        // Get all the scoreList where competence equals to (competenceId + 1)
        defaultScoreShouldNotBeFound("competenceId.equals=" + (competenceId + 1));
    }

    private void defaultScoreFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultScoreShouldBeFound(shouldBeFound);
        defaultScoreShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScoreShouldBeFound(String filter) throws Exception {
        restScoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(score.getId().intValue())))
            .andExpect(jsonPath("$.[*].valeur").value(hasItem(DEFAULT_VALEUR)))
            .andExpect(jsonPath("$.[*].pourcentage").value(hasItem(DEFAULT_POURCENTAGE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCalcul").value(hasItem(DEFAULT_DATE_CALCUL.toString())));

        // Check, that the count call also returns 1
        restScoreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScoreShouldNotBeFound(String filter) throws Exception {
        restScoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScoreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingScore() throws Exception {
        // Get the score
        restScoreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScore() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the score
        Score updatedScore = scoreRepository.findById(score.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScore are not directly saved in db
        em.detach(updatedScore);
        updatedScore.valeur(UPDATED_VALEUR).pourcentage(UPDATED_POURCENTAGE).statut(UPDATED_STATUT).dateCalcul(UPDATED_DATE_CALCUL);
        ScoreDTO scoreDTO = scoreMapper.toDto(updatedScore);

        restScoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scoreDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scoreDTO))
            )
            .andExpect(status().isOk());

        // Validate the Score in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScoreToMatchAllProperties(updatedScore);
    }

    @Test
    @Transactional
    void putNonExistingScore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        score.setId(longCount.incrementAndGet());

        // Create the Score
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scoreDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scoreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Score in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        score.setId(longCount.incrementAndGet());

        // Create the Score
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scoreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Score in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        score.setId(longCount.incrementAndGet());

        // Create the Score
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScoreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scoreDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Score in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScoreWithPatch() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the score using partial update
        Score partialUpdatedScore = new Score();
        partialUpdatedScore.setId(score.getId());

        restScoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScore.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScore))
            )
            .andExpect(status().isOk());

        // Validate the Score in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScoreUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedScore, score), getPersistedScore(score));
    }

    @Test
    @Transactional
    void fullUpdateScoreWithPatch() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the score using partial update
        Score partialUpdatedScore = new Score();
        partialUpdatedScore.setId(score.getId());

        partialUpdatedScore.valeur(UPDATED_VALEUR).pourcentage(UPDATED_POURCENTAGE).statut(UPDATED_STATUT).dateCalcul(UPDATED_DATE_CALCUL);

        restScoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScore.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScore))
            )
            .andExpect(status().isOk());

        // Validate the Score in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScoreUpdatableFieldsEquals(partialUpdatedScore, getPersistedScore(partialUpdatedScore));
    }

    @Test
    @Transactional
    void patchNonExistingScore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        score.setId(longCount.incrementAndGet());

        // Create the Score
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scoreDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scoreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Score in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        score.setId(longCount.incrementAndGet());

        // Create the Score
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scoreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Score in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScore() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        score.setId(longCount.incrementAndGet());

        // Create the Score
        ScoreDTO scoreDTO = scoreMapper.toDto(score);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScoreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(scoreDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Score in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScore() throws Exception {
        // Initialize the database
        insertedScore = scoreRepository.saveAndFlush(score);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the score
        restScoreMockMvc
            .perform(delete(ENTITY_API_URL_ID, score.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scoreRepository.count();
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

    protected Score getPersistedScore(Score score) {
        return scoreRepository.findById(score.getId()).orElseThrow();
    }

    protected void assertPersistedScoreToMatchAllProperties(Score expectedScore) {
        assertScoreAllPropertiesEquals(expectedScore, getPersistedScore(expectedScore));
    }

    protected void assertPersistedScoreToMatchUpdatableProperties(Score expectedScore) {
        assertScoreAllUpdatablePropertiesEquals(expectedScore, getPersistedScore(expectedScore));
    }
}
