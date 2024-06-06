package org.meross4j.command;

import org.meross4j.comunication.MerossMqttConnector;

public class TogglexTurnOff {
    /**
     * @param devName The device name
     * @return MqttMessage for turning off the device
     */
    public String requestTurnOff(String devName){
        String method="SET";
        String nameSpace="Namespace.CONTROL_TOGGLEX";
        String payload = """
                  {'togglex': {"onoff": 0, "channel": 0}}""";
        return MerossMqttConnector.buildMqttMessage(method,nameSpace,payload);
    }
}
