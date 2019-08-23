package com.agorapulse.micronaut.snitch;

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;

/**
 * Snitch job configuration.
 *
 * Contains only the name and id.
 */
@EachProperty("snitches.jobs")
public class SnitchJobConfiguration {

    private final String name;
    private String id;

    public SnitchJobConfiguration(@Parameter String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

}
