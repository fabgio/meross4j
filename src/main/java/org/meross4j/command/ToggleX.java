package org.meross4j.command;

import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;

public class ToggleX {
    public static class turnOn implements Command {
        @Override
        public void createCommandType(String type) {
            String payload = """
                  {'togglex' {"onoff": 1, "channel": 0}}
                  """;
            MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),
                    payload, MerossMqttConnector.buildResponseTopic());
        }
    }

    public static class turnOff implements Command {
        @Override
        public void createCommandType(String type) {
            String payload = """
                        {'togglex': {"onoff": 0, "channel": 0}}
                        """;
            MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),
                    payload, MerossMqttConnector.buildResponseTopic());
        }
    }
}




