package org.meross4j.record;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @param deviceType
 * @param devIconId
 * @param onlineStatus
 * @param devName
 * @param firmwareVersion
 * @param uuid
 * @param userDeviceIcon
 * @param channels
 * @param bindTime
 * @param iconType
 * @param domain
 * @param reservedDomain
 * @param hardwareCapabilities
 * @param subType
 * @param region
 * @param hardwareVersion
 */
public record Device(@SerializedName(value = "deviceType") String deviceType,
                     @SerializedName(value = "devIconId" ) String devIconId,
                     @SerializedName(value = "onlineStatus") int onlineStatus,
                     @SerializedName(value = "devName") String devName,
                     @SerializedName(value = "fmwareVersion") String firmwareVersion,
                     @SerializedName(value = "uuid") String uuid,
                     @SerializedName(value = "userDevIcon") String userDeviceIcon,
                     @SerializedName(value = "channels") ArrayList<Object> channels, //check
                     @SerializedName(value = "bindTime") long bindTime,
                     @SerializedName(value = "iconType") int iconType,
                     @SerializedName(value = "domain") String domain,
                     @SerializedName(value = "reservedDomain") String reservedDomain,
                     @SerializedName(value = "hardwareCapabilities") ArrayList<Object> hardwareCapabilities,//check
                     @SerializedName(value = "subType") String subType,
                     @SerializedName(value = "region") String region,
                     @SerializedName(value = "hdwareVersion") String hardwareVersion) {
}
