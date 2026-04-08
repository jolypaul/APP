package com.mycompany.skilltest.web.rest;

import static com.mycompany.skilltest.domain.ReponseAsserts.*;
import static com.mycompany.skilltest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.skilltest.IntegrationTest;
import com.mycompany.skilltest.domain.Evaluation;
import com.mycompany.skilltest.domain.Question;
import com.mycompany.skilltest.domain.Reponse;
import com.mycompany.skilltest.repository.ReponseRepository;
import com.mycompany.skilltest.service.ReponseService;
import com.mycompany.skilltest.service.dto.ReponseDTO;
import com.mycompany.skilltest.service.mapper.ReponseMapper;
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
 * Integration tests for the {@link ReponseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReponseResourceIT {

    private static final String DEFAULT_CONTENU = "AAAAAAAAAA";
    private static final String UPDATED_CONTENU = "BBBBBBBBBB";

    private static final Boolean DEFAULT_EST_CORRECTE = false;
    private static final Boolean UPDATED_EST_CORRECTE = true;

    private static final Instant DEFAULT_DATE_REPONSE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_REPONSE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COMMENTAIRE_MANAGER = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE_MANAGER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/reponses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReponseRepository reponseRepository;

    @Mock
    private ReponseRepository reponseRepositoryMock;

    @Autowired
    private ReponseMapper reponseMapper;

    @Mock
    private ReponseService reponseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReponseMockMvc;

    private Reponse reponse;

    private Reponse insertedReponse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reponse createEntity() {
        return new Reponse()
            .contenu(DEFAULT_CONTENU)
            .estCorrecte(DEFAULT_EST_CORRECTE)
            .dateReponse(DEFAULT_DATE_REPONSE)
            .commentaireManager(DEFAULT_COMMENTAIRE_MANAGER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reponse createUpdatedEntity() {
        return new Reponse()
            .contenu(UPDATED_CONTENU)
            .estCorrecte(UPDATED_EST_CORRECTE)
            .dateReponse(UPDATED_DATE_REPONSE)
            .commentaireManager(UPDATED_COMMENTAIRE_MANAGER);
    }

    @BeforeEach
    void initTest() {
        reponse = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReponse != null) {
            reponseRepository.delete(insertedReponse);
            insertedReponse = null;
        }
    }

    @Test
    @Transactional
    void createReponse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Reponse
        ReponseDTO reponseDTO = reponseMapper.toDto(reponse);
        var returnedReponseDTO = om.readValue(
            restReponseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reponseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReponseDTO.class
        );

        // Validate the Reponse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReponse = reponseMapper.toEntity(returnedReponseDTO);
        assertReponseUpdatableFieldsEquals(returnedReponse, getPersistedReponse(returnedReponse));

        insertedReponse = returnedReponse;
    }

    @Test
    @Transactional
    void createReponseWithExistingId() throws Exception {
        // Create the Reponse with an existing ID
        reponse.setId(1L);
        ReponseDTO reponseDTO = reponseMapper.toDto(reponse);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reponseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Reponse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateReponseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        reponse.setDateReponse(null);

        // Create the Reponse, which fails.
        ReponseDTO reponseDTO = reponseMapper.toDto(reponse);

        restReponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reponseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReponses() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        // Get all the reponseList
        restReponseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reponse.getId().intValue())))
            .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)))
            .andExpect(jsonPath("$.[*].estCorrecte").value(hasItem(DEFAULT_EST_CORRECTE)))
            .andExpect(jsonPath("$.[*].dateReponse").value(hasItem(DEFAULT_DATE_REPONSE.toString())))
            .andExpect(jsonPath("$.[*].commentaireManager").value(hasItem(DEFAULT_COMMENTAIRE_MANAGER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReponsesWithEagerRelationshipsIsEnabled() throws Exception {
        when(reponseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReponseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(reponseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReponsesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(reponseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReponseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(reponseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReponse() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        // Get the reponse
        restReponseMockMvc
            .perform(get(ENTITY_API_URL_ID, reponse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reponse.getId().intValue()))
            .andExpect(jsonPath("$.contenu").value(DEFAULT_CONTENU))
            .andExpect(jsonPath("$.estCorrecte").value(DEFAULT_EST_CORRECTE))
            .andExpect(jsonPath("$.dateReponse").value(DEFAULT_DATE_REPONSE.toString()))
            .andExpect(jsonPath("$.commentaireManager").value(DEFAULT_COMMENTAIRE_MANAGER));
    }

    @Test
    @Transactional
    void getReponsesByIdFiltering() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        Long id = reponse.getId();

        defaultReponseFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReponseFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReponseFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReponsesByEstCorrecteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        // Get all the reponseList where estCorrecte equals to
        defaultReponseFiltering("estCorrecte.equals=" + DEFAULT_EST_CORRECTE, "estCorrecte.equals=" + UPDATED_EST_CORRECTE);
    }

    @Test
    @Transactional
    void getAllReponsesByEstCorrecteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        // Get all the reponseList where estCorrecte in
        defaultReponseFiltering(
            "estCorrecte.in=" + DEFAULT_EST_CORRECTE + "," + UPDATED_EST_CORRECTE,
            "estCorrecte.in=" + UPDATED_EST_CORRECTE
        );
    }

    @Test
    @Transactional
    void getAllReponsesByEstCorrecteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        // Get all the reponseList where estCorrecte is not null
        defaultReponseFiltering("estCorrecte.specified=true", "estCorrecte.specified=false");
    }

    @Test
    @Transactional
    void getAllReponsesByDateReponseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        // Get all the reponseList where dateReponse equals to
        defaultReponseFiltering("dateReponse.equals=" + DEFAULT_DATE_REPONSE, "dateReponse.equals=" + UPDATED_DATE_REPONSE);
    }

    @Test
    @Transactional
    void getAllReponsesByDateReponseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        // Get all the reponseList where dateReponse in
        defaultReponseFiltering(
            "dateReponse.in=" + DEFAULT_DATE_REPONSE + "," + UPDATED_DATE_REPONSE,
            "dateReponse.in=" + UPDATED_DATE_REPONSE
        );
    }

    @Test
    @Transactional
    void getAllReponsesByDateReponseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        // Get all the reponseList where dateReponse is not null
        defaultReponseFiltering("dateReponse.specified=true", "dateReponse.specified=false");
    }

    @Test
    @Transactional
    void getAllReponsesByQuestionIsEqualToSomething() throws Exception {
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            reponseRepository.saveAndFlush(reponse);
            question = QuestionResourceIT.createEntity();
        } else {
            question = TestUtil.findAll(em, Question.class).get(0);
        }
        em.persist(question);
        em.flush();
        reponse.setQuestion(question);
        reponseRepository.saveAndFlush(reponse);
        Long questionId = question.getId();
        // Get all the reponseList where question equals to questionId
        defaultReponseShouldBeFound("questionId.equals=" + questionId);

        // Get all the reponseList where question equals to (questionId + 1)
        defaultReponseShouldNotBeFound("questionId.equals=" + (questionId + 1));
    }

    @Test
    @Transactional
    void getAllReponsesByEvaluationIsEqualToSomething() throws Exception {
        Evaluation evaluation;
        if (TestUtil.findAll(em, Evaluation.class).isEmpty()) {
            reponseRepository.saveAndFlush(reponse);
            evaluation = EvaluationResourceIT.createEntity();
        } else {
            evaluation = TestUtil.findAll(em, Evaluation.class).get(0);
        }
        em.persist(evaluation);
        em.flush();
        reponse.setEvaluation(evaluation);
        reponseRepository.saveAndFlush(reponse);
        Long evaluationId = evaluation.getId();
        // Get all the reponseList where evaluation equals to evaluationId
        defaultReponseShouldBeFound("evaluationId.equals=" + evaluationId);

        // Get all the reponseList where evaluation equals to (evaluationId + 1)
        defaultReponseShouldNotBeFound("evaluationId.equals=" + (evaluationId + 1));
    }

    private void defaultReponseFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReponseShouldBeFound(shouldBeFound);
        defaultReponseShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReponseShouldBeFound(String filter) throws Exception {
        restReponseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reponse.getId().intValue())))
            .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)))
            .andExpect(jsonPath("$.[*].estCorrecte").value(hasItem(DEFAULT_EST_CORRECTE)))
            .andExpect(jsonPath("$.[*].dateReponse").value(hasItem(DEFAULT_DATE_REPONSE.toString())))
            .andExpect(jsonPath("$.[*].commentaireManager").value(hasItem(DEFAULT_COMMENTAIRE_MANAGER)));

        // Check, that the count call also returns 1
        restReponseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReponseShouldNotBeFound(String filter) throws Exception {
        restReponseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReponseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReponse() throws Exception {
        // Get the reponse
        restReponseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReponse() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reponse
        Reponse updatedReponse = reponseRepository.findById(reponse.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReponse are not directly saved in db
        em.detach(updatedReponse);
        updatedReponse
            .contenu(UPDATED_CONTENU)
            .estCorrecte(UPDATED_EST_CORRECTE)
            .dateReponse(UPDATED_DATE_REPONSE)
            .commentaireManager(UPDATED_COMMENTAIRE_MANAGER);
        ReponseDTO reponseDTO = reponseMapper.toDto(updatedReponse);

        restReponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reponseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reponseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Reponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReponseToMatchAllProperties(updatedReponse);
    }

    @Test
    @Transactional
    void putNonExistingReponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reponse.setId(longCount.incrementAndGet());

        // Create the Reponse
        ReponseDTO reponseDTO = reponseMapper.toDto(reponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reponseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reponse.setId(longCount.incrementAndGet());

        // Create the Reponse
        ReponseDTO reponseDTO = reponseMapper.toDto(reponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reponse.setId(longCount.incrementAndGet());

        // Create the Reponse
        ReponseDTO reponseDTO = reponseMapper.toDto(reponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReponseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reponseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReponseWithPatch() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reponse using partial update
        Reponse partialUpdatedReponse = new Reponse();
        partialUpdatedReponse.setId(reponse.getId());

        partialUpdatedReponse
            .contenu(UPDATED_CONTENU)
            .estCorrecte(UPDATED_EST_CORRECTE)
            .dateReponse(UPDATED_DATE_REPONSE)
            .commentaireManager(UPDATED_COMMENTAIRE_MANAGER);

        restReponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReponse))
            )
            .andExpect(status().isOk());

        // Validate the Reponse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReponseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedReponse, reponse), getPersistedReponse(reponse));
    }

    @Test
    @Transactional
    void fullUpdateReponseWithPatch() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reponse using partial update
        Reponse partialUpdatedReponse = new Reponse();
        partialUpdatedReponse.setId(reponse.getId());

        partialUpdatedReponse
            .contenu(UPDATED_CONTENU)
            .estCorrecte(UPDATED_EST_CORRECTE)
            .dateReponse(UPDATED_DATE_REPONSE)
            .commentaireManager(UPDATED_COMMENTAIRE_MANAGER);

        restReponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReponse))
            )
            .andExpect(status().isOk());

        // Validate the Reponse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReponseUpdatableFieldsEquals(partialUpdatedReponse, getPersistedReponse(partialUpdatedReponse));
    }

    @Test
    @Transactional
    void patchNonExistingReponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reponse.setId(longCount.incrementAndGet());

        // Create the Reponse
        ReponseDTO reponseDTO = reponseMapper.toDto(reponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reponseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reponse.setId(longCount.incrementAndGet());

        // Create the Reponse
        ReponseDTO reponseDTO = reponseMapper.toDto(reponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reponse.setId(longCount.incrementAndGet());

        // Create the Reponse
        ReponseDTO reponseDTO = reponseMapper.toDto(reponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReponseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reponseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReponse() throws Exception {
        // Initialize the database
        insertedReponse = reponseRepository.saveAndFlush(reponse);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the reponse
        restReponseMockMvc
            .perform(delete(ENTITY_API_URL_ID, reponse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reponseRepository.count();
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

    protected Reponse getPersistedReponse(Reponse reponse) {
        return reponseRepository.findById(reponse.getId()).orElseThrow();
    }

    protected void assertPersistedReponseToMatchAllProperties(Reponse expectedReponse) {
        assertReponseAllPropertiesEquals(expectedReponse, getPersistedReponse(expectedReponse));
    }

    protected void assertPersistedReponseToMatchUpdatableProperties(Reponse expectedReponse) {
        assertReponseAllUpdatablePropertiesEquals(expectedReponse, getPersistedReponse(expectedReponse));
    }
}
