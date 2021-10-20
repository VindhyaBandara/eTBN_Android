package org.readium.r2.testapp.data.model;

import com.google.gson.annotations.SerializedName;

 public class PublicationCollections {
    @SerializedName("Name")
    private String title;

    @SerializedName("Description")
    private final String href;

    @SerializedName("id")
    private String type;


    public PublicationCollections(String title, String href,String type) {

        this.title = title;
        this.href = href;
        this.type = type;
    }



    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public String gethref() {
        return href;
    }

    public void sethref(String href) {
        href = href;
    }

    public String gettype() {
        return type;
    }

    public void settype(String type) {
        this.type = type;
    }

//    @Override
//    public String toString() {
//        return "PublicationCollections{" +
//                "title='" + title + '\'' +
//                ", href='" + href + '\'' +
//                ", type='" + type + '\'' +
//                '}';
//    }
}
