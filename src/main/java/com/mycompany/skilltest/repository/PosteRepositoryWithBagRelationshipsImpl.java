package com.mycompany.skilltest.repository;

import com.mycompany.skilltest.domain.Poste;
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
public class PosteRepositoryWithBagRelationshipsImpl implements PosteRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String POSTES_PARAMETER = "postes";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Poste> fetchBagRelationships(Optional<Poste> poste) {
        return poste.map(this::fetchCompetenceses);
    }

    @Override
    public Page<Poste> fetchBagRelationships(Page<Poste> postes) {
        return new PageImpl<>(fetchBagRelationships(postes.getContent()), postes.getPageable(), postes.getTotalElements());
    }

    @Override
    public List<Poste> fetchBagRelationships(List<Poste> postes) {
        return Optional.of(postes).map(this::fetchCompetenceses).orElse(Collections.emptyList());
    }

    Poste fetchCompetenceses(Poste result) {
        return entityManager
            .createQuery("select poste from Poste poste left join fetch poste.competenceses where poste.id = :id", Poste.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Poste> fetchCompetenceses(List<Poste> postes) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, postes.size()).forEach(index -> order.put(postes.get(index).getId(), index));
        List<Poste> result = entityManager
            .createQuery("select poste from Poste poste left join fetch poste.competenceses where poste in :postes", Poste.class)
            .setParameter(POSTES_PARAMETER, postes)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
