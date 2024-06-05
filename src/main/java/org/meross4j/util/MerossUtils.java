package org.meross4j.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class MerossUtils {
    public static String buildAppId(){
        String rndUUID = UUID.randomUUID().toString();
        String stringToHash = "API"+rndUUID;
        return DigestUtils.md5Hex(stringToHash);
    }

}
