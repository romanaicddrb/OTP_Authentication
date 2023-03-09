package org.icddrb.otp_authentication.model_class.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OTPRequestModel implements Parcelable {
    String platform;
    String deviceId;
    String mobile;
    String email;

    public OTPRequestModel(String deviceId,
                           String mobile,
                           String email) {
        this.platform = "ANDROID";
        this.deviceId = deviceId;
        this.mobile = mobile;
        this.email = email;
    }

    public OTPRequestModel(Parcel parcel) {
        this.platform = parcel.readString();
        this.deviceId = parcel.readString();
        this.mobile = parcel.readString();
        this.email = parcel.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(platform);
        parcel.writeString(deviceId);
        parcel.writeString(mobile);
        parcel.writeString(email);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<OTPRequestModel>() {
        @Override
        public OTPRequestModel createFromParcel(Parcel parcel) {
            return new OTPRequestModel(parcel);
        }

        @Override
        public OTPRequestModel[] newArray(int i) {
            return new OTPRequestModel[i];
        }
    };

    @Override
    public String toString() {
        return "OTPRequestModel{" +
                "platform=" + platform +
                ", deviceId='" + deviceId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email=" + email +
                '}';
    }
}
