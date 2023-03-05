package org.icddrb.otp_authentication.model_class.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OTPRequestModel implements Parcelable {
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

    public OTPRequestModel(Parcel parcel) {
        this.projectId = parcel.readInt();
        this.deviceId = parcel.readString();
        this.requestUid = parcel.readString();
        this.requestType = parcel.readString();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(projectId);
        parcel.writeString(requestUid);
        parcel.writeString(deviceId);
        parcel.writeString(requestType);
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
                "projectId=" + projectId +
                ", deviceId='" + deviceId + '\'' +
                ", requestUid='" + requestUid + '\'' +
                ", requestType=" + requestType +
                '}';
    }
}
