package com.mycompany.skilltest.service;

import com.mycompany.skilltest.domain.*; // for static metamodels
import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.repository.CompetenceRepository;
import com.mycompany.skilltest.service.criteria.CompetenceCriteria;
import com.mycompany.skilltest.service.dto.CompetenceDTO;
import com.mycompany.skilltest.service.mapper.CompetenceMapper;
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
 * Service for executing complex queries for {@link Competence} entities in the database.
 * The main input is a {@link CompetenceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CompetenceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CompetenceQueryService extends QueryService<Competence> {

    private static final Logger LOG = LoggerFactory.getLogger(CompetenceQueryService.class);

    private final CompetenceRepository competenceRepository;

    private final CompetenceMapper competenceMapper;

    public CompetenceQueryService(CompetenceRepository competenceRepository, CompetenceMapper competenceMapper) {
        this.competenceRepository = competenceRepository;
        this.competenceMapper = competenceMapper;
    }

    /**
     * Return a {@link Page} of {@link CompetenceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CompetenceDTO> findByCriteria(CompetenceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Competence> specification = createSpecification(criteria);
        return competenceRepository.findAll(specification, page).map(competenceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CompetenceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Competence> specification = createSpecification(criteria);
        return competenceRepository.count(specification);
    }

    /**
     * Function to convert {@link CompetenceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Competence> createSpecification(CompetenceCriteria criteria) {
        Specification<Competence> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Competence_.id),
                buildStringSpecification(criteria.getNom(), Competence_.nom),
                buildStringSpecification(criteria.getCategorie(), Competence_.categorie),
                buildSpecification(criteria.getNiveauAttendu(), Competence_.niveauAttendu),
                buildSpecification(criteria.getPostesId(), root -> root.join(Competence_.posteses, JoinType.LEFT).get(Poste_.id)),
                buildSpecification(criteria.getTestsId(), root -> root.join(Competence_.testses, JoinType.LEFT).get(Test_.id))
            );
        }
        return specification;
    }
}
