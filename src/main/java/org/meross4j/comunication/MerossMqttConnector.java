package org.meross4j.comunication;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.exceptions.Mqtt5SubAckException;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
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
    private static String brokerAddress;
    private static String userId;
    private static String clientId;
    private static String key;
    private static String destinationDeviceUUID;

    /**
     * @param message the mqtt message to be published
     * @param requestTopic the request topic
     */
    public static void publishMqttMessage(byte @NotNull[] message, @NotNull String requestTopic)  {
        String clearPwd = userId + key;
        String hashedPassword = DigestUtils.md5Hex(clearPwd);
        logger.debug("hashedPassword: {}", hashedPassword);
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(clientId)
                .serverHost(brokerAddress)
                .serverPort(SECURE_WEB_SOCKET_PORT)
                .sslWithDefaultConfig()
                .buildBlocking();

       Mqtt5Publish publishMessage = Mqtt5Publish.builder()
                .topic(requestTopic)
                .qos(MqttQos.AT_MOST_ONCE) // QOS=0 python paho default value
                .payload(message)
                .build();

        Mqtt5Subscribe subscribeMessage = Mqtt5Subscribe.builder()
                .addSubscription()
                .topicFilter(buildClientUserTopic())//correct
                .qos(MqttQos.AT_LEAST_ONCE)
                .applySubscription()
                .addSubscription()
                .topicFilter(buildClientResponseTopic())
                .qos(MqttQos.AT_LEAST_ONCE)
                .applySubscription()
                .build();

        var connAck = client
                .connectWith()
                .keepAlive(30)
                .cleanStart(false)
                .simpleAuth()
                .username(userId)
                .password(hashedPassword.getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .send();
        logger.debug("connAck: {}", connAck);
        var subAck = client.subscribe(subscribeMessage);
        logger.debug("subAck: {} subscriptions: {}",subAck,subscribeMessage.getSubscriptions());
        try {
            if(publishMessage.getPayload().isPresent()) {
                ByteBuffer buffer = publishMessage.getPayload().get();
                CharBuffer charBuffer = StandardCharsets.US_ASCII.decode(buffer);
                var pubAck = client.publish(publishMessage);
                logger.debug("pubAck: {} payload: {}",pubAck,charBuffer);
            }
        }catch (Mqtt5SubAckException e) {
            logger.error("subscription(s) failed: {}", e.getMqttMessage().getReasonCodes());
        }finally {
            client.disconnect();
        }
    }

    /**
     * @param method    The method
     * @param namespace The namespace
     * @param payload   The payload
     */
    public static byte[]  buildMqttMessage(String method, String namespace,
                                        Map<String,Object> payload) {
        long timestamp = Instant.now().toEpochMilli();
        byte[] randomStringToEncode = StandardCharsets.UTF_8.encode(UUID.randomUUID().toString()).array();
        String messageId = DigestUtils.md5Hex(randomStringToEncode).toLowerCase();
        byte[] signatureToEncode = StandardCharsets.UTF_8.encode(messageId + key + timestamp).array();
        String signature = DigestUtils.md5Hex(signatureToEncode).toLowerCase();
        Map<String, Object> headerMap = new LinkedHashMap<>();
        Map<String, Object> dataMap = new LinkedHashMap<>();
        headerMap.put("from",buildClientResponseTopic());
        headerMap.put("messageId",messageId);
        headerMap.put("method",method);
        headerMap.put("namespace",namespace);
        headerMap.put("payloadVersion",1);
        headerMap.put("sign",signature);
        headerMap.put("timestamp",timestamp);
        headerMap.put("triggerSrc","Android");
        headerMap.put("uuid",destinationDeviceUUID);
        dataMap.put("header",headerMap);
        dataMap.put("payload",payload); //payload has to be a map element itself
        String jsonString = new Gson().toJson(dataMap);
        return StandardCharsets.UTF_8.encode(jsonString).array();
    }

    /**
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
    /** App command
     * @return The response topic
     */
    public  static @NotNull String buildClientResponseTopic() {
        return "/app/"+getUserId()+"-"+buildAppId()+"/subscribe";
    }

    public static @NotNull String buildClientId(){
        return "app:"+buildAppId();
    }

    /** App command
     * @param deviceUUID The device UUID
     * @return The publish  topic
     */
    // topic to be published?
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

