package com.example.otp_authentication.model_class.request;

public class OTPVerifyModel {

    int projectId;
    String deviceId;
    String requestUid;
    int otp;

    public OTPVerifyModel(String deviceId,
                          String requestUid,
                          int otp) {

        this.projectId = 1;
        this.deviceId = deviceId;
        this.requestUid = requestUid;
        this.otp = otp;
    }

    public OTPVerifyModel(int projectId,
                          String deviceId,
                          String requestUid,
                          int otp) {

        this.projectId = projectId;
        this.deviceId = deviceId;
        this.requestUid = requestUid;
        this.otp = otp;
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

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }
}
