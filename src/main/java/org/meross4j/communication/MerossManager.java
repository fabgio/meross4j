package org.meross4j.communication;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.meross4j.command.Command;
import org.meross4j.factory.AbstractFactory;
import org.meross4j.factory.FactoryProvider;
import org.meross4j.record.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
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
     * Executes a command on the device and set mode e,g. ON or OFF and returns data
     * @param deviceName The device's name
     * @param mode the mode
     * @return Response record
     */
    public Response executeCommand(String deviceName, String mode) {
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
        String devType = merossHttpConnector.getDevTypeByDevName(deviceName);
        AbstractFactory abstractFactory = FactoryProvider.getFactory(devType);
        Command command = abstractFactory.commandMode(mode);
        byte[] commandMessage = command.commandType(devType);
        int deviceStatus = merossHttpConnector.getDevStatusByDevName(deviceName);
        if (deviceStatus != MerossEnum.OnlineStatus.ONLINE.getValue()) {
            throw new RuntimeException("device status is not online");
        }
        String publishMqttMessage = MerossMqttConnector.publishMqttMessage(commandMessage, requestTopic);
        JsonElement jsonElement =  JsonParser.parseString(publishMqttMessage);
        String method = jsonElement.getAsJsonObject().getAsJsonObject("header").get("method").getAsString();
        return new Response(Map.of("method",method));
    }

    /**
     * Executes a command on the device and returns data e.g. onoff status
     * @param deviceName The device's name
     * @return Response record
     */
    public Response executeCommand(String deviceName) {
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
        String devType = merossHttpConnector.getDevTypeByDevName(deviceName);
        byte[] systemAllMessage = MerossMqttConnector.buildMqttMessage("GET",
                MerossEnum.Namespace.SYSTEM_ALL.getValue(), Collections.emptyMap());
        int deviceStatus = merossHttpConnector.getDevStatusByDevName(deviceName);
        if (deviceStatus != MerossEnum.OnlineStatus.ONLINE.getValue()) {
            throw new RuntimeException("device status is not online");
        }
        String systemAllPublishesMessage = MerossMqttConnector.publishMqttMessage(systemAllMessage, requestTopic);
        merossHttpConnector.logOut();
         return switch (devType){
             case "mss110","mss210","mss310","mss310h"->deselializeTogglexResponse(systemAllPublishesMessage);
             default -> throw new IllegalStateException("Unexpected devType: " + devType);
        };
    }


    private Response deselializeTogglexResponse(String jsonString) {
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
}