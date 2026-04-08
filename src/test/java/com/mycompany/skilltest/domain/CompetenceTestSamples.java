package com.mycompany.skilltest.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CompetenceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Competence getCompetenceSample1() {
        return new Competence().id(1L).nom("nom1").categorie("categorie1");
    }

    public static Competence getCompetenceSample2() {
        return new Competence().id(2L).nom("nom2").categorie("categorie2");
    }

    public static Competence getCompetenceRandomSampleGenerator() {
        return new Competence().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString()).categorie(UUID.randomUUID().toString());
    }
}
