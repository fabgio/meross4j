package org.meross4j.command;

import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;
import java.util.Collections;
import java.util.Map;

public class ToggleX {
    public static class turnOn implements Command {
        @Override
        public byte[] createCommandType(String type) {
            Map<String,Object> payload = Map.of("togglex",Map.of("onoff",1,"channel",0));
            return MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),payload);
        }
    }

    public static class turnOff implements Command {
        @Override
        public byte[] createCommandType(String type) {
            Map<String,Object> payload = Map.of("togglex",Map.of("onoff",0,"channel",0));
            return MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),payload);
        }
    }
    public static class abilities implements Command {
        @Override
        public byte[] createCommandType(String type) {
            return MerossMqttConnector.buildMqttMessage("GET", MerossConstants.Namespace.SYSTEM_ABILITY.getValue(), Collections.emptyMap());
        }
    }
}




