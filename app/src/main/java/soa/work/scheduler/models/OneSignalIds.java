package soa.work.scheduler.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OneSignalIds {
    @SerializedName("onesignal_app_id")
    @Expose
    private String onesignalAppId;
    @SerializedName("rest_api_key")
    @Expose
    private String restApiKey;

    public String getOnesignalAppId() {
        return onesignalAppId;
    }

    public void setOnesignalAppId(String onesignalAppId) {
        this.onesignalAppId = onesignalAppId;
    }

    public String getRestApiKey() {
        return restApiKey;
    }

    public void setRestApiKey(String restApiKey) {
        this.restApiKey = restApiKey;
    }
}
