package com.mycompany.skilltest.web.rest;

import static com.mycompany.skilltest.domain.CompetenceAsserts.*;
import static com.mycompany.skilltest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.skilltest.IntegrationTest;
import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.domain.Poste;
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.domain.enumeration.Level;
import com.mycompany.skilltest.repository.CompetenceRepository;
import com.mycompany.skilltest.service.dto.CompetenceDTO;
import com.mycompany.skilltest.service.mapper.CompetenceMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CompetenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CompetenceResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORIE = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORIE = "BBBBBBBBBB";

    private static final Level DEFAULT_NIVEAU_ATTENDU = Level.DEBUTANT;
    private static final Level UPDATED_NIVEAU_ATTENDU = Level.INTERMEDIAIRE;

    private static final String ENTITY_API_URL = "/api/competences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private CompetenceMapper competenceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompetenceMockMvc;

    private Competence competence;

    private Competence insertedCompetence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Competence createEntity() {
        return new Competence()
            .nom(DEFAULT_NOM)
            .description(DEFAULT_DESCRIPTION)
            .categorie(DEFAULT_CATEGORIE)
            .niveauAttendu(DEFAULT_NIVEAU_ATTENDU);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Competence createUpdatedEntity() {
        return new Competence()
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .categorie(UPDATED_CATEGORIE)
            .niveauAttendu(UPDATED_NIVEAU_ATTENDU);
    }

    @BeforeEach
    void initTest() {
        competence = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCompetence != null) {
            competenceRepository.delete(insertedCompetence);
            insertedCompetence = null;
        }
    }

    @Test
    @Transactional
    void createCompetence() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Competence
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);
        var returnedCompetenceDTO = om.readValue(
            restCompetenceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(competenceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CompetenceDTO.class
        );

        // Validate the Competence in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCompetence = competenceMapper.toEntity(returnedCompetenceDTO);
        assertCompetenceUpdatableFieldsEquals(returnedCompetence, getPersistedCompetence(returnedCompetence));

        insertedCompetence = returnedCompetence;
    }

    @Test
    @Transactional
    void createCompetenceWithExistingId() throws Exception {
        // Create the Competence with an existing ID
        competence.setId(1L);
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompetenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(competenceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Competence in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        competence.setNom(null);

        // Create the Competence, which fails.
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);

        restCompetenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(competenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNiveauAttenduIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        competence.setNiveauAttendu(null);

        // Create the Competence, which fails.
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);

        restCompetenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(competenceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCompetences() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList
        restCompetenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(competence.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].categorie").value(hasItem(DEFAULT_CATEGORIE)))
            .andExpect(jsonPath("$.[*].niveauAttendu").value(hasItem(DEFAULT_NIVEAU_ATTENDU.toString())));
    }

    @Test
    @Transactional
    void getCompetence() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get the competence
        restCompetenceMockMvc
            .perform(get(ENTITY_API_URL_ID, competence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(competence.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.categorie").value(DEFAULT_CATEGORIE))
            .andExpect(jsonPath("$.niveauAttendu").value(DEFAULT_NIVEAU_ATTENDU.toString()));
    }

    @Test
    @Transactional
    void getCompetencesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        Long id = competence.getId();

        defaultCompetenceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCompetenceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCompetenceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCompetencesByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where nom equals to
        defaultCompetenceFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCompetencesByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where nom in
        defaultCompetenceFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCompetencesByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where nom is not null
        defaultCompetenceFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllCompetencesByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where nom contains
        defaultCompetenceFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCompetencesByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where nom does not contain
        defaultCompetenceFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllCompetencesByCategorieIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where categorie equals to
        defaultCompetenceFiltering("categorie.equals=" + DEFAULT_CATEGORIE, "categorie.equals=" + UPDATED_CATEGORIE);
    }

    @Test
    @Transactional
    void getAllCompetencesByCategorieIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where categorie in
        defaultCompetenceFiltering("categorie.in=" + DEFAULT_CATEGORIE + "," + UPDATED_CATEGORIE, "categorie.in=" + UPDATED_CATEGORIE);
    }

    @Test
    @Transactional
    void getAllCompetencesByCategorieIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where categorie is not null
        defaultCompetenceFiltering("categorie.specified=true", "categorie.specified=false");
    }

    @Test
    @Transactional
    void getAllCompetencesByCategorieContainsSomething() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where categorie contains
        defaultCompetenceFiltering("categorie.contains=" + DEFAULT_CATEGORIE, "categorie.contains=" + UPDATED_CATEGORIE);
    }

    @Test
    @Transactional
    void getAllCompetencesByCategorieNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where categorie does not contain
        defaultCompetenceFiltering("categorie.doesNotContain=" + UPDATED_CATEGORIE, "categorie.doesNotContain=" + DEFAULT_CATEGORIE);
    }

    @Test
    @Transactional
    void getAllCompetencesByNiveauAttenduIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where niveauAttendu equals to
        defaultCompetenceFiltering("niveauAttendu.equals=" + DEFAULT_NIVEAU_ATTENDU, "niveauAttendu.equals=" + UPDATED_NIVEAU_ATTENDU);
    }

    @Test
    @Transactional
    void getAllCompetencesByNiveauAttenduIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where niveauAttendu in
        defaultCompetenceFiltering(
            "niveauAttendu.in=" + DEFAULT_NIVEAU_ATTENDU + "," + UPDATED_NIVEAU_ATTENDU,
            "niveauAttendu.in=" + UPDATED_NIVEAU_ATTENDU
        );
    }

    @Test
    @Transactional
    void getAllCompetencesByNiveauAttenduIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        // Get all the competenceList where niveauAttendu is not null
        defaultCompetenceFiltering("niveauAttendu.specified=true", "niveauAttendu.specified=false");
    }

    @Test
    @Transactional
    void getAllCompetencesByPostesIsEqualToSomething() throws Exception {
        Poste postes;
        if (TestUtil.findAll(em, Poste.class).isEmpty()) {
            competenceRepository.saveAndFlush(competence);
            postes = PosteResourceIT.createEntity();
        } else {
            postes = TestUtil.findAll(em, Poste.class).get(0);
        }
        em.persist(postes);
        em.flush();
        competence.addPostes(postes);
        competenceRepository.saveAndFlush(competence);
        Long postesId = postes.getId();
        // Get all the competenceList where postes equals to postesId
        defaultCompetenceShouldBeFound("postesId.equals=" + postesId);

        // Get all the competenceList where postes equals to (postesId + 1)
        defaultCompetenceShouldNotBeFound("postesId.equals=" + (postesId + 1));
    }

    @Test
    @Transactional
    void getAllCompetencesByTestsIsEqualToSomething() throws Exception {
        Test tests;
        if (TestUtil.findAll(em, Test.class).isEmpty()) {
            competenceRepository.saveAndFlush(competence);
            tests = TestResourceIT.createEntity();
        } else {
            tests = TestUtil.findAll(em, Test.class).get(0);
        }
        em.persist(tests);
        em.flush();
        competence.addTests(tests);
        competenceRepository.saveAndFlush(competence);
        Long testsId = tests.getId();
        // Get all the competenceList where tests equals to testsId
        defaultCompetenceShouldBeFound("testsId.equals=" + testsId);

        // Get all the competenceList where tests equals to (testsId + 1)
        defaultCompetenceShouldNotBeFound("testsId.equals=" + (testsId + 1));
    }

    private void defaultCompetenceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCompetenceShouldBeFound(shouldBeFound);
        defaultCompetenceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCompetenceShouldBeFound(String filter) throws Exception {
        restCompetenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(competence.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].categorie").value(hasItem(DEFAULT_CATEGORIE)))
            .andExpect(jsonPath("$.[*].niveauAttendu").value(hasItem(DEFAULT_NIVEAU_ATTENDU.toString())));

        // Check, that the count call also returns 1
        restCompetenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCompetenceShouldNotBeFound(String filter) throws Exception {
        restCompetenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCompetenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCompetence() throws Exception {
        // Get the competence
        restCompetenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCompetence() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the competence
        Competence updatedCompetence = competenceRepository.findById(competence.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCompetence are not directly saved in db
        em.detach(updatedCompetence);
        updatedCompetence
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .categorie(UPDATED_CATEGORIE)
            .niveauAttendu(UPDATED_NIVEAU_ATTENDU);
        CompetenceDTO competenceDTO = competenceMapper.toDto(updatedCompetence);

        restCompetenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, competenceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(competenceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Competence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCompetenceToMatchAllProperties(updatedCompetence);
    }

    @Test
    @Transactional
    void putNonExistingCompetence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        competence.setId(longCount.incrementAndGet());

        // Create the Competence
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompetenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, competenceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(competenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Competence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompetence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        competence.setId(longCount.incrementAndGet());

        // Create the Competence
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompetenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(competenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Competence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompetence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        competence.setId(longCount.incrementAndGet());

        // Create the Competence
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompetenceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(competenceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Competence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCompetenceWithPatch() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the competence using partial update
        Competence partialUpdatedCompetence = new Competence();
        partialUpdatedCompetence.setId(competence.getId());

        partialUpdatedCompetence.description(UPDATED_DESCRIPTION);

        restCompetenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompetence.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompetence))
            )
            .andExpect(status().isOk());

        // Validate the Competence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompetenceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCompetence, competence),
            getPersistedCompetence(competence)
        );
    }

    @Test
    @Transactional
    void fullUpdateCompetenceWithPatch() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the competence using partial update
        Competence partialUpdatedCompetence = new Competence();
        partialUpdatedCompetence.setId(competence.getId());

        partialUpdatedCompetence
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .categorie(UPDATED_CATEGORIE)
            .niveauAttendu(UPDATED_NIVEAU_ATTENDU);

        restCompetenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompetence.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompetence))
            )
            .andExpect(status().isOk());

        // Validate the Competence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompetenceUpdatableFieldsEquals(partialUpdatedCompetence, getPersistedCompetence(partialUpdatedCompetence));
    }

    @Test
    @Transactional
    void patchNonExistingCompetence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        competence.setId(longCount.incrementAndGet());

        // Create the Competence
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompetenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, competenceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(competenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Competence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompetence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        competence.setId(longCount.incrementAndGet());

        // Create the Competence
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompetenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(competenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Competence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompetence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        competence.setId(longCount.incrementAndGet());

        // Create the Competence
        CompetenceDTO competenceDTO = competenceMapper.toDto(competence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompetenceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(competenceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Competence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCompetence() throws Exception {
        // Initialize the database
        insertedCompetence = competenceRepository.saveAndFlush(competence);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the competence
        restCompetenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, competence.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return competenceRepository.count();
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

    protected Competence getPersistedCompetence(Competence competence) {
        return competenceRepository.findById(competence.getId()).orElseThrow();
    }

    protected void assertPersistedCompetenceToMatchAllProperties(Competence expectedCompetence) {
        assertCompetenceAllPropertiesEquals(expectedCompetence, getPersistedCompetence(expectedCompetence));
    }

    protected void assertPersistedCompetenceToMatchUpdatableProperties(Competence expectedCompetence) {
        assertCompetenceAllUpdatablePropertiesEquals(expectedCompetence, getPersistedCompetence(expectedCompetence));
    }
}
