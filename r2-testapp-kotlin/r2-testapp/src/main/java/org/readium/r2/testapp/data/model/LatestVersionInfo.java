package org.readium.r2.testapp.data.model;

import com.google.gson.annotations.SerializedName;

public class LatestVersionInfo {
    @SerializedName("id")
    private String id;

    @SerializedName("deviceType")
    private String deviceType;

    @SerializedName("appName")
    private String appName;

    @SerializedName("version")
    private String version;

    @SerializedName("isMandatory")
    private String isMandatory;

    @SerializedName("releaseDate")
    private String releaseDate;

    @SerializedName("releaseNotes")
    private String releaseNotes;

    @SerializedName("assemblyVersion")
    private String assemblyVersion;

    @SerializedName("Url")
    private String Url;

    public LatestVersionInfo(String id, String deviceType,String appName,String version, String isMandatory,String releaseDate,String releaseNotes,String assemblyVersion,String Url) {

        this.id = id;
        this.deviceType = deviceType;
        this.appName = appName;
        this.version = version;
        this.isMandatory = isMandatory;
        this.releaseDate= releaseDate;
        this.releaseNotes = releaseNotes;
        this.assemblyVersion = assemblyVersion;
        this.Url = Url;
    }

    public String getrowid() {
        return id;
    }

    public void setrowid(String id) {
        this.id = id;
    }

    public String getdeviceType() {
        return deviceType;
    }

    public void setdeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getappName() {
        return appName;
    }

    public void setappName(String appName) {
        this.appName = appName;
    }

    public String getversion() {
        return version;
    }

    public void setversion(String version) {
        this.version = version;
    }

    public String getisMandatory() {
        return isMandatory;
    }

    public void setisMandatory(String isMandatory) {
        this.isMandatory = isMandatory;
    }

    public String getreleaseDate() {
        return releaseDate;
    }

    public void setreleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getreleaseNotes() {
        return releaseNotes;
    }

    public void setreleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getassemblyVersion() {
        return assemblyVersion;
    }

    public void setassemblyVersion(String assemblyVersion) {
        this.assemblyVersion = assemblyVersion;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }
}
