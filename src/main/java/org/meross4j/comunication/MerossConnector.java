package org.meross4j.comunication;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MerossConnector extends AbstractConnector {
    private final String apiBaseUrl;
    private final String email;
    private final String password;

    public MerossConnector(String apiBaseUrl, String email, String password) {
        this.apiBaseUrl = apiBaseUrl;
        this.email = email;
        this.password = password;
    }
    //TODO: Menage HTTP error conditions
    public HttpResponse<String> responseToLogin() throws ExecutionException, InterruptedException {
        var merossConnector = new MerossConnector(apiBaseUrl, email, password);
        Map loginMap = new HashMap();
        loginMap.put("email", email);
        loginMap.put("password", password);
        merossConnector.postRequest(loginMap,apiBaseUrl,MerossConstants.LOGIN_PATH);
        return postResponse();
    }
}
