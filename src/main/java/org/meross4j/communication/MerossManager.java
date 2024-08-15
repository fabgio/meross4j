package org.meross4j.communication;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.meross4j.command.Command;
import org.meross4j.factory.AbstractFactory;
import org.meross4j.factory.FactoryProvider;
import org.meross4j.record.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MerossManager {
    private final static Logger logger = LoggerFactory.getLogger(MerossManager.class);
    private final MerossHttpConnector merossHttpConnector;
    private MerossManager(MerossHttpConnector merossHttpConnector) {
        this.merossHttpConnector = merossHttpConnector;
    }

    public static MerossManager createMerossManager(MerossHttpConnector merossHttpConnector) {
        return new MerossManager(merossHttpConnector);
    }

    /**
     * Executes a command on the device and set commandMode e,g. ON or OFF and returns data
     * @param deviceName The device's name
     * @param commandType The command type
     * @param commandMode The command Mode
     * @return Response record
     */
    public Response executeCommand(String deviceName, String commandType, String commandMode) {
        String clientId = MerossMqttConnector.buildClientId();
        MerossMqttConnector.setClientId(clientId);
        String userid = merossHttpConnector.getCloudCredentials().userId();
        if (userid != null) {
            MerossMqttConnector.setUserId(userid);
        } else {
            logger.debug("userid is null");
        }
        String key = merossHttpConnector.getCloudCredentials().key();
        if (key != null) {
            MerossMqttConnector.setKey(key);
        } else {
            logger.debug("key is null");
        }
        String brokerAddress = merossHttpConnector.getCloudCredentials().mqttDomain();
        if (brokerAddress != null) {
            MerossMqttConnector.setBrokerAddress(brokerAddress);
        } else {
            logger.debug("brokerAddress is null");
        }
        String deviceUUID = merossHttpConnector.getDevUUIDByDevName(deviceName);
        if (deviceUUID != null) {
            MerossMqttConnector.setDestinationDeviceUUID(deviceUUID);
        } else {
            logger.debug("deviceUUID is null");
        }
        String requestTopic = MerossMqttConnector.buildDeviceRequestTopic(deviceUUID);
        byte[] systemAbilityMessage = MerossMqttConnector.buildMqttMessage("GET",
                MerossEnum.Namespace.SYSTEM_ABILITY.getValue(), Collections.emptyMap());
        AbstractFactory abstractFactory = FactoryProvider.getFactory(commandType);
        Command command = abstractFactory.commandMode(commandMode);
        byte[] commandMessage = command.commandType(commandType);
        int deviceStatus = merossHttpConnector.getDevStatusByDevName(deviceName);
        if (deviceStatus != MerossEnum.OnlineStatus.ONLINE.getValue()) {
            throw new RuntimeException("device status is not online");
        }
        String systemAbilityPublishesMessage = MerossMqttConnector.publishMqttMessage(systemAbilityMessage,requestTopic);
        ArrayList<String> abilities = abilityResponse(systemAbilityPublishesMessage);
        if(!abilities.contains(MerossEnum.Namespace.getAbilityValueByName(commandType))){
            throw new RuntimeException("command type not supported");
        }
        String method = getMethod(commandMessage, requestTopic);
        merossHttpConnector.logOut();
        return new Response(Map.of("method",method));
    }

    private static String getMethod(byte[] commandMessage, String requestTopic) {
        String publishMqttMessage = MerossMqttConnector.publishMqttMessage(commandMessage, requestTopic);
        JsonElement jsonElement = JsonParser.parseString(publishMqttMessage);
        return jsonElement.getAsJsonObject().getAsJsonObject("header").get("method").getAsString();
    }

    /**
     * Executes a command on the device and returns data e.g. onoff status
     * @param commandType The command type
     * @param deviceName The device's name
     *
     * @return Response record
     */
    public Response executeCommand(String deviceName, String commandType) {
        String clientId = MerossMqttConnector.buildClientId();
        MerossMqttConnector.setClientId(clientId);
        String userid = merossHttpConnector.getCloudCredentials().userId();
        if (userid != null) {
            MerossMqttConnector.setUserId(userid);
        } else {
            logger.debug("userid is null");
        }
        String key = merossHttpConnector.getCloudCredentials().key();
        if (key != null) {
            MerossMqttConnector.setKey(key);
        } else {
            logger.debug("key is null");
        }
        String brokerAddress = merossHttpConnector.getCloudCredentials().mqttDomain();
        if (brokerAddress != null) {
            MerossMqttConnector.setBrokerAddress(brokerAddress);
        } else {
            logger.debug("brokerAddress is null");
        }
        String deviceUUID = merossHttpConnector.getDevUUIDByDevName(deviceName);
        if (deviceUUID != null) {
            MerossMqttConnector.setDestinationDeviceUUID(deviceUUID);
        } else {
            logger.debug("deviceUUID is null");
        }
        String requestTopic = MerossMqttConnector.buildDeviceRequestTopic(deviceUUID);
        byte[] systemAllMessage = MerossMqttConnector.buildMqttMessage("GET",
                MerossEnum.Namespace.SYSTEM_ALL.getValue(), Collections.emptyMap());
        int deviceStatus = merossHttpConnector.getDevStatusByDevName(deviceName);
        if (deviceStatus != MerossEnum.OnlineStatus.ONLINE.getValue()) {
            throw new RuntimeException("device status is not online");
        }
        String systemAllPublishesMessage = MerossMqttConnector.publishMqttMessage(systemAllMessage, requestTopic);
        byte[] systemAbilityMessage = MerossMqttConnector.buildMqttMessage("GET",
                MerossEnum.Namespace.SYSTEM_ABILITY.getValue(), Collections.emptyMap());
        String systemAbilityPublishesMessage = MerossMqttConnector.publishMqttMessage(systemAbilityMessage,requestTopic);
        ArrayList<String>abilities = abilityResponse(systemAbilityPublishesMessage);
        if(!abilities.contains(MerossEnum.Namespace.getAbilityValueByName(commandType))){
            throw new RuntimeException("command type not supported");
        }
        merossHttpConnector.logOut();
        return getResponse(commandType, systemAllPublishesMessage);
    }

    private @NotNull Response getResponse(String commandType, String systemAllPublishesMessage) {
        return switch (commandType) {
            case "CONTROL_TOGGLEX" -> togglexResponse(systemAllPublishesMessage);
            default -> throw new IllegalStateException("Unexpected commandType: " + commandType);
        };
    }

    private Response togglexResponse(String jsonString) {
        JsonElement jsonElement =  JsonParser.parseString(jsonString);
        JsonArray togglexJsonArray = jsonElement.getAsJsonObject()
                .getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("all")
                .getAsJsonObject()
                .get("digest")
                .getAsJsonObject()
                .get("togglex")
                .getAsJsonArray();
        String method = jsonElement.getAsJsonObject().getAsJsonObject("header").get("method").getAsString();
        int channel = togglexJsonArray.get(0).getAsJsonObject().getAsJsonPrimitive("channel").getAsInt();
        int onoff = togglexJsonArray.get(0).getAsJsonObject().getAsJsonPrimitive("onoff").getAsInt();
        long lmTime = togglexJsonArray.get(0).getAsJsonObject().getAsJsonPrimitive("lmTime").getAsLong();
        return new Response(Map.of("method",method,"channel",channel,"onoff",onoff,"lmTime",lmTime));
    }

    private ArrayList<String> abilityResponse(String jsonString) {
        JsonElement digestElement = JsonParser.parseString(jsonString);
        String abilityString = digestElement.getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("ability")
                .getAsJsonObject()
                .toString();
        TypeToken<HashMap<String, HashMap<String,String>>>type = new TypeToken<>(){};
        HashMap<String,HashMap<String,String>> response = new Gson().fromJson(abilityString,type);
        return new ArrayList<>(response.keySet());

    }
}
