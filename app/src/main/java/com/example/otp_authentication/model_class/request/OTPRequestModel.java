package com.example.otp_authentication.model_class.request;

import com.google.gson.annotations.SerializedName;

public class OTPRequestModel {
    int projectId;
    String deviceId;
    String requestUid;

    @SerializedName("request_type")
    String requestType;

    public OTPRequestModel(String deviceId,
                           String requestUid,
                           String requestType) {
        this.projectId = 1;
        this.deviceId = deviceId;
        this.requestUid = requestUid;
        this.requestType = requestType;
    }
    public OTPRequestModel(int projectId,
                           String deviceId,
                           String requestUid,
                           String requestType) {
        this.projectId = projectId;
        this.deviceId = deviceId;
        this.requestUid = requestUid;
        this.requestType = requestType;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRequestUid() {
        return requestUid;
    }

    public void setRequestUid(String requestUid) {
        this.requestUid = requestUid;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
