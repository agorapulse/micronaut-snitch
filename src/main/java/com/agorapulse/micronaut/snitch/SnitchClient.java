package com.agorapulse.micronaut.snitch;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;

/**
 * Snitch client reports to https://deadmanssnitch.com/.
 */
@Client("${snitches.url:`https://nosnch.in`}")
public interface SnitchClient {

    @Get("/{id}")
    String snitch(String id, @QueryValue("s") String success);

}
