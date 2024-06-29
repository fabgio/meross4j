package org.meross4j.comunication;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.exceptions.Mqtt3SubAckException;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe;
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
        String hashedPassword = DigestUtils.md5Hex(userId + key);
        logger.debug("hashedPassword: {}", hashedPassword);
        logger.debug("clientId: {}", clientId);
        Mqtt3BlockingClient client = Mqtt3Client.builder()
                .identifier(clientId)
                .serverHost(brokerAddress)
                .serverPort(SECURE_WEB_SOCKET_PORT)
                .sslWithDefaultConfig()
                .buildBlocking();

        Mqtt3Publish publishMessage = Mqtt3Publish.builder()
                .topic(requestTopic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(message.getBytes(StandardCharsets.UTF_8))
                .build();

        Mqtt3Subscribe subscribeMessage = Mqtt3Subscribe.builder()
                .topicFilter(buildClientResponseTopic())
                .qos(MqttQos.AT_LEAST_ONCE)
                .build();

        var connAck = client
                .connectWith()
                .keepAlive(30)
                .cleanSession(false)
                .simpleAuth()
                .username(userId)
                .password(hashedPassword.getBytes())
                .applySimpleAuth()
                .willPublish(publishMessage)
                .send();
        logger.debug("published message: {}", publishMessage);
        logger.debug("connAck: {}", connAck);
        try {
            var subAck = client.subscribe(subscribeMessage);
            logger.debug("subAck 0: {}", subAck.getReturnCodes().get(0));
            logger.debug("SubAck size: {}", subAck.getReturnCodes().size());
            logger.debug("SubAck type: {}", subAck.getType());
            client.disconnect();
        }catch (Mqtt3SubAckException e) {
            e.getCause();
        }
    }

    /**
    
     * @param method                The method
     * @param namespace             The namespace
     * @param payload               The payload
     * @return a Mqtt message
     */
    public static String buildMqttMessage(String method, String namespace,
                                          String payload) {
        long timestamp = Instant.now().toEpochMilli();
        String randomString =  UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
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
     * @return  The response topic
     */
    public static String buildClientResponseTopic() {//ok
        return "/app/" +
                getUserId()+
                "-" +
                buildAppId()+
                "/subscribe";
    }

    /**
     * @return  The publish  topic
     */
    public static String buildDeviceRequestTopic(String destinationDeviceUUID) {
        //uncertain
        return "/appliance/"+
                destinationDeviceUUID+
                "/subscribe";
    }

    public static String buildAppId(){//MD5 hashed or not
        return  "API"+UUID.randomUUID();
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

    public static  String getUserId() {
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

