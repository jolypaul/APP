package com.mycompany.skilltest.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

public class TestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Test getTestSample1() {
        return new Test().id(1L).titre("titre1").duree(1);
    }

    public static Test getTestSample2() {
        return new Test().id(2L).titre("titre2").duree(2);
    }

    public static Test getTestRandomSampleGenerator() {
        return new Test().id(longCount.incrementAndGet()).titre(UUID.randomUUID().toString()).duree(intCount.incrementAndGet());
    }
}
