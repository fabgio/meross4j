package org.meross4j.comunication;

import com.google.gson.Gson;
import net.moznion.uribuildertiny.URIBuilderTiny;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Arrays;
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
 abstract class AbstractConnector implements Connector {
    private static final String CONSTANT_STRING = "23x17ahWarFH6w29";
    private static final String DEFAULT_APP_TYPE = "MerossIOT";
    private static final String MODULE_VERSION = "0.0.0";
    private final HttpClient client = HttpClient.newBuilder().build();
    private Map<String, String> paramsData;
    private String token;

    /**
     * @param uriBuilder The URI builder
     * @return HttpRequest
     */
    public synchronized HttpRequest authenticatedPostRequest(@NotNull URI uriBuilder) {
        String dataToSign;
        String authorizationValue;
        String nonce = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        long timestamp = Instant.now().toEpochMilli();
        var dataToSignBuilder = new StringBuilder();
        if (paramsData != null) {
            String encodedParams = encodeParams(paramsData);
            dataToSignBuilder.append(CONSTANT_STRING).append(timestamp).append(nonce).append(encodedParams);
        } else {
            throw new NullPointerException("Params data is null");
        }
        dataToSign = dataToSignBuilder.toString();
        String md5hash = DigestUtils.md5Hex(dataToSign);
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("params", getParamsData());
        payloadMap.put("sign", md5hash);
        payloadMap.put("timestamp", String.valueOf(timestamp));
        payloadMap.put("nonce", nonce);
        String payload = new Gson().toJson(payloadMap);
        if (token != null) {
            authorizationValue = "Basic " + token;
        } else {
            authorizationValue = "Basic";
        }
        return HttpRequest.newBuilder()
                .uri(URI.create(uriBuilder.toString()))
                .header("Authorization", authorizationValue)
                .header("AppVersion", "0.0.0")
                .header("vender", "meross")
                .header("AppType", DEFAULT_APP_TYPE)
                .header("AppLanguage", "EN")
                .header("User-Agent", DEFAULT_APP_TYPE + "/" + MODULE_VERSION)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
    }

    /**
     * @param paths The path of the POST call
     * @return The response
     */
    public synchronized HttpResponse<String> authenticatedPostResponse(String... paths){
        URI builder = getApiBuilder(Arrays.toString(paths));
        HttpRequest postRequest = authenticatedPostRequest(builder);
        try {
            return client.sendAsync(postRequest, HttpResponse.BodyHandlers.ofString()).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeParams(Map<String, String> paramsData) {
        String jsonString = new Gson().toJson(paramsData);
        return Base64.getEncoder().encodeToString(jsonString.getBytes());
    }
    private static URI getApiBuilder(String apiBaseUrl, String... paths) {
        return new URIBuilderTiny(apiBaseUrl).appendPaths(paths).build();
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

   public void setParamsData(Map<String, String> paramsData) {
        this.paramsData = paramsData;
        encodeParams(paramsData);
    }

    public String getParamsData() {
        return encodeParams(paramsData);
    }
}



