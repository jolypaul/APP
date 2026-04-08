package com.mycompany.skilltest.repository;

import com.mycompany.skilltest.domain.Reponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reponse entity.
 */
@Repository
public interface ReponseRepository extends JpaRepository<Reponse, Long>, JpaSpecificationExecutor<Reponse> {
    default Optional<Reponse> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Reponse> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Reponse> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select reponse from Reponse reponse left join fetch reponse.question",
        countQuery = "select count(reponse) from Reponse reponse"
    )
    Page<Reponse> findAllWithToOneRelationships(Pageable pageable);

    @Query("select reponse from Reponse reponse left join fetch reponse.question")
    List<Reponse> findAllWithToOneRelationships();

    @Query("select reponse from Reponse reponse left join fetch reponse.question where reponse.id =:id")
    Optional<Reponse> findOneWithToOneRelationships(@Param("id") Long id);

    List<Reponse> findByEvaluationId(Long evaluationId);

    Optional<Reponse> findOneByEvaluationIdAndQuestionId(Long evaluationId, Long questionId);
}
