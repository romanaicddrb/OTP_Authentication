package org.icddrb.otp_authentication.model_class.request;

public class OTPVerifyModel {

    String platform;
    String deviceId;
    String mobile;
    String email;
    String otp;

    public OTPVerifyModel(OTPRequestModel object,
                          String otp) {
        this.platform = object.getPlatform();
        this.deviceId = object.getDeviceId();
        this.mobile = object.getMobile();
        this.email = object.getEmail();
        this.otp = otp;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
