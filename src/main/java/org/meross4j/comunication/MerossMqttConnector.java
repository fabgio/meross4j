package org.meross4j.comunication;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.exceptions.Mqtt5SubAckException;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
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
    private static volatile String clientId;
    private static volatile String key;
    private static volatile String destinationDeviceUUID;

    /**
     * @param message the mqtt message to be published
     * @param requestTopic the request topic
     */
    public static void publishMqttMessage(String message, String requestTopic)  {
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
                .payload(message.getBytes(StandardCharsets.UTF_8))
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

        var pubAck = client.publish(publishMessage);
        logger.debug("connAck: {}", connAck);
        try {
            logger.debug("pubAck: {} payload {}", pubAck, publishMessage.getPayload().get());
            var subAck = client.subscribe(subscribeMessage);
            logger.debug("subAck: {} subscriptions: {}",subAck,subscribeMessage.getSubscriptions());
        }catch (Mqtt5SubAckException e) {
            logger.error("subscription(s) failed: {}", e.getMqttMessage().getReasonCodes());
        }finally {
            client.disconnect();
        }
    }

    /**
     * @param method                The method
     * @param namespace             The namespace
     * @param payload               The payload
     * @return A Mqtt message
     */
    public static String buildMqttMessage(String method, String namespace,
                                          String payload) {
        long timestamp = Instant.now().toEpochMilli();
        String randomString =  UUID.randomUUID().toString();
        String md5hash = DigestUtils.md5Hex(randomString);
        String messageId = md5hash.toLowerCase();
        String stringToHash = messageId + key + timestamp;
        String signature = DigestUtils.md5Hex(stringToHash).toLowerCase();
        Map<String, Object> headerMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
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
        dataMap.put("payload",payload);
        return Base64.getEncoder().encodeToString(new Gson().toJson(dataMap).getBytes(StandardCharsets.UTF_8));
    }



    /**
     * @return The client user topic
     */
    public static String buildClientUserTopic(){
        return "/app/"+getUserId()+"/subscribe";
    }


    public static String buildAppId(){
        String randomString = "API"+UUID.randomUUID();
        String encodedString = StandardCharsets.UTF_8.encode(randomString).toString();
        return DigestUtils.md5Hex(encodedString);
    }
    /** App command
     * @return The response topic
     */
    public static String buildClientResponseTopic() {
        return "/app/"+getUserId()+"-"+buildAppId()+"/subscribe";
    }

    public static String buildClientId(){
        return "app:"+buildAppId();
    }

    /** App command
     * @param deviceUUID The device UUID
     * @return The publish  topic
     */
    // topic to be published?
    public static String buildDeviceRequestTopic(String deviceUUID) {
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

