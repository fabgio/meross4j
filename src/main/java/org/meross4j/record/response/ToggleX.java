package org.meross4j.record.response;

import com.google.gson.annotations.SerializedName;
import org.meross4j.comunication.Response;

public record ToggleX(@SerializedName(value = "channel") int channel,
                      @SerializedName(value = "onoff") int onoff,
                      @SerializedName(value = "lmTime") long lmTime) implements Response {
}


