package com.mycompany.skilltest.repository;

import com.mycompany.skilltest.domain.Test;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface TestRepositoryWithBagRelationships {
    Optional<Test> fetchBagRelationships(Optional<Test> test);

    List<Test> fetchBagRelationships(List<Test> tests);

    Page<Test> fetchBagRelationships(Page<Test> tests);
}
