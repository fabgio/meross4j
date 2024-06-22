package org.meross4j.command;

import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;

public class ToggleX {
    public static class turnOn implements Command {

        /**
         * @return MqttMessage for turning the device on
         */

        @Override
        public String createCommand(String type) {
            String payload = """
                  {'togglex' {"onoff": 1, "channel": 0}}
                  """;
            return MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(), payload,MerossMqttConnector.buildResponseTopic());
        }
    }

    public static class turnOff implements Command {

        /**
         * @return MqttMessage for turning  the device off
         */
        @Override
        public String createCommand(String type) {
            String payload = """
                        {'togglex': {"onoff": 0, "channel": 0}}
                        """;
            return MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(), payload,MerossMqttConnector.buildResponseTopic());
        }
    }
}




