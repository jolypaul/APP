package com.mycompany.skilltest.repository;

import com.mycompany.skilltest.domain.Test;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class TestRepositoryWithBagRelationshipsImpl implements TestRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String TESTS_PARAMETER = "tests";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Test> fetchBagRelationships(Optional<Test> test) {
        return test.map(this::fetchCompetenceses);
    }

    @Override
    public Page<Test> fetchBagRelationships(Page<Test> tests) {
        return new PageImpl<>(fetchBagRelationships(tests.getContent()), tests.getPageable(), tests.getTotalElements());
    }

    @Override
    public List<Test> fetchBagRelationships(List<Test> tests) {
        return Optional.of(tests).map(this::fetchCompetenceses).orElse(Collections.emptyList());
    }

    Test fetchCompetenceses(Test result) {
        return entityManager
            .createQuery("select test from Test test left join fetch test.competenceses where test.id = :id", Test.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Test> fetchCompetenceses(List<Test> tests) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, tests.size()).forEach(index -> order.put(tests.get(index).getId(), index));
        List<Test> result = entityManager
            .createQuery("select test from Test test left join fetch test.competenceses where test in :tests", Test.class)
            .setParameter(TESTS_PARAMETER, tests)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
