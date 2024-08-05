package org.meross4j.record.response;

import com.google.gson.annotations.SerializedName;

public record ToggleXResponse(@SerializedName(value = "channel") int channel,
                              @SerializedName(value = "onoff") int onoff,
                              @SerializedName(value = "lmTime") long lmTime) implements Response {
}


