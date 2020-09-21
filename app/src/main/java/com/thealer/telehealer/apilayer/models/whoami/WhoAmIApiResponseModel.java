package com.thealer.telehealer.apilayer.models.whoami;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

/**
 * Created by Aswin on 11,November,2018
 */
public class WhoAmIApiResponseModel extends CommonUserApiResponseModel {

    private String version;
    private String user_activated;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUser_activated() {
        return user_activated;
    }

    public void setUser_activated(String user_activated) {
        this.user_activated = user_activated;
    }
}
