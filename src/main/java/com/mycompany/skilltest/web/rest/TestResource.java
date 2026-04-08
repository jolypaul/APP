package com.mycompany.skilltest.web.rest;

import com.mycompany.skilltest.repository.TestRepository;
import com.mycompany.skilltest.service.TestQueryService;
import com.mycompany.skilltest.service.TestService;
import com.mycompany.skilltest.service.criteria.TestCriteria;
import com.mycompany.skilltest.service.dto.TestDTO;
import com.mycompany.skilltest.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.skilltest.domain.Test}.
 */
@RestController
@RequestMapping("/api/tests")
public class TestResource {

    private static final Logger LOG = LoggerFactory.getLogger(TestResource.class);

    private static final String ENTITY_NAME = "test";

    @Value("${jhipster.clientApp.name:skillTestApp}")
    private String applicationName;

    private final TestService testService;

    private final TestRepository testRepository;

    private final TestQueryService testQueryService;

    public TestResource(TestService testService, TestRepository testRepository, TestQueryService testQueryService) {
        this.testService = testService;
        this.testRepository = testRepository;
        this.testQueryService = testQueryService;
    }

    /**
     * {@code POST  /tests} : Create a new test.
     *
     * @param testDTO the testDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new testDTO, or with status {@code 400 (Bad Request)} if the test has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TestDTO> createTest(@Valid @RequestBody TestDTO testDTO) throws URISyntaxException {
        LOG.debug("REST request to save Test : {}", testDTO);
        if (testDTO.getId() != null) {
            throw new BadRequestAlertException("A new test cannot already have an ID", ENTITY_NAME, "idexists");
        }
        testDTO = testService.save(testDTO);
        return ResponseEntity.created(new URI("/api/tests/" + testDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, testDTO.getId().toString()))
            .body(testDTO);
    }

    /**
     * {@code PUT  /tests/:id} : Updates an existing test.
     *
     * @param id the id of the testDTO to save.
     * @param testDTO the testDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testDTO,
     * or with status {@code 400 (Bad Request)} if the testDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the testDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TestDTO> updateTest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TestDTO testDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Test : {}, {}", id, testDTO);
        if (testDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!testRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        testDTO = testService.update(testDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, testDTO.getId().toString()))
            .body(testDTO);
    }

    /**
     * {@code PATCH  /tests/:id} : Partial updates given fields of an existing test, field will ignore if it is null
     *
     * @param id the id of the testDTO to save.
     * @param testDTO the testDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testDTO,
     * or with status {@code 400 (Bad Request)} if the testDTO is not valid,
     * or with status {@code 404 (Not Found)} if the testDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the testDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TestDTO> partialUpdateTest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TestDTO testDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Test partially : {}, {}", id, testDTO);
        if (testDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!testRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TestDTO> result = testService.partialUpdate(testDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, testDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tests} : get all the Tests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Tests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TestDTO>> getAllTests(
        TestCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Tests by criteria: {}", criteria);

        Page<TestDTO> page = testQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tests/count} : count all the tests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTests(TestCriteria criteria) {
        LOG.debug("REST request to count Tests by criteria: {}", criteria);
        return ResponseEntity.ok().body(testQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tests/:id} : get the "id" test.
     *
     * @param id the id of the testDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the testDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TestDTO> getTest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Test : {}", id);
        Optional<TestDTO> testDTO = testService.findOne(id);
        return ResponseUtil.wrapOrNotFound(testDTO);
    }

    /**
     * {@code DELETE  /tests/:id} : delete the "id" test.
     *
     * @param id the id of the testDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Test : {}", id);
        testService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
