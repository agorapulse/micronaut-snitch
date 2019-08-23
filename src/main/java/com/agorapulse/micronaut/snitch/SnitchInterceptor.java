package com.agorapulse.micronaut.snitch;

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.qualifiers.Qualifiers;

import javax.inject.Singleton;

/**
 * Interceptor for @{@link Snitch} annotation.
 */
@Singleton
public class SnitchInterceptor implements MethodInterceptor<Object, Object> {

    private final ApplicationContext applicationContext;

    public SnitchInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> methodContext) {
        String configurationName = methodContext.getValue(Snitch.class, String.class).orElse(Snitch.DEFAULT_CONFIGURATION_NAME);
        boolean before = methodContext.getValue(Snitch.class, "before", Boolean.class).orElse(false);
        SnitchService service = applicationContext.findBean(SnitchService.class, Qualifiers.byName(configurationName))
                .orElseGet(() -> applicationContext.getBean(SnitchService.class));
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
