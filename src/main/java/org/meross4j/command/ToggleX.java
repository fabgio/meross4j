package org.meross4j.command;

import org.meross4j.comunication.Command;
import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;
import java.util.Map;

public class ToggleX {
    public static class turnOn implements Command {
        @Override
        public byte[] commandType(String type) {
            Map<String,Object> payload = Map.of("togglex",Map.of("onoff",1,"channel",0));
            return MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),payload);
        }
    }

    public static class turnOff implements Command {
        @Override
        public byte[] commandType(String type) {
            Map<String,Object> payload = Map.of("togglex",Map.of("onoff",0,"channel",0));
            return MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),payload);
        }
    }
}




