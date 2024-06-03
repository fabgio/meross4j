package org.meross4j.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.meross4j.comunication.MerossHttpConnector;
import org.meross4j.record.Device;

import java.util.UUID;

public class MerossUtils {
    public static String buildAppId(){
        String rndUUID = UUID.randomUUID().toString();
        String stringToHash = "API"+rndUUID;
        return DigestUtils.md5Hex(stringToHash);
    }

    public static String getDevUUIDByDevName(String devName) {
        return new MerossHttpConnector().getDevices().stream()
                .filter(device->device.devName().equals(devName))
                .map(Device::uuid)
                .findFirst()
                .orElseThrow(()->new RuntimeException("No device found with name: "+devName));
    }
}
