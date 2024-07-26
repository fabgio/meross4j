package org.meross4j.comunication;

import org.meross4j.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerossManager {
    private final static Logger logger = LoggerFactory.getLogger(MerossManager.class);
    private final MerossHttpConnector merossHttpConnector;
    private MerossManager(MerossHttpConnector merossHttpConnector) {
        this.merossHttpConnector = merossHttpConnector;
    }

    public static MerossManager createMerossManager(MerossHttpConnector merossHttpConnector) {
        return new MerossManager(merossHttpConnector);
    }
    public  void executeCommand(String deviceName, String mode)  {
        String clientId=MerossMqttConnector.buildClientId();
        MerossMqttConnector.setClientId(clientId);
        logger.debug("ClientId set to: {} ",clientId);
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
        Command command = abstractFactory.createCommandMode(mode);
        byte[] message = command.createCommandType(type);
        MerossMqttConnector.publishMqttMessage(message,requestTopic); //response
        merossHttpConnector.logOut();
    }
}
