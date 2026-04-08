package com.mycompany.skilltest.repository;

import com.mycompany.skilltest.domain.Competence;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Competence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long>, JpaSpecificationExecutor<Competence> {}
