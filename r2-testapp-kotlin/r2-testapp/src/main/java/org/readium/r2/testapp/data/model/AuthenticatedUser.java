package org.readium.r2.testapp.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthenticatedUser {
    @SerializedName("user")
    private user user;

    @SerializedName("token")
    private String token;


    public user getuser() {
        return user;
    }
    public String gettoken() {return token;}

    public String getusername() {
        return user.getusername();
    }
    public String getpassword() {
        return user.getpassword();
    }
    public String getid() {
        return user.getid();
    }
    public String getfirstName() {return user.getfirstName(); }
    public String getorganizationName() {return user.getorganizationName();}
    public String getorganizationId() {return user.getorganizationId();}

}
class user {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("id")
    private String id;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("organizationName")
    private String organizationName;

    @SerializedName("organizationId")
    private String organizationId;

    public String getusername() {
        return username;
    }
    public String getpassword() {
        return password;
    }
    public String getid() {
        return id;
    }
    public String getfirstName() { return firstName; }
    public String getorganizationName() { return organizationName; }
    public String getorganizationId() { return organizationId; }
}
