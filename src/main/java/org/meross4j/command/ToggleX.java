package org.meross4j.command;

import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToggleX {
    public static class turnOn implements Command {
        @Override
        public byte[] createCommandType(String type) {
            Map<String,Integer> elements = new LinkedHashMap<>();
            elements.put("onoff",1);
            elements.put("channel",0);
            Map<String,Object> payload = new LinkedHashMap<>();
            payload.put("togglex",elements);
            return  MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),payload);
        }
    }

    public static class turnOff implements Command {
        @Override
        public byte[] createCommandType(String type) {
            Map<String,Integer> elements = new LinkedHashMap<>();
            elements.put("onoff",0);
            elements.put("channel",0);
            Map<String,Object> payload = new LinkedHashMap<>();
            payload.put("togglex",elements);
            return MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),payload);
        }
    }
}




