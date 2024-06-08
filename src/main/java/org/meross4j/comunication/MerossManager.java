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
    void executeCommand(String name, String mode) {
        String  userid = merossHttpConnector.getCloudCredentials().userId();
        if (userid != null) {
            MerossMqttConnector.userId=userid;
         } else {
            logger.debug("userid is null");
        }
        String key = merossHttpConnector.getCloudCredentials().key();
        if (key != null) {
            MerossMqttConnector.key=key;
        } else {
            logger.debug("key is null");
        }

        String brokerAddress = merossHttpConnector.getCloudCredentials().mqttDomain();
        if (brokerAddress != null) {
            MerossMqttConnector.brokerAddress=brokerAddress;
        } else {
            logger.debug("brokerAddress is null");
        }

        String destinationDeviceUUID = merossHttpConnector.getDevUUIDByDevName(name);
        if (destinationDeviceUUID != null) {
            MerossMqttConnector.destinationDeviceUUID=destinationDeviceUUID;
        } else {
            logger.debug("deviceUUID is null");
        }

        String responseTopic = MerossMqttConnector.buildResponseTopic();

        String type = merossHttpConnector.getDevTypeByDevName(name);
        AbstractFactory abstractFactory = FactoryProvider.getFactory(type);

        Command command = abstractFactory.createCommand(mode);
        MerossMqttConnector.publishMqttMessage(command.toString(),responseTopic);
    }
}
