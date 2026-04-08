package com.mycompany.skilltest.service;

import com.mycompany.skilltest.domain.*; // for static metamodels
import com.mycompany.skilltest.domain.Evaluation;
import com.mycompany.skilltest.repository.EvaluationRepository;
import com.mycompany.skilltest.service.criteria.EvaluationCriteria;
import com.mycompany.skilltest.service.dto.EvaluationDTO;
import com.mycompany.skilltest.service.mapper.EvaluationMapper;
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
 * Service for executing complex queries for {@link Evaluation} entities in the database.
 * The main input is a {@link EvaluationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EvaluationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EvaluationQueryService extends QueryService<Evaluation> {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationQueryService.class);

    private final EvaluationRepository evaluationRepository;

    private final EvaluationMapper evaluationMapper;

    public EvaluationQueryService(EvaluationRepository evaluationRepository, EvaluationMapper evaluationMapper) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
    }

    /**
     * Return a {@link Page} of {@link EvaluationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EvaluationDTO> findByCriteria(EvaluationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Evaluation> specification = createSpecification(criteria);
        return evaluationRepository.findAll(specification, page).map(evaluationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EvaluationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Evaluation> specification = createSpecification(criteria);
        return evaluationRepository.count(specification);
    }

    /**
     * Function to convert {@link EvaluationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Evaluation> createSpecification(EvaluationCriteria criteria) {
        Specification<Evaluation> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Evaluation_.id),
                buildRangeSpecification(criteria.getDateEvaluation(), Evaluation_.dateEvaluation),
                buildSpecification(criteria.getStatus(), Evaluation_.status),
                buildSpecification(criteria.getMode(), Evaluation_.mode),
                buildRangeSpecification(criteria.getScoreTotal(), Evaluation_.scoreTotal),
                buildSpecification(criteria.getStatut(), Evaluation_.statut),
                buildSpecification(criteria.getEmployeeId(), root -> root.join(Evaluation_.employee, JoinType.LEFT).get(Employee_.id)),
                buildSpecification(criteria.getTestId(), root -> root.join(Evaluation_.test, JoinType.LEFT).get(Test_.id)),
                buildSpecification(criteria.getManagerId(), root -> root.join(Evaluation_.manager, JoinType.LEFT).get(Employee_.id))
            );
        }
        return specification;
    }
}
