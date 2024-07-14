package org.meross4j.record;

import com.google.gson.annotations.SerializedName;

/**
 *  Record containing the CloudCredential components
 **/

public record CloudCredentials(@SerializedName(value = "token")  String token,
        @SerializedName(value = "key") String key,
        @SerializedName(value = "userid") String userId, //mqtt
        @SerializedName(value = "email") String userEmail,
        @SerializedName(value = "domain") String domain,
        @SerializedName(value = "mqttDomain") String mqttDomain// mqtt
) {
}

