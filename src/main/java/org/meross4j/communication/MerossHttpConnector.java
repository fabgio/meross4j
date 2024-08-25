package org.meross4j.communication;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.meross4j.record.CloudCredentials;
import org.meross4j.record.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * the {@link MerossHttpConnector} is responsible for handling the Http functionality for connecting to the Meross Cloud
 */

public final class MerossHttpConnector {
    private final static Logger logger = LoggerFactory.getLogger(MerossHttpConnector.class);
    private static final String INITIAL_STRING = "23x17ahWarFH6w29";
    private static final String DEFAULT_APP_TYPE = "MerossIOT";
    private static final String MODULE_VERSION = "0.0.0";
    private static final long CONNECTION_TIMEOUT_SECONDS = 15;
    private final String apiBaseUrl;
    private final String email;
    private final String password;
    private String token;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.of(CONNECTION_TIMEOUT_SECONDS, ChronoUnit.SECONDS))
            .build();

    public MerossHttpConnector(String apiBaseUrl, String email, String password) {
        this.apiBaseUrl = apiBaseUrl;
        this.email = email;
        this.password = password;
    }

    public HttpResponse<String> response(Map<String, String> payloadMap, String path) {
        HttpResponse<String> httpResponse = postResponse(payloadMap, apiBaseUrl, path);
        if (httpResponse.statusCode() != 200) {
            logger.error("ResponseToLogin request resulted in HTTP error code {}", httpResponse.statusCode());
        } else {
            return httpResponse;
        }
        return null;
    }

    public HttpResponse<String> login() {
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
            return Objects.requireNonNull(response(loginMap, MerossEnum.HttpEndpoint.LOGIN.getValue()));
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
    public HttpResponse<String> errorCodeFreeLogin() {
        JsonElement jsonElement = JsonParser.parseString(login().body());
        int errorCode = jsonElement.getAsJsonObject().get("apiStatus").getAsInt();
        if (errorCode != MerossEnum.ErrorCode.NOT_AN_ERROR.getValue()) {
            String errorMessage = MerossEnum.ErrorCode.getMessageByStatusCode(errorCode);
            try {
                throw new IOException("Response resulted in error code" + "  "+errorCode + " with message"+ " "+ errorMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return login();
        }
    }

    /**
     * @return The user's Meross cloud Credentials
     */

    private  CloudCredentials loadCredentials(Path path) {
        CloudCredentials credentials;
        String data;
        try {
            JsonElement jsonElement = JsonParser.parseString(Files.readString(path));
            data = jsonElement.getAsJsonObject().toString();
            credentials = new Gson().fromJson(data, CloudCredentials.class);
        } catch (IOException e) {
            logger.debug("Error reading credentials from file", e);
            throw new RuntimeException(e);
        }
        return credentials;
    }

    public CloudCredentials fetchCredentials() {
        CloudCredentials credentials = CompletableFuture.supplyAsync(this::fetchCredentialsInternal).join();
        logger.info("Fetching credentials from cloud");
        saveCredentials(credentials);
        return credentials;
    }

    private CloudCredentials fetchCredentialsInternal() {
        JsonElement jsonElement = JsonParser.parseString(errorCodeFreeLogin().body());
        String data = jsonElement.getAsJsonObject().get("data").toString();
        return new Gson().fromJson(data, CloudCredentials.class);
    }

    public void saveCredentials(CloudCredentials cloudCredentials) {
        Path path = Path.of("src/main/resources/cloud_credentials.json");
        String json = new Gson().toJson(cloudCredentials);
        try {
            Files.writeString(path,json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CloudCredentials getCredentials() {
        Path path = Path.of("src", "main", "resources", "cloud_credentials.json");
        CloudCredentials credentials;
        if (Files.exists(path)) {
            credentials = loadCredentials(path);
            logger.info("Loaded credentials from: {}", path);
        } else {
            credentials = fetchCredentials();
        }
        return credentials;
    }

    public ArrayList<Device> loadDevices(Path path) {
        ArrayList<Device> devices;
        String data;
        try {
            JsonElement jsonElement = JsonParser.parseString(Files.readString(path));
            data = jsonElement.getAsJsonArray().toString();
            TypeToken<ArrayList<Device>> typeToken = new TypeToken<>() {};
            devices = new Gson().fromJson(data, typeToken);
        } catch (IOException e) {
            logger.debug("Error devices from file", e);
            throw new RuntimeException(e);
        }
        return devices;
    }

   public ArrayList<Device> fetchDevices() {
        ArrayList<Device> devices = CompletableFuture.supplyAsync(this::fetchDevicesInternal).join();
        saveDevices(devices);
        return devices;
    }

    public ArrayList<Device> fetchDevicesInternal(){
        String token =  fetchCredentialsInternal().token();
        setToken(token);
        var response = Objects.requireNonNull(response(Collections.emptyMap(),
                MerossEnum.HttpEndpoint.DEV_LIST.getValue()));
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String data = jsonElement.getAsJsonObject().get("data").toString();
        TypeToken<ArrayList<Device>> type = new TypeToken<>() {};
        return new Gson().fromJson(data, type);
    }

    /**
     * @return The user's device list
     */

    public ArrayList<Device> getDevices() {
        Path path = Path.of("src", "main", "resources", "devices.json");
        ArrayList<Device> devices;
        if (Files.exists(path)) {
            devices = loadDevices(path);
            logger.info("Loaded devices from {}", path);
        } else {
            devices = fetchDevices();
            saveDevices(devices);
        }
        return devices;
    }

   public void saveDevices(ArrayList<Device> devices) {
        Path path=Path.of("src/main/resources/devices.json");
        String json = new Gson().toJson(devices);
        try {
            Files.writeString(path,json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param uri The URI
     * @return The HttpResponse
     */

    public HttpResponse<String> postResponse(Map<String, String> paramsData, String uri, String path) {
        String dataToSign;
        String encodedParams;
        String authorizationValue;
        String nonce = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        long timestamp = Instant.now().toEpochMilli();
        if (paramsData != null) {
            encodedParams = encodeParams(paramsData);
        } else {
            try {
                throw new NullPointerException("Parameter data map is null");
            } catch (NullPointerException e) {
                logger.debug("Parameter data map is null");
                throw new RuntimeException(e);
            }
        }
        dataToSign = "%s%d%s%s".formatted(INITIAL_STRING, timestamp, nonce, encodedParams);
        String md5hash = DigestUtils.md5Hex(dataToSign);
        Map<String,String> payloadMap = new HashMap<>();
        payloadMap.put("params",encodedParams);
        payloadMap.put("sign", md5hash);
        payloadMap.put("timestamp", String.valueOf(timestamp));
        payloadMap.put("nonce", nonce);
        String payload = new Gson().toJson(payloadMap);
        if (token != null) {
            authorizationValue = "Basic %s".formatted(token);
        } else {
            authorizationValue = "Basic";
        }
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(uri + path))
                .header("Authorization", authorizationValue)
                .header("AppVersion", "0.0.0")
                .header("vender", "meross")
                .header("AppType", DEFAULT_APP_TYPE)
                .header("AppLanguage", "EN")
                .header("User-Agent", DEFAULT_APP_TYPE +"/"+ MODULE_VERSION)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        try {
            return client.sendAsync(postRequest, HttpResponse.BodyHandlers.ofString()).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.debug("Error while posting data", e);
            throw new RuntimeException(e);
        }
    }

    private static String encodeParams(Map<String, String> paramsData) {
        return Base64.getEncoder().encodeToString(new Gson().toJson(paramsData).getBytes());
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDevUUIDByDevName(String devName) {
        return  getDevices().stream()
                .filter(device->device.devName().equals(devName))
                .map(Device::uuid)
                .findFirst()
                .orElseThrow(()->new RuntimeException("No device found with name: "+devName));
    }

    public int getDevStatusByDevName(String devName) {
        return  getDevices().stream()
                .filter(device->device.devName().equals(devName))
                .map(Device::onlineStatus)
                .findFirst()
                .orElseThrow(()->new RuntimeException("No device found with name: "+devName));
    }

    public void logOut() {
        Objects.requireNonNull(response(Collections.emptyMap(), MerossEnum.HttpEndpoint.LOGOUT.getValue()));
    }
}


