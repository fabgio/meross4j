package org.meross4j.comunication;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Giovanni Fabiani - initial contribution
 * The {@link MerossMqttConnector}  class contanins the fundamental APIs  for connecting to the Meross broker i.e. building
 * and publishing MQTT messages.
 **/

public final class MerossMqttConnector {
    private final static Logger logger = LoggerFactory.getLogger(MerossMqttConnector.class);
    private static final int SECURE_WEB_SOCKET_PORT = 443; //Secure WebSocket
    private static volatile String brokerAddress;
    private static volatile String userId;
    private static volatile String clientId = buildClientId();
    private static volatile String key;
    private static volatile String destinationDeviceUUID;

    /**
     * @param message the mqtt message to be published
     * @param requestTopic the topic
     */
    public static void  publishMqttMessage(String message, String requestTopic) {
        String hashedPassword = DigestUtils.md5Hex(userId+key);
        logger.debug("hashedPassword: {}", hashedPassword);
        logger.debug("clientId: {}", clientId);
        Mqtt3Client client = Mqtt3Client.builder()
                .identifier(clientId)
                .serverHost(brokerAddress)
                .serverPort(SECURE_WEB_SOCKET_PORT)
                .sslWithDefaultConfig()
                .build();

        Mqtt3Publish publishMessage = Mqtt3Publish.builder()
                .topic(requestTopic)
                .payload(message.getBytes(StandardCharsets.UTF_8))
                .build();
                 client.toBlocking()
                .connectWith()
                .keepAlive(30)
                .cleanSession(false)
                .simpleAuth()
                .username(userId)
                .password(hashedPassword.getBytes())
                .applySimpleAuth()
                .willPublish(publishMessage)
                .send();
                logger.debug("Published message: {}", publishMessage);
    }

    /**
    
     * @param method                The method
     * @param namespace             The namespace
     * @param payload               The payload
     * @return a Mqtt message
     */
    public static String buildMqttMessage(String method, String namespace,
                                          String payload, String responseTopic) {
        long timestamp = Instant.now().toEpochMilli();
        String randomString = UUID.randomUUID().toString();
        String md5hash = DigestUtils.md5Hex(randomString);
        String messageId = md5hash.toLowerCase();
        String stringToHash = messageId + key + timestamp;
        String signature = DigestUtils.md5Hex(stringToHash);
        Map<String, Object> headerMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        headerMap.put("from",responseTopic);
        headerMap.put("messageId",messageId);
        headerMap.put("method",method);
        headerMap.put("namespace",namespace);
        headerMap.put("payloadVersion",1);
        headerMap.put("sign",signature);
        headerMap.put("timestamp",timestamp);
        headerMap.put("triggerSrc","Android");
        headerMap.put("uuid",destinationDeviceUUID);
        dataMap.put("header",headerMap);
        dataMap.put("payload",payload);
        return Base64.getEncoder().encodeToString(new Gson().toJson(dataMap).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @return  The response topic
     */
    public static String  buildResponseTopic() {
        return "/app/" +
                getUserId() +
                "-" +
                buildAppId() +
                "/subscribe";
    }

    /**
     * @return  The response topic
     */
    public static String  buildDeviceRequestTopic(String destinationDeviceUUID) {
        return "/appliance/" +
                destinationDeviceUUID+
                "/subscribe";
    }

    public static String buildAppId(){
        String rndUUID = UUID.randomUUID().toString();
        String stringToHash = "API"+rndUUID;
        return DigestUtils.md5Hex(stringToHash);
    }

    public static String buildClientId(){
        return "app:"+buildAppId();
    }

    public static void setUserId(String userId) {
        MerossMqttConnector.userId = userId;
    }

    public static void setClientId(String clientId) {
        MerossMqttConnector.clientId = clientId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setBrokerAddress(String brokerAddress) {
        MerossMqttConnector.brokerAddress = brokerAddress;
    }

    public static void setKey(String key) {
        MerossMqttConnector.key = key;
    }

    public static void setDestinationDeviceUUID(String destinationDeviceUUID) {
        MerossMqttConnector.destinationDeviceUUID = destinationDeviceUUID;
    }
}

