package org.meross4j.mixins;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.meross4j.comunication.MerossMqttConnector;
import org.meross4j.util.MerossUtils;

public class ToggleX {
    static MqttMessage requestTurnOn(String devName){
        String method="SET";
        String nameSpace="Namespace.CONTROL_TOGGLEX";
        String payload = """
                  {'togglex': {"onoff": 0, "channel": 0}}""";
        String deviceUUID = MerossUtils.getDevUUIDFromDevName(devName);

        return new MerossMqttConnector().buildMqttMessage(method,nameSpace,payload,deviceUUID);
    }


    static MqttMessage requestTurnOff(String devName){
        String method="SET";
        String nameSpace="Namespace.CONTROL_TOGGLEX";
        String payload = """
                  {'togglex': {"onoff": 1, "channel": 0}}""";
        String deviceUUID = MerossUtils.getDevUUIDFromDevName(devName);

        return new MerossMqttConnector().buildMqttMessage(method,nameSpace,payload,deviceUUID);

    }
}
