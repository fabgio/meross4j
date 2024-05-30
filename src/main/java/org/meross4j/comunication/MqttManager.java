package org.meross4j.comunication;

import org.eclipse.paho.mqttv5.common.MqttMessage;

public interface MqttManager {
    void publishMessage(MqttMessage message, String topic);
    public MqttMessage buildMessage(String method, String namespace,byte[] payload, String destinationDeviceUUID);
}
