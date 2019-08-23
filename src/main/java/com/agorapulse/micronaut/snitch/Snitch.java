package com.agorapulse.micronaut.snitch;

import io.micronaut.aop.Around;
import io.micronaut.context.annotation.Type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Methods annotated with this annotation will report to the snitch service.
 *
 * The reporting happens after the excution by default but you can set <code>before</code> property to <code>true</code>
 * to execute the snitch before the metho execution.
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
@Around
@Type(SnitchInterceptor.class)
public @interface Snitch {

    /**
     * Name of the default configuration.
     */
    String DEFAULT_CONFIGURATION_NAME = "default";

    /**
     * Name of the configuration.
     * @return name of the configuration
     */
    String value() default DEFAULT_CONFIGURATION_NAME;

    /**
     * Whether the snitch should happen before the method execution.
     *
     * Defaults to <code>false</code>.
     *
     * Please, take a note that there will be only successful reports when this property is set to <code>true</code>
     *
     * @return whether the snitch should happen before the method execution.
     */
    boolean before() default false;

}
