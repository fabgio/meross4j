package org.meross4j.comunication;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.meross4j.record.CloudCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * the {@link MerossHttpConnector} is the concrete implementation of the {@link AbstractConnector} class and it
 * is responsible for handling the Http functionality for connecting to the Meross Cloud
 */

public final class MerossHttpConnector  extends AbstractConnector {
    private final static Logger logger = LoggerFactory.getLogger(MerossHttpConnector.class);
    private final String apiBaseUrl;
    private final String email;
    private final String password;

    public MerossHttpConnector(String apiBaseUrl, String email, String password) {
        this.apiBaseUrl = apiBaseUrl;
        this.email = email;
        this.password = password;
    }

    public HttpResponse<String> response() {
        Map<String, String> loginMap = new HashMap<>();
        if (email != null) {
            loginMap.put("email", email);
        } else {
            throw new IllegalArgumentException("email is null");
        }
        if (password != null) {
            loginMap.put("password", password);
        } else {
            throw new IllegalArgumentException("password is null");
        }
        loginMap.put("password", password);
        HttpResponse<String> httpResponse = postResponse(loginMap, apiBaseUrl, MerossConstants.LOGIN_PATH);
        if (httpResponse.statusCode() != 200) {
            logger.error("responseToLogin request resulted in HTTP error code {}", httpResponse.statusCode());
        } else {
            return httpResponse;
        }
        return null;
    }

    /**
     * @return The response body at login
     */
    public String body() {
       JSONObject body = new JSONObject(response().body());
        if (body.get("info").equals("Email unregistered")) {
            throw new IllegalArgumentException("Email unregistered");
        } else if (body.get("info").equals("Wrong password")) {
            throw new IllegalArgumentException("Wrong password");
        } else {
            return body.toString();
        }
    }

    public CloudCredentials cloudCredentials(){
        JSONObject jsonObject = new JSONObject(body());
        String data = jsonObject.getJSONObject("data").toString();
        return new Gson().fromJson(data, CloudCredentials.class);
    }
}