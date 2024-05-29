package org.meross4j.comunication;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Giovanni Fabiani - initial contribution
 * The {@link AbstractMqttConnector}  class contanins the fundamental APIs  for connecting to the Meross mqtt broker.
 * It has to be extended by a concrete class
 **/

public abstract class AbstractMqttConnector {
    private final static Logger logger = LoggerFactory.getLogger(AbstractMqttConnector.class);
    public synchronized MqttAsyncClient createMqttAsyncClient(String brokerHost, String clientId) {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setKeepAliveInterval(30);
        try {
            MqttAsyncClient mqttAsyncClient = new MqttAsyncClient(brokerHost, clientId);
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
            mqttAsyncClient.connect(options);
            return mqttAsyncClient;
        } catch (MqttException e) {
            logger.debug("Unable to create  Meross Mqtt client", e);
            throw new RuntimeException(e);
        }
    }
    MqttMessage publishMessage(MqttClient mqttClient, String topic, byte[] payload, int qos)  {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload);
        mqttMessage.setQos(qos);
        try {
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            logger.debug("Unable to publish message", e);
            throw new RuntimeException(e);
        }
        return mqttMessage;
    }
}
