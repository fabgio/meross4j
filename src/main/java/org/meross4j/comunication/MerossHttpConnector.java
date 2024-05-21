package org.meross4j.comunication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MerossHttpConnector  extends AbstractConnector {
    private static Logger logger = LoggerFactory.getLogger(MerossHttpConnector.class);
    private final String apiBaseUrl;
    private final String email;
    private final String password;

    public MerossHttpConnector(String apiBaseUrl, String email, String password) {
        this.apiBaseUrl = apiBaseUrl;
        this.email = email;
        this.password = password;
    }
    //TODO: Menage HTTP error conditions
    public HttpResponse<String> responseToLogin()
            throws ExecutionException, InterruptedException {
        Map<String,String> loginMap = new HashMap<>();
        loginMap.put("email", email);
        loginMap.put("password", password);
        HttpResponse<String> httpResponse = postResponse(loginMap,apiBaseUrl,MerossConstants.LOGIN_PATH);
        if (httpResponse.statusCode() != 200) {
            logger.error("responseToLogin request resulted in HTTP error code {}", httpResponse.statusCode());
        } else {
            return httpResponse;
        }
        return null;
    }

}
