package com.mycompany.skilltest.service;

import com.mycompany.skilltest.domain.*; // for static metamodels
import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.repository.TestRepository;
import com.mycompany.skilltest.service.criteria.TestCriteria;
import com.mycompany.skilltest.service.dto.TestDTO;
import com.mycompany.skilltest.service.mapper.TestMapper;
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
 * Service for executing complex queries for {@link Test} entities in the database.
 * The main input is a {@link TestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TestQueryService extends QueryService<Test> {

    private static final Logger LOG = LoggerFactory.getLogger(TestQueryService.class);

    private final TestRepository testRepository;

    private final TestMapper testMapper;

    public TestQueryService(TestRepository testRepository, TestMapper testMapper) {
        this.testRepository = testRepository;
        this.testMapper = testMapper;
    }

    /**
     * Return a {@link Page} of {@link TestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TestDTO> findByCriteria(TestCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Test> specification = createSpecification(criteria);
        return testRepository.fetchBagRelationships(testRepository.findAll(specification, page)).map(testMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TestCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Test> specification = createSpecification(criteria);
        return testRepository.count(specification);
    }

    /**
     * Function to convert {@link TestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Test> createSpecification(TestCriteria criteria) {
        Specification<Test> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Test_.id),
                buildStringSpecification(criteria.getTitre(), Test_.titre),
                buildSpecification(criteria.getMode(), Test_.mode),
                buildRangeSpecification(criteria.getDuree(), Test_.duree),
                buildRangeSpecification(criteria.getDateCreation(), Test_.dateCreation),
                buildSpecification(criteria.getActif(), Test_.actif),
                buildSpecification(criteria.getCompetencesId(), root -> root.join(Test_.competenceses, JoinType.LEFT).get(Competence_.id))
            );
        }
        return specification;
    }
}
