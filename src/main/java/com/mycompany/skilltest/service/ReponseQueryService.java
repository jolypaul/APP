package com.mycompany.skilltest.service;

import com.mycompany.skilltest.domain.*; // for static metamodels
import com.mycompany.skilltest.domain.Reponse;
import com.mycompany.skilltest.repository.ReponseRepository;
import com.mycompany.skilltest.service.criteria.ReponseCriteria;
import com.mycompany.skilltest.service.dto.ReponseDTO;
import com.mycompany.skilltest.service.mapper.ReponseMapper;
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
 * Service for executing complex queries for {@link Reponse} entities in the database.
 * The main input is a {@link ReponseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ReponseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReponseQueryService extends QueryService<Reponse> {

    private static final Logger LOG = LoggerFactory.getLogger(ReponseQueryService.class);

    private final ReponseRepository reponseRepository;

    private final ReponseMapper reponseMapper;

    public ReponseQueryService(ReponseRepository reponseRepository, ReponseMapper reponseMapper) {
        this.reponseRepository = reponseRepository;
        this.reponseMapper = reponseMapper;
    }

    /**
     * Return a {@link Page} of {@link ReponseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReponseDTO> findByCriteria(ReponseCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Reponse> specification = createSpecification(criteria);
        return reponseRepository.findAll(specification, page).map(reponseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReponseCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Reponse> specification = createSpecification(criteria);
        return reponseRepository.count(specification);
    }

    /**
     * Function to convert {@link ReponseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Reponse> createSpecification(ReponseCriteria criteria) {
        Specification<Reponse> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Reponse_.id),
                buildSpecification(criteria.getEstCorrecte(), Reponse_.estCorrecte),
                buildRangeSpecification(criteria.getDateReponse(), Reponse_.dateReponse),
                buildSpecification(criteria.getQuestionId(), root -> root.join(Reponse_.question, JoinType.LEFT).get(Question_.id)),
                buildSpecification(criteria.getEvaluationId(), root -> root.join(Reponse_.evaluation, JoinType.LEFT).get(Evaluation_.id))
            );
        }
        return specification;
    }
}
