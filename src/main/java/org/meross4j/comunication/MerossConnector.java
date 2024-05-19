package org.meross4j.comunication;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MerossConnector extends Connector {
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
    private String responseToLogin() throws ExecutionException, InterruptedException {
        var merossConnector = new MerossConnector(apiBaseUrl, email, password);
        HashMap<String,String> loginMap = new HashMap<>();
        loginMap.put("email", email);
        loginMap.put("password", password);
        merossConnector.setParamsData(loginMap);
        merossConnector.authenticatedPostRequest(apiBaseUrl);
        return authenticatedPostResponse(LOGIN_PATH).body();
    }
}
