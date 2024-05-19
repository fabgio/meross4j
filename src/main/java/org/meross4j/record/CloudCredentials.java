package org.meross4j.record;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link CloudCredentials} class contains cloud credential definitions for the
 * whole library
 *
 * @author Giovanni Fabiani - Initial contribution
 */

public record CloudCredentials(
        @SerializedName(value = "token")  String token,
        @SerializedName(value = "key") String key,
        @SerializedName(value = "userid") String userId,
        @SerializedName(value = "email") String userEmail,
        @SerializedName(value = "domain") String domain,
        @SerializedName(value = "mqttDomain") String mqttDomain,
        @SerializedName(value = "mfaLockExpire") String mfaLockExpire
) {
}

