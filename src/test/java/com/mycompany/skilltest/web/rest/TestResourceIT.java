package com.mycompany.skilltest.web.rest;

import static com.mycompany.skilltest.domain.TestAsserts.*;
import static com.mycompany.skilltest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.skilltest.IntegrationTest;
import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.domain.enumeration.TestMode;
import com.mycompany.skilltest.repository.TestRepository;
import com.mycompany.skilltest.service.TestService;
import com.mycompany.skilltest.service.dto.TestDTO;
import com.mycompany.skilltest.service.mapper.TestMapper;
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
 * Integration tests for the {@link TestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TestResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final TestMode DEFAULT_MODE = TestMode.CLASSIQUE;
    private static final TestMode UPDATED_MODE = TestMode.DISCRET;

    private static final Integer DEFAULT_DUREE = 1;
    private static final Integer UPDATED_DUREE = 2;
    private static final Integer SMALLER_DUREE = 1 - 1;

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/tests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestRepository testRepository;

    @Mock
    private TestRepository testRepositoryMock;

    @Autowired
    private TestMapper testMapper;

    @Mock
    private TestService testServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTestMockMvc;

    private Test test;

    private Test insertedTest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Test createEntity() {
        return new Test()
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .mode(DEFAULT_MODE)
            .duree(DEFAULT_DUREE)
            .dateCreation(DEFAULT_DATE_CREATION)
            .actif(DEFAULT_ACTIF);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Test createUpdatedEntity() {
        return new Test()
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .mode(UPDATED_MODE)
            .duree(UPDATED_DUREE)
            .dateCreation(UPDATED_DATE_CREATION)
            .actif(UPDATED_ACTIF);
    }

    @BeforeEach
    void initTest() {
        test = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTest != null) {
            testRepository.delete(insertedTest);
            insertedTest = null;
        }
    }

    @Test
    @Transactional
    void createTest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Test
        TestDTO testDTO = testMapper.toDto(test);
        var returnedTestDTO = om.readValue(
            restTestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TestDTO.class
        );

        // Validate the Test in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTest = testMapper.toEntity(returnedTestDTO);
        assertTestUpdatableFieldsEquals(returnedTest, getPersistedTest(returnedTest));

        insertedTest = returnedTest;
    }

    @Test
    @Transactional
    void createTestWithExistingId() throws Exception {
        // Create the Test with an existing ID
        test.setId(1L);
        TestDTO testDTO = testMapper.toDto(test);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Test in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        test.setTitre(null);

        // Create the Test, which fails.
        TestDTO testDTO = testMapper.toDto(test);

        restTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        test.setMode(null);

        // Create the Test, which fails.
        TestDTO testDTO = testMapper.toDto(test);

        restTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        test.setDateCreation(null);

        // Create the Test, which fails.
        TestDTO testDTO = testMapper.toDto(test);

        restTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        test.setActif(null);

        // Create the Test, which fails.
        TestDTO testDTO = testMapper.toDto(test);

        restTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTests() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList
        restTestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(test.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE.toString())))
            .andExpect(jsonPath("$.[*].duree").value(hasItem(DEFAULT_DUREE)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(testServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(testServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(testServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(testRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTest() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get the test
        restTestMockMvc
            .perform(get(ENTITY_API_URL_ID, test.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(test.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.mode").value(DEFAULT_MODE.toString()))
            .andExpect(jsonPath("$.duree").value(DEFAULT_DUREE))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getTestsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        Long id = test.getId();

        defaultTestFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTestFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTestFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTestsByTitreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where titre equals to
        defaultTestFiltering("titre.equals=" + DEFAULT_TITRE, "titre.equals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTestsByTitreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where titre in
        defaultTestFiltering("titre.in=" + DEFAULT_TITRE + "," + UPDATED_TITRE, "titre.in=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTestsByTitreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where titre is not null
        defaultTestFiltering("titre.specified=true", "titre.specified=false");
    }

    @Test
    @Transactional
    void getAllTestsByTitreContainsSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where titre contains
        defaultTestFiltering("titre.contains=" + DEFAULT_TITRE, "titre.contains=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTestsByTitreNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where titre does not contain
        defaultTestFiltering("titre.doesNotContain=" + UPDATED_TITRE, "titre.doesNotContain=" + DEFAULT_TITRE);
    }

    @Test
    @Transactional
    void getAllTestsByModeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where mode equals to
        defaultTestFiltering("mode.equals=" + DEFAULT_MODE, "mode.equals=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllTestsByModeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where mode in
        defaultTestFiltering("mode.in=" + DEFAULT_MODE + "," + UPDATED_MODE, "mode.in=" + UPDATED_MODE);
    }

    @Test
    @Transactional
    void getAllTestsByModeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where mode is not null
        defaultTestFiltering("mode.specified=true", "mode.specified=false");
    }

    @Test
    @Transactional
    void getAllTestsByDureeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where duree equals to
        defaultTestFiltering("duree.equals=" + DEFAULT_DUREE, "duree.equals=" + UPDATED_DUREE);
    }

    @Test
    @Transactional
    void getAllTestsByDureeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where duree in
        defaultTestFiltering("duree.in=" + DEFAULT_DUREE + "," + UPDATED_DUREE, "duree.in=" + UPDATED_DUREE);
    }

    @Test
    @Transactional
    void getAllTestsByDureeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where duree is not null
        defaultTestFiltering("duree.specified=true", "duree.specified=false");
    }

    @Test
    @Transactional
    void getAllTestsByDureeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where duree is greater than or equal to
        defaultTestFiltering("duree.greaterThanOrEqual=" + DEFAULT_DUREE, "duree.greaterThanOrEqual=" + UPDATED_DUREE);
    }

    @Test
    @Transactional
    void getAllTestsByDureeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where duree is less than or equal to
        defaultTestFiltering("duree.lessThanOrEqual=" + DEFAULT_DUREE, "duree.lessThanOrEqual=" + SMALLER_DUREE);
    }

    @Test
    @Transactional
    void getAllTestsByDureeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where duree is less than
        defaultTestFiltering("duree.lessThan=" + UPDATED_DUREE, "duree.lessThan=" + DEFAULT_DUREE);
    }

    @Test
    @Transactional
    void getAllTestsByDureeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where duree is greater than
        defaultTestFiltering("duree.greaterThan=" + SMALLER_DUREE, "duree.greaterThan=" + DEFAULT_DUREE);
    }

    @Test
    @Transactional
    void getAllTestsByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where dateCreation equals to
        defaultTestFiltering("dateCreation.equals=" + DEFAULT_DATE_CREATION, "dateCreation.equals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    void getAllTestsByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where dateCreation in
        defaultTestFiltering(
            "dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION,
            "dateCreation.in=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllTestsByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where dateCreation is not null
        defaultTestFiltering("dateCreation.specified=true", "dateCreation.specified=false");
    }

    @Test
    @Transactional
    void getAllTestsByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where actif equals to
        defaultTestFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllTestsByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where actif in
        defaultTestFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllTestsByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        // Get all the testList where actif is not null
        defaultTestFiltering("actif.specified=true", "actif.specified=false");
    }

    @Test
    @Transactional
    void getAllTestsByCompetencesIsEqualToSomething() throws Exception {
        Competence competences;
        if (TestUtil.findAll(em, Competence.class).isEmpty()) {
            testRepository.saveAndFlush(test);
            competences = CompetenceResourceIT.createEntity();
        } else {
            competences = TestUtil.findAll(em, Competence.class).get(0);
        }
        em.persist(competences);
        em.flush();
        test.addCompetences(competences);
        testRepository.saveAndFlush(test);
        Long competencesId = competences.getId();
        // Get all the testList where competences equals to competencesId
        defaultTestShouldBeFound("competencesId.equals=" + competencesId);

        // Get all the testList where competences equals to (competencesId + 1)
        defaultTestShouldNotBeFound("competencesId.equals=" + (competencesId + 1));
    }

    private void defaultTestFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTestShouldBeFound(shouldBeFound);
        defaultTestShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTestShouldBeFound(String filter) throws Exception {
        restTestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(test.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE.toString())))
            .andExpect(jsonPath("$.[*].duree").value(hasItem(DEFAULT_DUREE)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restTestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTestShouldNotBeFound(String filter) throws Exception {
        restTestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTest() throws Exception {
        // Get the test
        restTestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTest() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the test
        Test updatedTest = testRepository.findById(test.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTest are not directly saved in db
        em.detach(updatedTest);
        updatedTest
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .mode(UPDATED_MODE)
            .duree(UPDATED_DUREE)
            .dateCreation(UPDATED_DATE_CREATION)
            .actif(UPDATED_ACTIF);
        TestDTO testDTO = testMapper.toDto(updatedTest);

        restTestMockMvc
            .perform(put(ENTITY_API_URL_ID, testDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testDTO)))
            .andExpect(status().isOk());

        // Validate the Test in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTestToMatchAllProperties(updatedTest);
    }

    @Test
    @Transactional
    void putNonExistingTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        test.setId(longCount.incrementAndGet());

        // Create the Test
        TestDTO testDTO = testMapper.toDto(test);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestMockMvc
            .perform(put(ENTITY_API_URL_ID, testDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Test in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        test.setId(longCount.incrementAndGet());

        // Create the Test
        TestDTO testDTO = testMapper.toDto(test);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(testDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Test in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        test.setId(longCount.incrementAndGet());

        // Create the Test
        TestDTO testDTO = testMapper.toDto(test);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Test in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTestWithPatch() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the test using partial update
        Test partialUpdatedTest = new Test();
        partialUpdatedTest.setId(test.getId());

        partialUpdatedTest.mode(UPDATED_MODE).dateCreation(UPDATED_DATE_CREATION);

        restTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTest))
            )
            .andExpect(status().isOk());

        // Validate the Test in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTest, test), getPersistedTest(test));
    }

    @Test
    @Transactional
    void fullUpdateTestWithPatch() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the test using partial update
        Test partialUpdatedTest = new Test();
        partialUpdatedTest.setId(test.getId());

        partialUpdatedTest
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .mode(UPDATED_MODE)
            .duree(UPDATED_DUREE)
            .dateCreation(UPDATED_DATE_CREATION)
            .actif(UPDATED_ACTIF);

        restTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTest))
            )
            .andExpect(status().isOk());

        // Validate the Test in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestUpdatableFieldsEquals(partialUpdatedTest, getPersistedTest(partialUpdatedTest));
    }

    @Test
    @Transactional
    void patchNonExistingTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        test.setId(longCount.incrementAndGet());

        // Create the Test
        TestDTO testDTO = testMapper.toDto(test);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, testDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(testDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Test in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        test.setId(longCount.incrementAndGet());

        // Create the Test
        TestDTO testDTO = testMapper.toDto(test);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(testDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Test in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        test.setId(longCount.incrementAndGet());

        // Create the Test
        TestDTO testDTO = testMapper.toDto(test);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(testDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Test in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTest() throws Exception {
        // Initialize the database
        insertedTest = testRepository.saveAndFlush(test);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the test
        restTestMockMvc
            .perform(delete(ENTITY_API_URL_ID, test.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return testRepository.count();
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

    protected Test getPersistedTest(Test test) {
        return testRepository.findById(test.getId()).orElseThrow();
    }

    protected void assertPersistedTestToMatchAllProperties(Test expectedTest) {
        assertTestAllPropertiesEquals(expectedTest, getPersistedTest(expectedTest));
    }

    protected void assertPersistedTestToMatchUpdatableProperties(Test expectedTest) {
        assertTestAllUpdatablePropertiesEquals(expectedTest, getPersistedTest(expectedTest));
    }
}
