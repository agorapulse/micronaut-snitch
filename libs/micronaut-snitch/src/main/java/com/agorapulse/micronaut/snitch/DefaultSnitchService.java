/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2022 Agorapulse.
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

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default snitch service uses the SnitchClient to report to the snitch service.
 *
 * Successful reports are send at most once per 5 minutes.
 */
public class DefaultSnitchService implements SnitchService {

    public static final String OK = "Got it, thanks!";

    private static final Logger LOGGER = LoggerFactory.getLogger(SnitchService.class);
    private static final long CALL_INTERVAL = 5L * 60 * 1000; // 5 minutes

    private final SnitchClient client;
    private final SnitchJobConfiguration configuration;

    private long lastSuccessfulCallTime;
    private long lastTooManyRequestsCallTime;

    public DefaultSnitchService(SnitchClient client, SnitchJobConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    @Override
    public boolean snitch(boolean success) {
        if (isRequestAlreadyDoneRecently(lastSuccessfulCallTime) || isRequestAlreadyDoneRecently(lastTooManyRequestsCallTime)) {
            return true;
        }
        try {
            boolean lastSuccessfulCall = OK.equals(client.snitch(configuration.getId(), success ? "0" : "1"));

            if (lastSuccessfulCall) {
                lastSuccessfulCallTime = System.currentTimeMillis();
            }

            return lastSuccessfulCall;
        } catch (HttpClientResponseException ex) {
            if (ex.getStatus() == HttpStatus.TOO_MANY_REQUESTS) {
                this.lastTooManyRequestsCallTime = System.currentTimeMillis();
            } else {
                LOGGER.warn("Exception notifying snitch " + configuration.getName(), ex);
            }
            return false;
        } catch (Exception ex) {
            LOGGER.warn("Exception notifying snitch " + configuration.getName(), ex);
            return false;
        }
    }

    private boolean isRequestAlreadyDoneRecently(long callTime) {
        return callTime > 0 && callTime + CALL_INTERVAL > System.currentTimeMillis();
    }
}
