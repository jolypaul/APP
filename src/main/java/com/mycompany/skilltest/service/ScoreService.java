package com.mycompany.skilltest.service;

import com.mycompany.skilltest.service.dto.ScoreDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.skilltest.domain.Score}.
 */
public interface ScoreService {
    /**
     * Save a score.
     *
     * @param scoreDTO the entity to save.
     * @return the persisted entity.
     */
    ScoreDTO save(ScoreDTO scoreDTO);

    /**
     * Updates a score.
     *
     * @param scoreDTO the entity to update.
     * @return the persisted entity.
     */
    ScoreDTO update(ScoreDTO scoreDTO);

    /**
     * Partially updates a score.
     *
     * @param scoreDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ScoreDTO> partialUpdate(ScoreDTO scoreDTO);

    /**
     * Get all the scores with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ScoreDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" score.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScoreDTO> findOne(Long id);

    /**
     * Delete the "id" score.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
