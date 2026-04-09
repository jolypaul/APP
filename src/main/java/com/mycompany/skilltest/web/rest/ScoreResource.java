package com.mycompany.skilltest.web.rest;

import com.mycompany.skilltest.repository.ScoreRepository;
import com.mycompany.skilltest.security.AuthoritiesConstants;
import com.mycompany.skilltest.service.ScoreQueryService;
import com.mycompany.skilltest.service.ScoreService;
import com.mycompany.skilltest.service.criteria.ScoreCriteria;
import com.mycompany.skilltest.service.dto.ScoreDTO;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.skilltest.domain.Score}.
 */
@RestController
@RequestMapping("/api/scores")
@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.RH })
public class ScoreResource {

    private static final Logger LOG = LoggerFactory.getLogger(ScoreResource.class);

    private static final String ENTITY_NAME = "score";

    @Value("${jhipster.clientApp.name:skillTestApp}")
    private String applicationName;

    private final ScoreService scoreService;

    private final ScoreRepository scoreRepository;

    private final ScoreQueryService scoreQueryService;

    public ScoreResource(ScoreService scoreService, ScoreRepository scoreRepository, ScoreQueryService scoreQueryService) {
        this.scoreService = scoreService;
        this.scoreRepository = scoreRepository;
        this.scoreQueryService = scoreQueryService;
    }

    /**
     * {@code POST  /scores} : Create a new score.
     *
     * @param scoreDTO the scoreDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scoreDTO, or with status {@code 400 (Bad Request)} if the score has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ScoreDTO> createScore(@Valid @RequestBody ScoreDTO scoreDTO) throws URISyntaxException {
        LOG.debug("REST request to save Score : {}", scoreDTO);
        if (scoreDTO.getId() != null) {
            throw new BadRequestAlertException("A new score cannot already have an ID", ENTITY_NAME, "idexists");
        }
        scoreDTO = scoreService.save(scoreDTO);
        return ResponseEntity.created(new URI("/api/scores/" + scoreDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, scoreDTO.getId().toString()))
            .body(scoreDTO);
    }

    /**
     * {@code PUT  /scores/:id} : Updates an existing score.
     *
     * @param id the id of the scoreDTO to save.
     * @param scoreDTO the scoreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scoreDTO,
     * or with status {@code 400 (Bad Request)} if the scoreDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scoreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScoreDTO> updateScore(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScoreDTO scoreDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Score : {}, {}", id, scoreDTO);
        if (scoreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scoreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        scoreDTO = scoreService.update(scoreDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scoreDTO.getId().toString()))
            .body(scoreDTO);
    }

    /**
     * {@code PATCH  /scores/:id} : Partial updates given fields of an existing score, field will ignore if it is null
     *
     * @param id the id of the scoreDTO to save.
     * @param scoreDTO the scoreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scoreDTO,
     * or with status {@code 400 (Bad Request)} if the scoreDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scoreDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scoreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ScoreDTO> partialUpdateScore(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScoreDTO scoreDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Score partially : {}, {}", id, scoreDTO);
        if (scoreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scoreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ScoreDTO> result = scoreService.partialUpdate(scoreDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scoreDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /scores} : get all the Scores.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Scores in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ScoreDTO>> getAllScores(
        ScoreCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Scores by criteria: {}", criteria);

        Page<ScoreDTO> page = scoreQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scores/count} : count all the scores.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countScores(ScoreCriteria criteria) {
        LOG.debug("REST request to count Scores by criteria: {}", criteria);
        return ResponseEntity.ok().body(scoreQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /scores/:id} : get the "id" score.
     *
     * @param id the id of the scoreDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scoreDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScoreDTO> getScore(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Score : {}", id);
        Optional<ScoreDTO> scoreDTO = scoreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scoreDTO);
    }

    /**
     * {@code DELETE  /scores/:id} : delete the "id" score.
     *
     * @param id the id of the scoreDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScore(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Score : {}", id);
        scoreService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
