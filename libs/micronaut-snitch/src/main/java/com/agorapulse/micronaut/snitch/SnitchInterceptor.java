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

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;

/**
 * Interceptor for @{@link Snitch} annotation.
 */
@Singleton
public class SnitchInterceptor implements MethodInterceptor<Object, Object> {

    private final BeanContext beanContext;

    public SnitchInterceptor(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> methodContext) {
        String configurationName = methodContext.getValue(Snitch.class, String.class).orElse(Snitch.DEFAULT_CONFIGURATION_NAME);
        boolean before = methodContext.getValue(Snitch.class, "before", Boolean.class).orElse(false);
        SnitchService service = beanContext.findBean(SnitchService.class, Qualifiers.byName(configurationName))
                .orElseGet(() -> beanContext.getBean(SnitchService.class));
        try {
            if (before) {
                service.snitch();
            }

            Object result = methodContext.proceed();

            if (!before) {
                service.snitch();
            }

            return result;
        } catch (Throwable e) {
            if (!before) {
                service.snitch(false);
            }

            throw e;
        }
    }

}
