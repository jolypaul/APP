package com.mycompany.skilltest.service;

import com.mycompany.skilltest.domain.*; // for static metamodels
import com.mycompany.skilltest.domain.Poste;
import com.mycompany.skilltest.repository.PosteRepository;
import com.mycompany.skilltest.service.criteria.PosteCriteria;
import com.mycompany.skilltest.service.dto.PosteDTO;
import com.mycompany.skilltest.service.mapper.PosteMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Poste} entities in the database.
 * The main input is a {@link PosteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PosteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PosteQueryService extends QueryService<Poste> {

    private static final Logger LOG = LoggerFactory.getLogger(PosteQueryService.class);

    private final PosteRepository posteRepository;

    private final PosteMapper posteMapper;

    public PosteQueryService(PosteRepository posteRepository, PosteMapper posteMapper) {
        this.posteRepository = posteRepository;
        this.posteMapper = posteMapper;
    }

    /**
     * Return a {@link Page} of {@link PosteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PosteDTO> findByCriteria(PosteCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Poste> specification = createSpecification(criteria);
        return posteRepository.fetchBagRelationships(posteRepository.findAll(specification, page)).map(posteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PosteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Poste> specification = createSpecification(criteria);
        return posteRepository.count(specification);
    }

    /**
     * Function to convert {@link PosteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Poste> createSpecification(PosteCriteria criteria) {
        Specification<Poste> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Poste_.id),
                buildStringSpecification(criteria.getIntitule(), Poste_.intitule),
                buildSpecification(criteria.getNiveauRequis(), Poste_.niveauRequis),
                buildSpecification(criteria.getCompetencesId(), root -> root.join(Poste_.competenceses, JoinType.LEFT).get(Competence_.id))
            );
        }
        return specification;
    }
}
