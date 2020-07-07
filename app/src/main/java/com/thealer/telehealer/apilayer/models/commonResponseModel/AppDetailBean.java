package com.thealer.telehealer.apilayer.models.commonResponseModel;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;

import java.io.Serializable;

public class AppDetailBean implements Serializable {

    private String version;
    private String platform;

    public AppDetailBean() {
    }

    public AppDetailBean(String app_version, String app_platform) {
        this.version = app_version;
        this.platform = app_platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
          }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String displayPlatform(){
        if (platform != null && !platform.isEmpty()) {
            return platform.replace(String.valueOf(platform.charAt(0)), String.valueOf(platform.charAt(0)).toUpperCase());
        } else {
            return platform;
        }
    }

    public Boolean isWebUser() {
        if (getPlatform().equals(Constants.WEB))
            return true;
        else
            return false;
    }
}
