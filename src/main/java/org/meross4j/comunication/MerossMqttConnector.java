package org.meross4j.comunication;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
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
    private static final String brokerPort="443";
     public static volatile String brokerAddress;
     public static volatile String userId;
     public static volatile String key;
     public static volatile String destinationDeviceUUID;

    /**
     * @param message the mqtt message to be published
     * @param topic the topic
     */
    public static void publishMqttMessage(String message, String topic) {
        String brokerConnectionString = "tcp://"+brokerAddress+":"+brokerPort;
        int pubQos = 1;
        try {
            MqttAsyncClient mqttAsyncClient = new MqttAsyncClient(brokerConnectionString, userId);
            MqttConnectionOptions options = new MqttConnectionOptions();
            mqttAsyncClient.setCallback(new MqttCallback() {
                @Override
                public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
                    logger.info("Disconnected. Reason:" + mqttDisconnectResponse.getReasonString());
                }

                @Override
                public void mqttErrorOccurred(MqttException e) {
                    logger.error("mqttErrorOccurred: {}", e.getMessage());
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage)  {
                    logger.info("Topic: {}", s);
                    logger.info("Message: {}", mqttMessage.toString());
                    logger.info("qos: {}", mqttMessage.getQos());
                }

                @Override
                public void deliveryComplete(IMqttToken iMqttToken) {
                    logger.info("delivery complete {}", iMqttToken.isComplete());
                }

                @Override
                public void connectComplete(boolean b, String s) {
                    logger.info("connect complete {}", s);
                    logger.info("b: {}", b);
                }

                @Override
                public void authPacketArrived(int i, MqttProperties mqttProperties) {
                    logger.info("Auth packet arrived");
                }
            });
            options.setKeepAliveInterval(30);
            mqttAsyncClient.connect(options);
            mqttAsyncClient.subscribe(topic, 0);
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(pubQos);
            mqttAsyncClient.publish(topic, mqttMessage);
            mqttAsyncClient.disconnect();
            mqttAsyncClient.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
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
        String randomString = UUID.randomUUID().toString();
        String md5hash = DigestUtils.md5Hex(randomString);
        String messageId = md5hash.toLowerCase();
        String stringToHash = messageId + key + timestamp;
        String signature = DigestUtils.md5Hex(stringToHash);
        String clientResponseTopic = buildResponseTopic();
        Map<String, Object> headerMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        headerMap.put("from",clientResponseTopic);
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
        return new Gson().toJson(dataMap);
    }

    /**
     * @return  The response topic
     */
    public static String  buildResponseTopic() {
        return "/app/" +
                MerossMqttConnector.userId +
                "-" +
                buildAppId() +
                "/subscribe";
    }

    public static String buildAppId(){
        String rndUUID = UUID.randomUUID().toString();
        String stringToHash = "API"+rndUUID;
        return DigestUtils.md5Hex(stringToHash);
    }

}

