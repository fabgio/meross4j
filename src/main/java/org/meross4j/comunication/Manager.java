package org.meross4j.comunication;

import org.eclipse.paho.mqttv5.common.MqttMessage;

public interface Manager {
    void publishMessage(String brokerHost, String clientId, MqttMessage message, String topic);
    public MqttMessage buildMessage(String method, String namespace,byte[] payload, String destinationDeviceUUID);
}
