package org.meross4j.comunication;

import net.moznion.uribuildertiny.URIBuilderTiny;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

/**@author Giovanni Fabiani - initial contribution
 * The  HttpConnector class is responsible for connecting to Meross host
 */
public class MerossHttpclient {
    private static final long S_TIMEOUT = 30;
    private static String apiBaseUrl;
    private static String email;
    private static String password;
    private final HttpClient client = HttpClient.newBuilder().build();
    Logger logger = LoggerFactory.getLogger(MerossHttpclient.class);

    /**
     * @param apiBaseUrl the Meross URL
     * @param email      the user's email
     * @param password   the user's password
     */
    public MerossHttpclient(String apiBaseUrl, String email, String password) {
        this.apiBaseUrl = apiBaseUrl;
        this.email = email;
        this.password = password;
    }

    private static URI getApiBuilder(String... paths) {
        return new URIBuilderTiny(apiBaseUrl).appendPaths(paths).build();
    }
    private static HttpRequest authenticatedPostRequest(@NotNull URI uriBuilder) {
        return HttpRequest.newBuilder().uri(URI.create(uriBuilder.toString())).timeout(Duration.ofSeconds(MerossHttpclient.S_TIMEOUT))
                .header("Content-Type", "application/json").timeout(Duration.ofSeconds(MerossHttpclient.S_TIMEOUT)).POST()
    }
}


