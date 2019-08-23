package com.agorapulse.micronaut.snitch;

/**
 * Snitch service reprorts to the snitch provider with successful or unsuccessful calls.
 */
public interface SnitchService {

    /**
     * Report successfully.
     * @return <code>true</code> if the success was reported properly
     */
    default boolean snitch() {
        return snitch(true);
    }

    /**
     * Report success or failure.
     * @param success whether there was a success or failure
     * @return <code>true</code> if the success or failure was reported properly
     */
    boolean snitch(boolean success);

}
