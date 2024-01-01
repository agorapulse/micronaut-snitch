/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2024 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.micronaut.snitch;

import io.micronaut.context.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * Factory for built-in snitch services.
 */
@Factory
public class DefaultSnitchFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSnitchFactory.class);

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
    public SnitchService noopSnitchService(@Value("${snitches.disabled:false}") boolean snitchesDisabled) {
        if (!snitchesDisabled) {
            LOGGER.warn("Micronaut Snitch is not configured! Please set snitches.id configuration property or set snitches.disabled to true to ignore this warning.");
        }

        return NoopSnitchService.INSTANCE;
    }

}
