package org.meross4j.communication;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Giovanni Fabiani - initial contribution
 * The {@link MerossMqttConnector}  class contanins the fundamental APIs  for connecting to the Meross broker i.e. building
 * and publishing MQTT messages.
 **/

public final class MerossMqttConnector {
    private static final int SECURE_WEB_SOCKET_PORT = 443;
    private static final int RECEPTION_TIMEOUT_SECONDS = 15;
    private static String brokerAddress;
    private static String userId;
    private static String clientId;
    private static String key;
    private static String destinationDeviceUUID;
    private static String incomingPublishResponse;


    public static String publishMqttMessage(byte[] message, @NotNull String requestTopic) {
        String clearPassword = userId + key;
        String hashedPassword = DigestUtils.md5Hex(clearPassword);
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(clientId)
                .serverHost(brokerAddress)
                .serverPort(SECURE_WEB_SOCKET_PORT)
                .sslWithDefaultConfig()
                .buildBlocking();
                 client.connectWith()
                .keepAlive(30)
                .cleanStart(false)
                .simpleAuth()
                .username(userId)
                .password(hashedPassword.getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .send();
        Mqtt5Subscribe subscribeMessage = Mqtt5Subscribe.builder()
                .addSubscription()
                .topicFilter(buildClientUserTopic())
                .qos(MqttQos.AT_LEAST_ONCE)
                .applySubscription()
                .addSubscription()
                .topicFilter(buildClientResponseTopic())
                .qos(MqttQos.AT_LEAST_ONCE)
                .applySubscription()
                .build();
        Mqtt5Publish publishMessage = Mqtt5Publish.builder()
                .topic(requestTopic)
                .qos(MqttQos.AT_MOST_ONCE)
                .payload(message)
                .build();
        client.subscribe(subscribeMessage);
        client.publish(publishMessage);
        try (final Mqtt5BlockingClient.Mqtt5Publishes publishes = client.publishes(MqttGlobalPublishFilter.SUBSCRIBED)) {
            Optional<Mqtt5Publish> publishesResponse = publishes.receive(RECEPTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (publishesResponse.isPresent()) {
                Mqtt5Publish mqtt5PublishResponse = publishesResponse.get();
                if (mqtt5PublishResponse.getPayload().isPresent()) {
                    incomingPublishResponse = StandardCharsets.UTF_8.decode(mqtt5PublishResponse.getPayload().get())
                            .toString();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        client.disconnect();
        return incomingPublishResponse;
    }

    /**
     * @param method    The method
     * @param namespace The namespace
     * @param payload   The payload
     * @return the message
     */
    public static byte[] buildMqttMessage(String method, String namespace,
                                          Map<String, Object> payload) {
        int timestamp = Math.round(Instant.now().getEpochSecond());
        String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String messageId = DigestUtils.md5Hex(randomString.toLowerCase());
        String signatureToHash = messageId + key + timestamp;
        String signature = DigestUtils.md5Hex(signatureToHash).toLowerCase();
        Map<String, Object> headerMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
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
        return StandardCharsets.UTF_8.encode(jsonString).array();
    }

    /**
     * In general, the Meross App subscribes to this topic in order to update its state as events happen on the physical device.
     *
     * @return The client user topic
     */
    public static @NotNull String buildClientUserTopic() {
        return "/app/" + getUserId() + "/subscribe";
    }

    public static @NotNull String buildAppId() {
        String randomString = "API" + UUID.randomUUID();
        String encodedString = StandardCharsets.UTF_8.encode(randomString).toString();
        return DigestUtils.md5Hex(encodedString);
    }

    /**
     * App command.
     * It is the topic to which the Meross App subscribes. It is used by the app to receive the response to commands sent to the appliance
     *
     * @return The response topic
     */
    public static @NotNull String buildClientResponseTopic() {
        return "/app/" + getUserId() + "-" + buildAppId() + "/subscribe";
    }

    public static @NotNull String buildClientId() {
        return "app:" + buildAppId();
    }

    /**
     * App command.
     *
     * @param deviceUUID The device UUID
     * @return The topic to be published
     */

    public static @NotNull String buildDeviceRequestTopic(String deviceUUID) {
        return "/appliance/" + deviceUUID + "/subscribe";
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

