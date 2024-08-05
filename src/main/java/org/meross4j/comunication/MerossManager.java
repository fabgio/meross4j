package org.meross4j.comunication;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.meross4j.record.response.ToggleXResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
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
        String type = merossHttpConnector.getDevTypeByDevName(deviceName);
        AbstractFactory abstractFactory = FactoryProvider.getFactory(type);
        Command command = abstractFactory.commandMode(mode);
        byte[] commandMessage = command.commandType(type);
        byte[] systemAllMessage = MerossMqttConnector.buildMqttMessage("GET",
                MerossConstants.Namespace.SYSTEM_ALL.getValue(), Collections.emptyMap());
        int deviceStatus = merossHttpConnector.getDevStatusByDevName(deviceName);
        if (deviceStatus != MerossConstants.OnlineStatus.ONLINE.getValue()) {
            logger.debug("device status: not online");
            throw new RuntimeException("device status is not online");
        }
        String commandPublishesMessage = MerossMqttConnector.publishMqttMessage(commandMessage, requestTopic);
        logger.debug("commandPublishesMessage i.e. response from broker : {}", commandPublishesMessage);
        String systemAllPublishesMessage = MerossMqttConnector.publishMqttMessage(systemAllMessage, requestTopic);
        ToggleXResponse response = deselializeToggleXResponse(systemAllPublishesMessage);
        logger.debug("commandPublishesMessage i.e. response from broker : {}", systemAllPublishesMessage);
        merossHttpConnector.logOut();
        return response;
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
        byte[] systemAllMessage = MerossMqttConnector.buildMqttMessage("GET",
                MerossConstants.Namespace.SYSTEM_ALL.getValue(), Collections.emptyMap());
        int deviceStatus = merossHttpConnector.getDevStatusByDevName(deviceName);
        if (deviceStatus != MerossConstants.OnlineStatus.ONLINE.getValue()) {
            logger.debug("device status: not online");
            throw new RuntimeException("device status is not online");
        }
        String systemAllPublishesMessage = MerossMqttConnector.publishMqttMessage(systemAllMessage, requestTopic);
        Response response = deselializeToggleXResponse(systemAllPublishesMessage);
        logger.debug("commandPublishesMessage i.e. response from broker : {}", systemAllPublishesMessage);
        merossHttpConnector.logOut();
        return response;
    }

    private ToggleXResponse deselializeToggleXResponse(String jsonString) {
        JsonElement jsonElement =  JsonParser.parseString(jsonString);
        String togglexString = jsonElement.getAsJsonObject()
                .get("payload")
                .getAsJsonObject()
                .get("all")
                .getAsJsonObject()
                .get("digest")
                .getAsJsonObject()
                .get("togglex")
                .getAsJsonArray().toString();
        TypeToken<ArrayList<ToggleXResponse>>type=new TypeToken<>(){};
        return new Gson().fromJson(togglexString,type).get(0);
    }
}
