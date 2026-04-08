package com.mycompany.skilltest.service;

import com.mycompany.skilltest.service.dto.TestDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.skilltest.domain.Test}.
 */
public interface TestService {
    /**
     * Save a test.
     *
     * @param testDTO the entity to save.
     * @return the persisted entity.
     */
    TestDTO save(TestDTO testDTO);

    /**
     * Updates a test.
     *
     * @param testDTO the entity to update.
     * @return the persisted entity.
     */
    TestDTO update(TestDTO testDTO);

    /**
     * Partially updates a test.
     *
     * @param testDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TestDTO> partialUpdate(TestDTO testDTO);

    /**
     * Get all the tests with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TestDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" test.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TestDTO> findOne(Long id);

    /**
     * Delete the "id" test.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
