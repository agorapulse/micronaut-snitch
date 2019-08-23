package com.agorapulse.micronaut.snitch;

import io.micronaut.context.annotation.*;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Factory for built-in snitch services.
 */
@Factory
public class DefaultSnitchFactory {

    @EachBean(SnitchJobConfiguration.class)
    @Requires(property = "snitches.jobs")
    public SnitchService snitchService(SnitchClient client, SnitchJobConfiguration configuration) {
        return new DefaultSnitchService(client, configuration);
    }

    @Bean
    @Singleton
    @Named("default")
    @Requires(property = "snitches.id")
    public SnitchService defaultSnitchService(SnitchClient client, @Value("${snitches.id}") String id) {
        SnitchJobConfiguration configuration = new SnitchJobConfiguration("default");
        configuration.setId(id);
        return new DefaultSnitchService(client, configuration);
    }

    @Bean
    @Secondary
    @Singleton
    @Named("default")
    @Requires(missingProperty = "snitches.id")
    public SnitchService noopSnitchService() {
        return NoopSnitchService.INSTANCE;
    }

}
