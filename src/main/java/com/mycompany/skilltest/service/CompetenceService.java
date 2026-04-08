package com.mycompany.skilltest.service;

import com.mycompany.skilltest.service.dto.CompetenceDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.skilltest.domain.Competence}.
 */
public interface CompetenceService {
    /**
     * Save a competence.
     *
     * @param competenceDTO the entity to save.
     * @return the persisted entity.
     */
    CompetenceDTO save(CompetenceDTO competenceDTO);

    /**
     * Updates a competence.
     *
     * @param competenceDTO the entity to update.
     * @return the persisted entity.
     */
    CompetenceDTO update(CompetenceDTO competenceDTO);

    /**
     * Partially updates a competence.
     *
     * @param competenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CompetenceDTO> partialUpdate(CompetenceDTO competenceDTO);

    /**
     * Get the "id" competence.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CompetenceDTO> findOne(Long id);

    /**
     * Delete the "id" competence.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
