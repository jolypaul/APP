package com.mycompany.skilltest.repository;

import com.mycompany.skilltest.domain.Score;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Score entity.
 */
@Repository
public interface ScoreRepository extends JpaRepository<Score, Long>, JpaSpecificationExecutor<Score> {
    default Optional<Score> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Score> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Score> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select score from Score score left join fetch score.competence", countQuery = "select count(score) from Score score")
    Page<Score> findAllWithToOneRelationships(Pageable pageable);

    @Query("select score from Score score left join fetch score.competence")
    List<Score> findAllWithToOneRelationships();

    @Query("select score from Score score left join fetch score.competence where score.id =:id")
    Optional<Score> findOneWithToOneRelationships(@Param("id") Long id);

    List<Score> findByEvaluationId(Long evaluationId);
}
