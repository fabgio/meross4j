package org.meross4j.mixins;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.meross4j.comunication.MerossMqttConnector;
import org.meross4j.util.MerossUtils;

/**  This mixin is implemented by devices that support ToggleX operation, such as smart switches
 *   and smart bulbs.
 *
 */
public class ToggleX {
    /**
     * @param devName The device name
     * @return MqttMessage for turning on the device
     */
    static MqttMessage requestTurnOn(String devName){
        String method="SET";
        String nameSpace="Namespace.CONTROL_TOGGLEX";
        String payload = """
                  {'togglex': {"onoff": 1, "channel": 0}}""";
        String deviceUUID = MerossUtils.getDevUUIDByDevName(devName);
        return new MerossMqttConnector().buildMqttMessage(method,nameSpace,payload,deviceUUID);
    }

    /**
     * @param devName The device name
     * @return MqttMessage for turning off the device
     */
    static MqttMessage requestTurnOff(String devName){
        String method="SET";
        String nameSpace="Namespace.CONTROL_TOGGLEX";
        String payload = """
                  {'togglex': {"onoff": 0, "channel": 0}}""";
        String deviceUUID = MerossUtils.getDevUUIDByDevName(devName);
        return new MerossMqttConnector().buildMqttMessage(method,nameSpace,payload,deviceUUID);
    }
}
