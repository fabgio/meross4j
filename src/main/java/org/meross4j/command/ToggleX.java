package org.meross4j.command;

import com.google.gson.Gson;
import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;

import java.util.HashMap;
import java.util.Map;

public class ToggleX {
    public static class turnOn implements Command {
        @Override
        public void createCommandType(String type) {
            Map<String, Integer> dataMap = new HashMap<>();
            dataMap.put("onoff",1);
            dataMap.put("channel",0);
            Map<String, Object> togglexMap = new HashMap<>();
            togglexMap.put("togglex",dataMap);
            String payload=new Gson().toJson(togglexMap);
            MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),
                    payload, MerossMqttConnector.buildResponseTopic());
        }
    }

    public static class turnOff implements Command {
        @Override
        public void createCommandType(String type) {
            Map<String, Integer> dataMap = new HashMap<>();
            dataMap.put("onoff",0);
            dataMap.put("channel",0);
            Map<String, Object> togglexMap = new HashMap<>();
            togglexMap.put("togglex",dataMap);
            String payload=new Gson().toJson(togglexMap);
            MerossMqttConnector.buildMqttMessage("SET", MerossConstants.Namespace.CONTROL_TOGGLEX.getValue(),
                    payload, MerossMqttConnector.buildResponseTopic());
        }
    }
}




