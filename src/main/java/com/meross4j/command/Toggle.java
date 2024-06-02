package com.meross4j.command;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.meross4j.comunication.MerossHttpConnector;
import org.meross4j.comunication.MerossMqttConnector;
import org.meross4j.record.Device;

import java.util.Optional;

public class Toggle {
    static MqttMessage requestTurnOn(String devName){
        String mMethod="SET";
        String mNameSpace="Namespace.CONTROL_TOGGLEX";
        String mPayload = """
                  {'togglex': {"onoff": 0, "channel": 0}}""";
        Optional<String> deviceUUID = new MerossHttpConnector().getDevices().stream()
                .filter(name->name.equals(devName))
                .map(Device::uuid)
                .findFirst();

        return new MerossMqttConnector().buildMqttMessage(mMethod,mNameSpace,mPayload,deviceUUID.get());
    }
}
