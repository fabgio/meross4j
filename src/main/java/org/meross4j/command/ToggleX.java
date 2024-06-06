package org.meross4j.command;

import org.meross4j.comunication.MerossMqttConnector;

public class ToggleX {
    public static class turnOn implements Command {

        /**
         * @return MqttMessage for turning the device on
         */

        @Override
        public String create(String devName) {
            String method = "SET";
            String nameSpace = "Namespace.CONTROL_TOGGLEX";
            String payload = """
                    {'togglex': {"onoff": 1, "channel": 0}}""";
            return MerossMqttConnector.buildMqttMessage(method, nameSpace, payload);
        }
    }

    public static class turnOff implements Command {

        /**
         * @return MqttMessage for turning  the device off
         */
        @Override
        public String create(String devName) {
            String method = "SET";
            String nameSpace = "Namespace.CONTROL_TOGGLEX";
            String payload = """
                        {'togglex': {"onoff": 0, "channel": 0}}""";
            return MerossMqttConnector.buildMqttMessage(method, nameSpace, payload);
        }
    }
}




