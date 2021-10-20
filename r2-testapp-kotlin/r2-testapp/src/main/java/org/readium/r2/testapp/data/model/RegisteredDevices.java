package org.readium.r2.testapp.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisteredDevices {
    @SerializedName("DeviceID")
    private String DeviceID;

    @SerializedName("DeviceModel")
    private String DeviceModel;

    @SerializedName("DeviceName")
    private String DeviceName;

    @SerializedName("DeviceOS")
    private String DeviceOS;

    @SerializedName("id")
    private String id;

    @SerializedName("MACAddress")
    private String MACAddress;

    @SerializedName("RequestedForRemove")
    private String RequestedForRemove;

    @SerializedName("user")
    private String duser;

    public RegisteredDevices(String DeviceID, String DeviceModel,String DeviceName,String DeviceOS, String id,String MACAddress,String RequestedForRemove, String user) {

        this.DeviceID = DeviceID;
        this.DeviceModel = DeviceModel;
        this.DeviceName = DeviceName;
        this.DeviceOS = DeviceOS;
        this.id = id;
        this.MACAddress = MACAddress;
        this.RequestedForRemove = RequestedForRemove;
        this.duser = user;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String DeviceID) {
        this.DeviceID = DeviceID;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String DeviceModel) {
        this.DeviceModel = DeviceModel;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String DeviceName) {
        this.DeviceName = DeviceName;
    }

    public String getDeviceOS() {
        return DeviceOS;
    }

    public void setDeviceOS(String DeviceOS) {
        this.DeviceOS = DeviceOS;
    }

    public String getdevid() {
        return id;
    }

    public void setdevid(String id) {
        this.id = id;
    }

    public String getMACAddress() {
        return MACAddress;
    }

    public void setMACAddress(String MACAddress) {
        this.MACAddress = MACAddress;
    }

    public String getRequestedForRemove() {
        return RequestedForRemove;
    }

    public void setRequestedForRemove(String RequestedForRemove) {
        this.RequestedForRemove = RequestedForRemove;
    }

    public String getuser() {
        return duser;
    }

    public void setuser(String user) {
        this.duser = user;
    }
}
