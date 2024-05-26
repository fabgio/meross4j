package org.meross4j.comunication;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.meross4j.record.CloudCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Objects;

/**
 * the {@link MerossHttpConnector} is the concrete implementation of the {@link AbstractHttpConnector} class and it
 * is responsible for handling the Http functionality for connecting to the Meross Cloud
 */

public final class MerossHttpConnector  extends AbstractHttpConnector {
    private final static Logger logger = LoggerFactory.getLogger(MerossHttpConnector.class);
    private final String apiBaseUrl;
    private final String email;
    private final String password;

    public MerossHttpConnector(String apiBaseUrl, String email, String password) {
        this.apiBaseUrl = apiBaseUrl;
        this.email = email;
        this.password = password;
    }

    public HttpResponse<String> getResponse(HashMap<String,String>payloadMap, String path) {
        HttpResponse<String> httpResponse = postResponse(payloadMap, apiBaseUrl,path);
        if (httpResponse.statusCode() != 200) {
            logger.error("responseToLogin request resulted in HTTP error code {}", httpResponse.statusCode());
        } else {
            return httpResponse;
        }
        return null;
    }

   public HttpResponse<String> getLoginResponse() {
        HashMap<String, String> loginMap = new HashMap<>();
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
         return Objects.requireNonNull(getResponse(loginMap, MerossConstants.LOGIN_PATH));
    }


    /**
     * @return The response body at login
     */
    public String loginResponseBody() {
       JSONObject body = new JSONObject(getLoginResponse().body());
        if (body.get("info").equals("Email unregistered")) {
            throw new IllegalArgumentException("Email unregistered");
        } else if (body.get("info").equals("Wrong password")) {
            throw new IllegalArgumentException("Wrong password");
        } else {
            return body.toString();
        }
    }

    public CloudCredentials cloudCredentials(){
        JSONObject jsonObject = new JSONObject(loginResponseBody());
        String data = jsonObject.getJSONObject("data").toString();
        return new Gson().fromJson(data, CloudCredentials.class);
    }
}