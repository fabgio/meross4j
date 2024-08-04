package org.meross4j.record;

import com.google.gson.annotations.SerializedName;

public record Response(@SerializedName(value = "channel") int channel,
                       @SerializedName(value = "onoff") int onoff,
                       @SerializedName(value = "lmTime") long lmTime) {
}


