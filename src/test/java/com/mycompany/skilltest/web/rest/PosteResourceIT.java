package com.mycompany.skilltest.web.rest;

import static com.mycompany.skilltest.domain.PosteAsserts.*;
import static com.mycompany.skilltest.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.skilltest.IntegrationTest;
import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.domain.Poste;
import com.mycompany.skilltest.domain.enumeration.Level;
import com.mycompany.skilltest.repository.PosteRepository;
import com.mycompany.skilltest.service.PosteService;
import com.mycompany.skilltest.service.dto.PosteDTO;
import com.mycompany.skilltest.service.mapper.PosteMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PosteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PosteResourceIT {

    private static final String DEFAULT_INTITULE = "AAAAAAAAAA";
    private static final String UPDATED_INTITULE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Level DEFAULT_NIVEAU_REQUIS = Level.DEBUTANT;
    private static final Level UPDATED_NIVEAU_REQUIS = Level.INTERMEDIAIRE;

    private static final String ENTITY_API_URL = "/api/postes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PosteRepository posteRepository;

    @Mock
    private PosteRepository posteRepositoryMock;

    @Autowired
    private PosteMapper posteMapper;

    @Mock
    private PosteService posteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPosteMockMvc;

    private Poste poste;

    private Poste insertedPoste;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Poste createEntity() {
        return new Poste().intitule(DEFAULT_INTITULE).description(DEFAULT_DESCRIPTION).niveauRequis(DEFAULT_NIVEAU_REQUIS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Poste createUpdatedEntity() {
        return new Poste().intitule(UPDATED_INTITULE).description(UPDATED_DESCRIPTION).niveauRequis(UPDATED_NIVEAU_REQUIS);
    }

    @BeforeEach
    void initTest() {
        poste = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPoste != null) {
            posteRepository.delete(insertedPoste);
            insertedPoste = null;
        }
    }

    @Test
    @Transactional
    void createPoste() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);
        var returnedPosteDTO = om.readValue(
            restPosteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PosteDTO.class
        );

        // Validate the Poste in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPoste = posteMapper.toEntity(returnedPosteDTO);
        assertPosteUpdatableFieldsEquals(returnedPoste, getPersistedPoste(returnedPoste));

        insertedPoste = returnedPoste;
    }

    @Test
    @Transactional
    void createPosteWithExistingId() throws Exception {
        // Create the Poste with an existing ID
        poste.setId(1L);
        PosteDTO posteDTO = posteMapper.toDto(poste);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPosteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIntituleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        poste.setIntitule(null);

        // Create the Poste, which fails.
        PosteDTO posteDTO = posteMapper.toDto(poste);

        restPosteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNiveauRequisIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        poste.setNiveauRequis(null);

        // Create the Poste, which fails.
        PosteDTO posteDTO = posteMapper.toDto(poste);

        restPosteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPostes() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get all the posteList
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(poste.getId().intValue())))
            .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].niveauRequis").value(hasItem(DEFAULT_NIVEAU_REQUIS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostesWithEagerRelationshipsIsEnabled() throws Exception {
        when(posteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPosteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(posteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(posteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPosteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(posteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPoste() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get the poste
        restPosteMockMvc
            .perform(get(ENTITY_API_URL_ID, poste.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(poste.getId().intValue()))
            .andExpect(jsonPath("$.intitule").value(DEFAULT_INTITULE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.niveauRequis").value(DEFAULT_NIVEAU_REQUIS.toString()));
    }

    @Test
    @Transactional
    void getPostesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        Long id = poste.getId();

        defaultPosteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPosteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPosteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPostesByIntituleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get all the posteList where intitule equals to
        defaultPosteFiltering("intitule.equals=" + DEFAULT_INTITULE, "intitule.equals=" + UPDATED_INTITULE);
    }

    @Test
    @Transactional
    void getAllPostesByIntituleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get all the posteList where intitule in
        defaultPosteFiltering("intitule.in=" + DEFAULT_INTITULE + "," + UPDATED_INTITULE, "intitule.in=" + UPDATED_INTITULE);
    }

    @Test
    @Transactional
    void getAllPostesByIntituleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get all the posteList where intitule is not null
        defaultPosteFiltering("intitule.specified=true", "intitule.specified=false");
    }

    @Test
    @Transactional
    void getAllPostesByIntituleContainsSomething() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get all the posteList where intitule contains
        defaultPosteFiltering("intitule.contains=" + DEFAULT_INTITULE, "intitule.contains=" + UPDATED_INTITULE);
    }

    @Test
    @Transactional
    void getAllPostesByIntituleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get all the posteList where intitule does not contain
        defaultPosteFiltering("intitule.doesNotContain=" + UPDATED_INTITULE, "intitule.doesNotContain=" + DEFAULT_INTITULE);
    }

    @Test
    @Transactional
    void getAllPostesByNiveauRequisIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get all the posteList where niveauRequis equals to
        defaultPosteFiltering("niveauRequis.equals=" + DEFAULT_NIVEAU_REQUIS, "niveauRequis.equals=" + UPDATED_NIVEAU_REQUIS);
    }

    @Test
    @Transactional
    void getAllPostesByNiveauRequisIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get all the posteList where niveauRequis in
        defaultPosteFiltering(
            "niveauRequis.in=" + DEFAULT_NIVEAU_REQUIS + "," + UPDATED_NIVEAU_REQUIS,
            "niveauRequis.in=" + UPDATED_NIVEAU_REQUIS
        );
    }

    @Test
    @Transactional
    void getAllPostesByNiveauRequisIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        // Get all the posteList where niveauRequis is not null
        defaultPosteFiltering("niveauRequis.specified=true", "niveauRequis.specified=false");
    }

    @Test
    @Transactional
    void getAllPostesByCompetencesIsEqualToSomething() throws Exception {
        Competence competences;
        if (TestUtil.findAll(em, Competence.class).isEmpty()) {
            posteRepository.saveAndFlush(poste);
            competences = CompetenceResourceIT.createEntity();
        } else {
            competences = TestUtil.findAll(em, Competence.class).get(0);
        }
        em.persist(competences);
        em.flush();
        poste.addCompetences(competences);
        posteRepository.saveAndFlush(poste);
        Long competencesId = competences.getId();
        // Get all the posteList where competences equals to competencesId
        defaultPosteShouldBeFound("competencesId.equals=" + competencesId);

        // Get all the posteList where competences equals to (competencesId + 1)
        defaultPosteShouldNotBeFound("competencesId.equals=" + (competencesId + 1));
    }

    private void defaultPosteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPosteShouldBeFound(shouldBeFound);
        defaultPosteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPosteShouldBeFound(String filter) throws Exception {
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(poste.getId().intValue())))
            .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].niveauRequis").value(hasItem(DEFAULT_NIVEAU_REQUIS.toString())));

        // Check, that the count call also returns 1
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPosteShouldNotBeFound(String filter) throws Exception {
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPoste() throws Exception {
        // Get the poste
        restPosteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPoste() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poste
        Poste updatedPoste = posteRepository.findById(poste.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPoste are not directly saved in db
        em.detach(updatedPoste);
        updatedPoste.intitule(UPDATED_INTITULE).description(UPDATED_DESCRIPTION).niveauRequis(UPDATED_NIVEAU_REQUIS);
        PosteDTO posteDTO = posteMapper.toDto(updatedPoste);

        restPosteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, posteDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Poste in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPosteToMatchAllProperties(updatedPoste);
    }

    @Test
    @Transactional
    void putNonExistingPoste() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poste.setId(longCount.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, posteDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPoste() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poste.setId(longCount.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(posteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPoste() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poste.setId(longCount.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(posteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Poste in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePosteWithPatch() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poste using partial update
        Poste partialUpdatedPoste = new Poste();
        partialUpdatedPoste.setId(poste.getId());

        partialUpdatedPoste.niveauRequis(UPDATED_NIVEAU_REQUIS);

        restPosteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoste.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoste))
            )
            .andExpect(status().isOk());

        // Validate the Poste in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPosteUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPoste, poste), getPersistedPoste(poste));
    }

    @Test
    @Transactional
    void fullUpdatePosteWithPatch() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poste using partial update
        Poste partialUpdatedPoste = new Poste();
        partialUpdatedPoste.setId(poste.getId());

        partialUpdatedPoste.intitule(UPDATED_INTITULE).description(UPDATED_DESCRIPTION).niveauRequis(UPDATED_NIVEAU_REQUIS);

        restPosteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoste.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoste))
            )
            .andExpect(status().isOk());

        // Validate the Poste in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPosteUpdatableFieldsEquals(partialUpdatedPoste, getPersistedPoste(partialUpdatedPoste));
    }

    @Test
    @Transactional
    void patchNonExistingPoste() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poste.setId(longCount.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, posteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(posteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPoste() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poste.setId(longCount.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(posteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPoste() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poste.setId(longCount.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(posteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Poste in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePoste() throws Exception {
        // Initialize the database
        insertedPoste = posteRepository.saveAndFlush(poste);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the poste
        restPosteMockMvc
            .perform(delete(ENTITY_API_URL_ID, poste.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return posteRepository.count();
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

    protected Poste getPersistedPoste(Poste poste) {
        return posteRepository.findById(poste.getId()).orElseThrow();
    }

    protected void assertPersistedPosteToMatchAllProperties(Poste expectedPoste) {
        assertPosteAllPropertiesEquals(expectedPoste, getPersistedPoste(expectedPoste));
    }

    protected void assertPersistedPosteToMatchUpdatableProperties(Poste expectedPoste) {
        assertPosteAllUpdatablePropertiesEquals(expectedPoste, getPersistedPoste(expectedPoste));
    }
}
