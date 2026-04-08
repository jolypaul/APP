package com.mycompany.skilltest.repository;

import com.mycompany.skilltest.domain.Poste;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PosteRepositoryWithBagRelationships {
    Optional<Poste> fetchBagRelationships(Optional<Poste> poste);

    List<Poste> fetchBagRelationships(List<Poste> postes);

    Page<Poste> fetchBagRelationships(Page<Poste> postes);
}
