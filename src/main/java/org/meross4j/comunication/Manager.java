package org.meross4j.comunication;

import org.eclipse.paho.mqttv5.common.MqttMessage;

public interface Manager {
    void publishMqttMessage(MerossHttpConnector merossHttpConnector, MqttMessage message, String topic);
    MqttMessage buildMqttMessage(MerossHttpConnector merossHttpConnector, String method, String namespace,
                                 String payload, String destinationDeviceUUID);
}
