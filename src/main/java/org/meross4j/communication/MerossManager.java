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

public class MerossManager {
    private final static Logger logger = LoggerFactory.getLogger(MerossManager.class);
    private final MerossHttpConnector merossHttpConnector;
    private MerossManager(MerossHttpConnector merossHttpConnector) {
        this.merossHttpConnector = merossHttpConnector;
    }

    public static MerossManager createMerossManager(MerossHttpConnector merossHttpConnector) {
        return new MerossManager(merossHttpConnector);
    }

    public Response executeCommand(String deviceName, String mode) {
        String clientId = MerossMqttConnector.buildClientId();
        MerossMqttConnector.setClientId(clientId);
        logger.debug("ClientId set to: {} ", clientId);
        String userid = merossHttpConnector.getCloudCredentials().userId();
        if (userid != null) {
            MerossMqttConnector.setUserId(userid);
            logger.debug("userid set to: {}", userid);
        } else {
            logger.debug("userid is null");
        }
        String key = merossHttpConnector.getCloudCredentials().key();
        if (key != null) {
            MerossMqttConnector.setKey(key);
            logger.debug("key set to: {}", key);
        } else {
            logger.debug("key is null");
        }
        String brokerAddress = merossHttpConnector.getCloudCredentials().mqttDomain();
        if (brokerAddress != null) {
            MerossMqttConnector.setBrokerAddress(brokerAddress);
            logger.debug("brokerAddress set to: {}", brokerAddress);
        } else {
            logger.debug("brokerAddress is null");
        }
        String deviceUUID = merossHttpConnector.getDevUUIDByDevName(deviceName);
        if (deviceUUID != null) {
            MerossMqttConnector.setDestinationDeviceUUID(deviceUUID);
            logger.debug("deviceUUID set to: {}", deviceUUID);
        } else {
            logger.debug("deviceUUID is null");
        }
        String requestTopic = MerossMqttConnector.buildDeviceRequestTopic(deviceUUID);
        String devType = merossHttpConnector.getDevTypeByDevName(deviceName);
        AbstractFactory abstractFactory = FactoryProvider.getFactory(devType);
        Command command = abstractFactory.commandMode(mode);
        byte[] commandMessage = command.commandType(devType);
        byte[] systemAllMessage = MerossMqttConnector.buildMqttMessage("GET",
                MerossEnum.Namespace.SYSTEM_ALL.getValue(), Collections.emptyMap());
        int deviceStatus = merossHttpConnector.getDevStatusByDevName(deviceName);
        if (deviceStatus != MerossEnum.OnlineStatus.ONLINE.getValue()) {
            logger.debug("device status: not online");
            throw new RuntimeException("device status is not online");
        }
        String commandPublishesMessage = MerossMqttConnector.publishMqttMessage(commandMessage, requestTopic);
        logger.debug("commandPublishesMessage i.e. response from broker : {}", commandPublishesMessage);
        String systemAllPublishesMessage = MerossMqttConnector.publishMqttMessage(systemAllMessage, requestTopic);
        logger.debug("systemAllPublishesMessage i.e. response from broker : {}", systemAllPublishesMessage);
        merossHttpConnector.logOut();
        return switch (devType){
            case "mss110","mss210","mss310","mss310h"->deselializeTogglexResponse(systemAllPublishesMessage);
            default -> throw new IllegalStateException("Unexpected devType: " + devType);
        };
    }

    public Response executeCommand(String deviceName) {
        String clientId = MerossMqttConnector.buildClientId();
        MerossMqttConnector.setClientId(clientId);
        logger.debug("ClientId set to: {} ", clientId);
        String userid = merossHttpConnector.getCloudCredentials().userId();
        if (userid != null) {
            MerossMqttConnector.setUserId(userid);
            logger.debug("userid set to: {}", userid);
        } else {
            logger.debug("userid is null");
        }
        String key = merossHttpConnector.getCloudCredentials().key();
        if (key != null) {
            MerossMqttConnector.setKey(key);
            logger.debug("key set to: {}", key);
        } else {
            logger.debug("key is null");
        }
        String brokerAddress = merossHttpConnector.getCloudCredentials().mqttDomain();
        if (brokerAddress != null) {
            MerossMqttConnector.setBrokerAddress(brokerAddress);
            logger.debug("brokerAddress set to: {}", brokerAddress);
        } else {
            logger.debug("brokerAddress is null");
        }
        String deviceUUID = merossHttpConnector.getDevUUIDByDevName(deviceName);
        if (deviceUUID != null) {
            MerossMqttConnector.setDestinationDeviceUUID(deviceUUID);
            logger.debug("deviceUUID set to: {}", deviceUUID);
        } else {
            logger.debug("deviceUUID is null");
        }
        String requestTopic = MerossMqttConnector.buildDeviceRequestTopic(deviceUUID);
        String type = merossHttpConnector.getDevTypeByDevName(deviceName);
        byte[] systemAllMessage = MerossMqttConnector.buildMqttMessage("GET",
                MerossEnum.Namespace.SYSTEM_ALL.getValue(), Collections.emptyMap());
        int deviceStatus = merossHttpConnector.getDevStatusByDevName(deviceName);
        if (deviceStatus != MerossEnum.OnlineStatus.ONLINE.getValue()) {
            logger.debug("device status: not online");
            throw new RuntimeException("device status is not online");
        }
        String systemAllPublishesMessage = MerossMqttConnector.publishMqttMessage(systemAllMessage, requestTopic);
        logger.debug("systemAllPublishesMessage i.e. response from broker : {}", systemAllPublishesMessage);
        merossHttpConnector.logOut();
        return switch (type){
            case "mss110","mss210","mss310","mss310h"->deselializeTogglexResponse(systemAllPublishesMessage);
            default -> throw new IllegalStateException("Unexpected type: " + type);
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
        return new Response(method,channel,onoff,lmTime);
    }
}
