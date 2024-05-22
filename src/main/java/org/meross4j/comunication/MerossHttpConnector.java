package org.meross4j.comunication;

import org.meross4j.utils.GsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

 public final class MerossHttpConnector  extends AbstractConnector {
     private static Logger logger = LoggerFactory.getLogger(MerossHttpConnector.class);
     private final String apiBaseUrl;
     private final String email;
     private final String password;

     public MerossHttpConnector(String apiBaseUrl, String email, String password) {
         this.apiBaseUrl = apiBaseUrl;
         this.email = email;
         this.password = password;
     }

     public HttpResponse<String> responseToLogin()
             throws ExecutionException, InterruptedException {
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

     public Map<String, String> responseBodyToLogin() throws ExecutionException, InterruptedException {
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