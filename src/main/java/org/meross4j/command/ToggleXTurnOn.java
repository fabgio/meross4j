package org.meross4j.command;

import org.meross4j.comunication.MerossMqttConnector;

/**  This mixin is implemented by devices that support ToggleX operation, such as smart switches
 *   and smart bulbs.
 *
 */
public class ToggleXTurnOn {

    /**
     * @param devName The device name
     * @return MqttMessage for turning on the device
     */
    public static String requestTurnOn(String devName){
        String method="SET";
        String nameSpace="Namespace.CONTROL_TOGGLEX";
        String payload = """
                  {'togglex': {"onoff": 1, "channel": 0}}""";
        return MerossMqttConnector.buildMqttMessage(method, nameSpace, payload);
    }


}
