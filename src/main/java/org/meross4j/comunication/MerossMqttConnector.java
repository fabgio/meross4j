package org.meross4j.comunication;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Giovanni Fabiani - initial contribution
 * The {@link MerossMqttConnector}  class contanins the fundamental APIs  for connecting to the Meross broker i.e. building
 * and publishing MQTT messages.
 **/

public final class MerossMqttConnector {
    private final static Logger logger = LoggerFactory.getLogger(MerossMqttConnector.class);
    private static final int SECURE_WEB_SOCKET_PORT = 443; //Secure WebSocket
    private static String brokerAddress;
    private static String userId;
    private static String clientId;
    private static String key;
    private static String destinationDeviceUUID;


    /**
     * @param message      the mqtt message to be published
     * @param requestTopic the request topic
     */
    //this method becomes invisible using async
    public static void publishMqttMessage(byte[] message, @NotNull String requestTopic) {
            String clearPassword = userId + key;
            String hashedPassword = DigestUtils.md5Hex(clearPassword);
            logger.debug("hashedPassword: {}", hashedPassword);
            final Mqtt5AsyncClient client = Mqtt5Client.builder()
                    .identifier(clientId)
                    .serverHost(brokerAddress)
                    .serverPort(SECURE_WEB_SOCKET_PORT)
                    .simpleAuth()
                    .username(userId)
                    .password(hashedPassword.getBytes(StandardCharsets.UTF_8))
                    .applySimpleAuth()
                    .sslWithDefaultConfig()
                    .buildAsync();

            client.connectWith().keepAlive(30).cleanStart(false).send()
                    .thenAccept(connAck -> logger.debug("connected: {}", connAck))
                    .thenCompose(v -> client.publishWith().topic(requestTopic).payload(message).qos(MqttQos.AT_MOST_ONCE).send())
                    .thenAccept(pubAck -> logger.debug("published: {} ", pubAck))
                    .thenCompose(v -> client.subscribeWith().addSubscription().topicFilter(buildClientUserTopic()).qos(MqttQos.AT_LEAST_ONCE).applySubscription()
                            .addSubscription().topicFilter(buildClientResponseTopic()).qos(MqttQos.AT_LEAST_ONCE).applySubscription()
                            .send())
                    .thenAccept(subAck -> logger.debug("subscribed: {}", subAck))
                    .thenAccept(receiveResult -> client.publishes(MqttGlobalPublishFilter.SUBSCRIBED, response -> logger.info("receive result {}", response.getPayload().get())))
                    .thenCompose(v -> client.disconnect())
                    .thenAccept(v -> logger.debug("disconnected: {}", v));
        }



    /**
     * @param method    The method
     * @param namespace The namespace
     * @param payload   The payload
     */
    public  static byte[] buildMqttMessage(String method, String namespace,
                                        Map<String,Object> payload) {
                int timestamp = Math.round(Instant.now().getEpochSecond());
                RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
                        .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                        .withinRange('0', 'z')
                        .build();
                String randomString = randomStringGenerator.generate(16);
                String messageId = DigestUtils.md5Hex(randomString.toLowerCase()); //hashed as string
                String signatureToHash = messageId + key + timestamp;
                String signature = DigestUtils.md5Hex(signatureToHash).toLowerCase(); //hashed as string
                Map<String, Object> headerMap = Collections.synchronizedMap(new HashMap<>());
                Map<String, Object> dataMap = Collections.synchronizedMap(new HashMap<>());
                headerMap.put("from", buildClientResponseTopic());
                headerMap.put("messageId", messageId);
                headerMap.put("method", method);
                headerMap.put("namespace", namespace);
                headerMap.put("payloadVersion", 1);
                headerMap.put("sign", signature);
                headerMap.put("timestamp", timestamp);
                headerMap.put("triggerSrc", "Android");
                headerMap.put("uuid", destinationDeviceUUID);
                dataMap.put("header", headerMap);
                dataMap.put("payload", payload);
                String jsonString = new Gson().toJson(dataMap);
                logger.debug("jsonString: {}", jsonString);
                return StandardCharsets.UTF_8.encode(jsonString).array();;


    }

    /**
     * In general, the Meross App subscribes to this topic in order to update its state as events happen on the physical device.
     * @return The client user topic
     */
    public static @NotNull String buildClientUserTopic(){
        return "/app/"+getUserId()+"/subscribe";
    }

    public static @NotNull String buildAppId(){
        String randomString = "API"+UUID.randomUUID();
        String encodedString = StandardCharsets.UTF_8.encode(randomString).toString();
        return DigestUtils.md5Hex(encodedString);
    }
    /** App command.
     * It is the topic to which the Meross App subscribes. It is used by the app to receive the response to commands sent to the appliance
     * @return The response topic
     */
    public  static @NotNull String buildClientResponseTopic() {
        return "/app/"+getUserId()+"-"+buildAppId()+"/subscribe";
    }

    public static @NotNull String buildClientId(){
        return "app:"+buildAppId();
    }

    /** App command.
     * @param deviceUUID The device UUID
     * @return The topic to be published
     */
    // topic to be published. push notification
    public static @NotNull String buildDeviceRequestTopic(String deviceUUID) {
        return "/appliance/"+deviceUUID+"/subscribe";
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

