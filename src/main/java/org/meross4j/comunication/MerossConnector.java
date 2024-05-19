package org.meross4j.comunication;

import org.jetbrains.annotations.NotNull;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class MerossConnector extends AbstractConnector {
    private final URI apiBaseUrl;
    private final String email;
    private final String password;
    private static final String LOGIN_PATH = "v1/Auth/signIn";

    public MerossConnector(URI apiBaseUrl, String email, String password) {
        this.apiBaseUrl = apiBaseUrl;
        this.email = email;
        this.password = password;
    }
    //TODO: Menage HTTP error conditions
    private String responseToLogin()  {
        var merossConnector = new MerossConnector(apiBaseUrl, email, password);
        Map<String,String> loginMap = new HashMap<>();
        loginMap.put("email", email);
        loginMap.put("password", password);
        merossConnector.setParamsData(loginMap);
        merossConnector.authenticatedPostRequest(apiBaseUrl);
        return authenticatedPostResponse(LOGIN_PATH).body();
    }

    @Override
    public HttpRequest authenticatedPostRequest(@NotNull URI uriBuilder) {
         super.authenticatedPostRequest(uriBuilder);
         return null;
    }

    @Override
    public HttpResponse<String> authenticatedPostResponse(String... paths) {
        super.authenticatedPostResponse(paths);
        return null;
    }
}
