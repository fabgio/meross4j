package org.meross4j.comunication;

import org.meross4j.util.GsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * the {@link MerossHttpConnector} is the concrete implementation of the {@link AbstractConnector} class and it
 * is responsible for handling the Http functionality for connecting to the Meross Cloud
 */
//TODO: Javadoc
public final class MerossHttpConnector  extends AbstractConnector {
     private final static  Logger logger = LoggerFactory.getLogger(MerossHttpConnector.class);
     private final String apiBaseUrl;
     private final String email;
     private final String password;

     public MerossHttpConnector(String apiBaseUrl, String email, String password) {
         this.apiBaseUrl = apiBaseUrl;
         this.email = email;
         this.password = password;
     }

     public HttpResponse<String> responseToLogin() {
         Map<String, String> loginMap = new HashMap<>();
         loginMap.put("email", email);
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
     public Map<String, String> responseBodyAtLogin()  {
         GsonDeserializer<Map<String, String>> gsonDeserializer = new GsonDeserializer<>();
         Map<String, String> responseBodyMap = gsonDeserializer.deserialize(Objects
                 .requireNonNull(responseToLogin()).body());
         if (responseBodyMap.containsKey("info") && responseBodyMap.get("info").equals("Email unregistered")) {
             throw new IllegalArgumentException("Email unregistered");
         } else if (responseBodyMap.containsKey("info") && responseBodyMap.get("info").equals("Wrong password")) {
             throw new IllegalArgumentException("Wrong password");
         } else {
             return responseBodyMap;
         }
     }
 }