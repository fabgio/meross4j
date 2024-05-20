package org.meross4j.comunication;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
  * @author Giovanni Fabiani - initial contribution
  *
  * The AbstractConnector contanins the fundamental APIs  for connecting to the Meross host. It has
  * to be extended by a concrete class
 **/
 abstract class AbstractConnector  {
    private static Logger logger = LoggerFactory.getLogger(AbstractConnector.class);
    private static final String CONSTANT_STRING = "23x17ahWarFH6w29";
    private static final String DEFAULT_APP_TYPE = "MerossIOT";
    private static final String MODULE_VERSION = "0.0.0";
    private final HttpClient client = HttpClient.newBuilder().build();
    private String token;

    /**
     * @param uri The URI
     * @return HttpRequest
     */
     HttpResponse postResponse(Map<String, String> paramsData, @NotNull String uri, String path)
            throws ExecutionException, InterruptedException {
        String dataToSign;
        String encodedParams;
        String authorizationValue;
        String nonce = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        long timestamp = Instant.now().toEpochMilli();
        var dataToSignBuilder = new StringBuilder();
        if (paramsData != null) {
            encodedParams = encodeParams(paramsData);
            dataToSignBuilder.append(CONSTANT_STRING).append(timestamp).append(nonce).append(encodedParams);
        } else {
            throw new NullPointerException("Parameter data is null");
        }
        dataToSign = dataToSignBuilder.toString();
        String md5hash = DigestUtils.md5Hex(dataToSign);
        Map<String,String> payloadMap = new HashMap<>();
        payloadMap.put("params",encodedParams);
        payloadMap.put("sign", md5hash);
        payloadMap.put("timestamp", String.valueOf(timestamp));
        payloadMap.put("nonce", nonce);
        String payload = new Gson().toJson(payloadMap);
        if (token != null) {
            authorizationValue = "Basic " + token;
        } else {
            authorizationValue = "Basic";
        }
        var uriBuilder = new StringBuilder(uri).append(path);
        HttpRequest postRequest= HttpRequest.newBuilder()
                .uri(URI.create(uriBuilder.toString()))
                .header("Authorization", authorizationValue)
                .header("AppVersion", "0.0.0")
                .header("vender", "meross")
                .header("AppType", DEFAULT_APP_TYPE)
                .header("AppLanguage", "EN")
                .header("User-Agent", DEFAULT_APP_TYPE +"/"+ MODULE_VERSION)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        return client.sendAsync(postRequest, HttpResponse.BodyHandlers.ofString()).get();
    }

    private static String encodeParams(Map<String, String> paramsData) {
        return Base64.getEncoder().encodeToString(new Gson().toJson(paramsData).getBytes());
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}



