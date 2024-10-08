package org.meross4j.command;

import org.meross4j.communication.MerossEnum;
import org.meross4j.communication.MerossMqttConnector;
import java.util.Map;

public class TogglexCommand {
    public static class turnOn implements Command {
        @Override
        public byte[] commandType(String type) {
            Map<String,Object> payload = Map.of("togglex",Map.of("onoff",1,"channel",0));
            return MerossMqttConnector.buildMqttMessage("SET", MerossEnum.Namespace.CONTROL_TOGGLEX.getValue(),payload);
        }
    }

    public static class turnOff implements Command {
        @Override
        public byte[] commandType(String type) {
            Map<String,Object> payload = Map.of("togglex",Map.of("onoff",0,"channel",0));
            return MerossMqttConnector.buildMqttMessage("SET", MerossEnum.Namespace.CONTROL_TOGGLEX.getValue(),payload);
        }
    }
}




