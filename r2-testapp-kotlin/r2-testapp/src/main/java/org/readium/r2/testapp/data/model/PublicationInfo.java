package org.readium.r2.testapp.data.model;

import com.google.gson.annotations.SerializedName;

public class PublicationInfo {

    @SerializedName("id")
    private String id;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("ISBN")
    private String ISBN;

    @SerializedName("Title")
    private String Title;

    @SerializedName("Edition")
    private String Edition;

    @SerializedName("AuthorName")
    private String AuthorName;

    @SerializedName("Summary")
    private String Summary;

    @SerializedName("ThumbnailPath")
    private String ThumbnailPath;

    @SerializedName("PublisherName")
    private String PublisherName;

    @SerializedName("PublishedDate")
    private String PublishedDate;

    @SerializedName("CityOfPublication")
    private String CityOfPublication;

    @SerializedName("CountryOfPublication")
    private String CountryOfPublication;

    @SerializedName("PublicationFileType")
    private String PublicationFileType;

    @SerializedName("ThumbnailFileType")
    private String ThumbnailFileType;

    public PublicationInfo(String id,String uuid,String ISBN,String Title,String Edition,String AuthorName,String Summary,String ThumbnailPath,String PublisherName,String PublishedDate,String CityOfPublication,String CountryOfPublication,String PublicationFileType,String ThumbnailFileType) {

        this.id = id;
        this.uuid = uuid;
        this.ISBN = ISBN;
        this.Title = Title;
        this.Edition = Edition;
        this.AuthorName= AuthorName;
        this.Summary = Summary;
        this.ThumbnailPath = ThumbnailPath;
        this.PublisherName = PublisherName;
        this.PublishedDate = PublishedDate;
        this.CityOfPublication = CityOfPublication;
        this.CountryOfPublication = CountryOfPublication;
        this.PublicationFileType = PublicationFileType;
        this.ThumbnailFileType = ThumbnailFileType;
    }

    public String getrowid() {
        return id;
    }

    public void setrowid(String id) {
        this.id = id;
    }

    public String getuuid() {
        return uuid;
    }

    public void setuuid(String uuid) {
        this.uuid = uuid;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getEdition() {
        return Edition;
    }

    public void setEdition(String Edition) {
        this.Edition = Edition;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String AuthorName) {
        this.AuthorName = AuthorName;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String Summary) {
        this.Summary = Summary;
    }

    public String getThumbnailPath() {
        return ThumbnailPath;
    }

    public void setThumbnailPath(String ThumbnailPath) {
        this.ThumbnailPath = ThumbnailPath;
    }

    public String getPublisherName() {
        return PublisherName;
    }

    public void setPublisherName(String PublisherName) {
        this.PublisherName = PublisherName;
    }

    public String getPublishedDate() {
        return PublishedDate;
    }

    public void setPublishedDate(String PublishedDate) {
        this.PublishedDate = PublishedDate;
    }

    public String getCityOfPublication() {
        return CityOfPublication;
    }

    public void setCityOfPublication(String CityOfPublication) {
        this.CityOfPublication = CityOfPublication;
    }

    public String getCountryOfPublication() {
        return CountryOfPublication;
    }

    public void setCountryOfPublication(String CountryOfPublication) {
        this.CountryOfPublication = CountryOfPublication;
    }

    public String getPublicationFileType() {
        return PublicationFileType;
    }

    public void setPublicationFileType(String PublicationFileType) {
        this.PublicationFileType = PublicationFileType;
    }

    public String getThumbnailFileType() {
        return ThumbnailFileType;
    }

    public void setThumbnailFileType(String ThumbnailFileType) {
        this.ThumbnailFileType = ThumbnailFileType;
    }
}
