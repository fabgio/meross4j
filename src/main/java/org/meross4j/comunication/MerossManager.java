package org.meross4j.comunication;

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

public class MerossManager implements Manager {
    private final static Logger logger = LoggerFactory.getLogger(MerossManager.class);
    private final MerossHttpConnector merossHttpConnector;
    private final String MQTT_PORT = "443";

    public MerossManager(MerossHttpConnector merossHttpConnector) {
        this.merossHttpConnector = merossHttpConnector;
    }

    @Override
    public synchronized void publishMessage(String brokerHost, String clientId, MqttMessage message, String topic) {
        String brokerCoordinates = merossHttpConnector.getCloudCredentials().mqttDomain() + ":" + MQTT_PORT;
        String userId = merossHttpConnector.getCloudCredentials().userId();
        try {
            MqttAsyncClient mqttAsyncClient = new MqttAsyncClient(brokerCoordinates, userId);
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setKeepAliveInterval(30);
            mqttAsyncClient.connect(options);
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
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MqttMessage buildMessage(String method, String namespace,byte[] payload, String destinationDeviceUUID) {
        String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        String md5hash = DigestUtils.md5Hex(randomString);
        String messageId = md5hash.toLowerCase();
        long timestamp = Instant.now().toEpochMilli();
        String stringToHash = messageId + merossHttpConnector.getCloudCredentials().key() + timestamp;
        String signature = DigestUtils.md5Hex(stringToHash);
        Map<String, String>  headermap = new HashMap<String, String>();
        
        return null;
    }
}

