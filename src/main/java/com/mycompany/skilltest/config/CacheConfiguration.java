package com.mycompany.skilltest.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        var ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.mycompany.skilltest.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.mycompany.skilltest.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.mycompany.skilltest.domain.User.class.getName());
            createCache(cm, com.mycompany.skilltest.domain.Authority.class.getName());
            createCache(cm, com.mycompany.skilltest.domain.User.class.getName() + ".authorities");
            createCache(cm, com.mycompany.skilltest.domain.Employee.class.getName());
            createCache(cm, com.mycompany.skilltest.domain.Poste.class.getName());
            createCache(cm, com.mycompany.skilltest.domain.Poste.class.getName() + ".competenceses");
            createCache(cm, com.mycompany.skilltest.domain.Competence.class.getName());
            createCache(cm, com.mycompany.skilltest.domain.Competence.class.getName() + ".posteses");
            createCache(cm, com.mycompany.skilltest.domain.Competence.class.getName() + ".testses");
            createCache(cm, com.mycompany.skilltest.domain.Test.class.getName());
            createCache(cm, com.mycompany.skilltest.domain.Test.class.getName() + ".competenceses");
            createCache(cm, com.mycompany.skilltest.domain.Question.class.getName());
            createCache(cm, com.mycompany.skilltest.domain.Reponse.class.getName());
            createCache(cm, com.mycompany.skilltest.domain.Evaluation.class.getName());
            createCache(cm, com.mycompany.skilltest.domain.Score.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
