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
import org.meross4j.util.MerossUtils;
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
    private final static String MQTT_PORT = "443";
    private final MerossHttpConnector merossHttpConnector = new MerossHttpConnector();

    /**
     * @param mqttMessage the Mqtt Message
     * @param topic the topic
     */

    public  void publishMqttMessage(MqttMessage mqttMessage, String topic) {
        int pubQos = 1;
        String brokerCoordinates = merossHttpConnector.getCloudCredentials().mqttDomain()+ ":" + MQTT_PORT;
        String userId = merossHttpConnector.getCloudCredentials().userId();
        try {
            MqttAsyncClient mqttAsyncClient = new MqttAsyncClient(brokerCoordinates, userId);
            MqttConnectionOptions options = new MqttConnectionOptions();
            mqttAsyncClient.setCallback(new MqttCallback() {
                @Override
                public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {

                }

                @Override
                public void mqttErrorOccurred(MqttException e) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage)  {

                }

                @Override
                public void deliveryComplete(IMqttToken iMqttToken) {

                }

                @Override
                public void connectComplete(boolean b, String s) {

                }

                @Override
                public void authPacketArrived(int i, MqttProperties mqttProperties) {
                    
                }
            });
            options.setKeepAliveInterval(30);
            mqttAsyncClient.connect(options);
            mqttAsyncClient.subscribe(topic, 0);
            mqttMessage.setQos(pubQos);
            mqttAsyncClient.publish(topic, mqttMessage);
            mqttAsyncClient.disconnect();
            mqttAsyncClient.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public MqttMessage buildMqttMessage(String method, String namespace,
                                                     String payload, String destinationDeviceUUID) {
        long timestamp = Instant.now().toEpochMilli();
        String randomString = UUID.randomUUID().toString();
        String md5hash = DigestUtils.md5Hex(randomString);
        String messageId = md5hash.toLowerCase();
        String stringToHash = messageId + merossHttpConnector.getCloudCredentials().key() + timestamp;
        String signature = DigestUtils.md5Hex(stringToHash);
        String clientResponseTopic = buildResponseTopic();
        Map<String, Object>  headerMap = new HashMap<>();
        Map<String, Object>  dataMap = new HashMap<>();
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
        String mqttMessage = new Gson().toJson(dataMap);
        return new MqttMessage(mqttMessage.getBytes());
    }

    private  String  buildResponseTopic() {
        StringBuilder topicBuilder = new StringBuilder("/app/")
                .append(merossHttpConnector.getCloudCredentials().userId())
                .append("-")
                .append(MerossUtils.buildAppId())
                .append("/subscribe");
        return topicBuilder.toString();
    }
}

