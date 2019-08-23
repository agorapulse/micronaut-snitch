package com.agorapulse.micronaut.snitch;

/**
 * No-operation implementation of the service does nothing by default.
 */
public enum NoopSnitchService implements SnitchService {

    INSTANCE;

    /**
     * @param success whether to report succes or not
     * @return always true
     */
    @Override
    public boolean snitch(boolean success) {
        return true;
    }

}
