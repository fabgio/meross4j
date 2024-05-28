package org.meross4j.comunication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.meross4j.record.CloudCredentials;
import org.meross4j.record.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

    public HttpResponse<String> getResponse(Map<String, String> payloadMap, String path) {
        HttpResponse<String> httpResponse = postResponse(payloadMap, apiBaseUrl, path);
        if (httpResponse.statusCode() != 200) {
            logger.error("responseToLogin request resulted in HTTP error code {}", httpResponse.statusCode());
        } else {
            return httpResponse;
        }
        return null;
    }

    public  HttpResponse<String> getLoginResponse()  {
        Map<String, String> loginMap = new HashMap<>();
        if (email != null && !email.isBlank()) {
            loginMap.put("email", email);
        } else {
            try {
                throw new IllegalArgumentException("Email address is null or blank");
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
        if (password != null && !password.isBlank()) {
            loginMap.put("password", password);
        } else {
            throw new IllegalArgumentException("Password is null or blank");
        }
        try {
            loginMap.put("password", password);
            return Objects.requireNonNull(getResponse(loginMap, MerossConstants.LOGIN_PATH));
        } catch (Exception e) {
            try {
                throw new IOException("Unable to reach Meross Host");
            } catch (IOException ex) {
                logger.info("Unable to reach Meross Host", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * @return The response body at login
     */
    public String loginResponseBody()  {
        JSONObject body = new JSONObject(getLoginResponse().body());
        if (body.get("info").equals("Email unregistered")) {
            try {
                throw new IllegalArgumentException("Email address unregistered");
            } catch (IllegalArgumentException e) {
                logger.info("Email unregistered", e);
                throw new RuntimeException(e);
            }
        } else if (body.get("info").equals("Wrong password")) {
            try {
                throw new IllegalArgumentException("Wrong password");
            } catch (IllegalArgumentException e) {
                logger.info("Wrong password", e);
                throw new RuntimeException(e);
            }
        } else {
            return body.toString();
        }
    }

    /**
     * @return The user's Meross cloud Credentials
     */
    public CloudCredentials getCloudCredentials() {
        JSONObject jsonObject = new JSONObject(loginResponseBody());
        String data = jsonObject.getJSONObject("data").toString();
        return new Gson().fromJson(data, CloudCredentials.class);
    }

    public HttpResponse<String> getDevicesResponse() {
        String token =  getCloudCredentials().token();
        setToken(token);
        return Objects.requireNonNull(getResponse(Collections.emptyMap(), MerossConstants.DEV_LIST_PATH));
    }

    public String getDevicesResponseBody() {
        JSONObject body = new JSONObject(getDevicesResponse().body());
        return body.toString();
        }

    /**
     * @return The user's device list
     */
    public ArrayList<Device> getDevices(){
        JSONObject jsonObject = new JSONObject(getDevicesResponseBody());
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        String data = jsonArray.toString();
        TypeToken<ArrayList<Device>> type = new TypeToken<>() {};
        return new Gson().fromJson(data, type);
        }
     }
