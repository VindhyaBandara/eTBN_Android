package org.readium.r2.testapp.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSession {

    private final SharedPreferences prefs;

    public AppSession(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setLoggeduserid(String loggeduserid) {
        prefs.edit().putString("loggeduserid", loggeduserid).commit();
    }

    public String getLoggeduserid() {
        String usename = prefs.getString("loggeduserid","");
        return usename;
    }

    public void setUsertoken(String usertoken) {
        prefs.edit().putString("usertoken", usertoken).commit();
    }

    public String getUsertoken() {
        String usertoken = prefs.getString("usertoken","");
        return usertoken;
    }

    public void setOrgname(String orgname) {
        prefs.edit().putString("orgname", orgname).commit();
    }

    public String getOrgname() {
        String orgname = prefs.getString("orgname","");
        return orgname;
    }

    public void setOrgid(String orgid) {
        prefs.edit().putString("orgid", orgid).commit();
    }

    public String getOrgid() {
        String orgid = prefs.getString("orgid","");
        return orgid;
    }

    public void setPagenumber(String pnumber) {
        prefs.edit().putString("pnumber", pnumber).commit();
    }

    public String getPagenumber() {
        String pnumber = prefs.getString("pnumber","");
        return pnumber;
    }
}
