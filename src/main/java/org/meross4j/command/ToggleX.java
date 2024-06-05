package org.meross4j.command;

import org.meross4j.comunication.MerossMqttConnector;

/**  This mixin is implemented by devices that support ToggleX operation, such as smart switches
 *   and smart bulbs.
 *
 */
public class ToggleX {

    /**
     * @param devName The device name
     * @return MqttMessage for turning on the device
     */
    public static void requestTurnOn(String devName){
        String method="SET";
        String nameSpace="Namespace.CONTROL_TOGGLEX";
        String payload = """
                  {'togglex': {"onoff": 1, "channel": 0}}""";


    }

    /**
     * @param devName The device name
     * @return MqttMessage for turning off the device
     */
    public void requestTurnOff(String devName){
        String method="SET";
        String nameSpace="Namespace.CONTROL_TOGGLEX";
        String payload = """
                  {'togglex': {"onoff": 0, "channel": 0}}""";


    }
}
