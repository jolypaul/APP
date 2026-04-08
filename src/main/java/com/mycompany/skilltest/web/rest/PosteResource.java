package com.mycompany.skilltest.web.rest;

import com.mycompany.skilltest.repository.PosteRepository;
import com.mycompany.skilltest.service.PosteQueryService;
import com.mycompany.skilltest.service.PosteService;
import com.mycompany.skilltest.service.criteria.PosteCriteria;
import com.mycompany.skilltest.service.dto.PosteDTO;
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
 * REST controller for managing {@link com.mycompany.skilltest.domain.Poste}.
 */
@RestController
@RequestMapping("/api/postes")
public class PosteResource {

    private static final Logger LOG = LoggerFactory.getLogger(PosteResource.class);

    private static final String ENTITY_NAME = "poste";

    @Value("${jhipster.clientApp.name:skillTestApp}")
    private String applicationName;

    private final PosteService posteService;

    private final PosteRepository posteRepository;

    private final PosteQueryService posteQueryService;

    public PosteResource(PosteService posteService, PosteRepository posteRepository, PosteQueryService posteQueryService) {
        this.posteService = posteService;
        this.posteRepository = posteRepository;
        this.posteQueryService = posteQueryService;
    }

    /**
     * {@code POST  /postes} : Create a new poste.
     *
     * @param posteDTO the posteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new posteDTO, or with status {@code 400 (Bad Request)} if the poste has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PosteDTO> createPoste(@Valid @RequestBody PosteDTO posteDTO) throws URISyntaxException {
        LOG.debug("REST request to save Poste : {}", posteDTO);
        if (posteDTO.getId() != null) {
            throw new BadRequestAlertException("A new poste cannot already have an ID", ENTITY_NAME, "idexists");
        }
        posteDTO = posteService.save(posteDTO);
        return ResponseEntity.created(new URI("/api/postes/" + posteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, posteDTO.getId().toString()))
            .body(posteDTO);
    }

    /**
     * {@code PUT  /postes/:id} : Updates an existing poste.
     *
     * @param id the id of the posteDTO to save.
     * @param posteDTO the posteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated posteDTO,
     * or with status {@code 400 (Bad Request)} if the posteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the posteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PosteDTO> updatePoste(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PosteDTO posteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Poste : {}, {}", id, posteDTO);
        if (posteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, posteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!posteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        posteDTO = posteService.update(posteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, posteDTO.getId().toString()))
            .body(posteDTO);
    }

    /**
     * {@code PATCH  /postes/:id} : Partial updates given fields of an existing poste, field will ignore if it is null
     *
     * @param id the id of the posteDTO to save.
     * @param posteDTO the posteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated posteDTO,
     * or with status {@code 400 (Bad Request)} if the posteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the posteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the posteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PosteDTO> partialUpdatePoste(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PosteDTO posteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Poste partially : {}, {}", id, posteDTO);
        if (posteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, posteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!posteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PosteDTO> result = posteService.partialUpdate(posteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, posteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /postes} : get all the Postes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Postes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PosteDTO>> getAllPostes(
        PosteCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Postes by criteria: {}", criteria);

        Page<PosteDTO> page = posteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /postes/count} : count all the postes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPostes(PosteCriteria criteria) {
        LOG.debug("REST request to count Postes by criteria: {}", criteria);
        return ResponseEntity.ok().body(posteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /postes/:id} : get the "id" poste.
     *
     * @param id the id of the posteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the posteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PosteDTO> getPoste(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Poste : {}", id);
        Optional<PosteDTO> posteDTO = posteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(posteDTO);
    }

    /**
     * {@code DELETE  /postes/:id} : delete the "id" poste.
     *
     * @param id the id of the posteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoste(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Poste : {}", id);
        posteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
