package org.meross4j.record;

import com.google.gson.annotations.SerializedName;

/**
 *  Record containing the  Device components
 **/
public record Device(@SerializedName(value = "deviceType") String deviceType,
                     @SerializedName(value = "devIconId" ) String devIconId,
                     @SerializedName(value = "onlineStatus") int onlineStatus,
                     @SerializedName(value = "devName") String devName,
                     @SerializedName(value = "fmwareVersion") String firmwareVersion,
                     @SerializedName(value = "uuid") String uuid,
                     @SerializedName(value = "userDevIcon") String userDeviceIcon,
                     @SerializedName(value = "bindTime") long bindTime,
                     @SerializedName(value = "iconType") int iconType,
                     @SerializedName(value = "domain") String domain,
                     @SerializedName(value = "reservedDomain") String reservedDomain,
                     @SerializedName(value = "subType") String subType,
                     @SerializedName(value = "region") String region,
                     @SerializedName(value = "hdwareVersion") String hardwareVersion) {
}
