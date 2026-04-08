package com.mycompany.skilltest.repository;

import com.mycompany.skilltest.domain.Evaluation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Evaluation entity.
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long>, JpaSpecificationExecutor<Evaluation> {
    default Optional<Evaluation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Evaluation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Evaluation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select evaluation from Evaluation evaluation left join fetch evaluation.employee left join fetch evaluation.test left join fetch evaluation.manager",
        countQuery = "select count(evaluation) from Evaluation evaluation"
    )
    Page<Evaluation> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select evaluation from Evaluation evaluation left join fetch evaluation.employee left join fetch evaluation.test left join fetch evaluation.manager"
    )
    List<Evaluation> findAllWithToOneRelationships();

    @Query(
        "select evaluation from Evaluation evaluation left join fetch evaluation.employee left join fetch evaluation.test left join fetch evaluation.manager where evaluation.id =:id"
    )
    Optional<Evaluation> findOneWithToOneRelationships(@Param("id") Long id);
}
