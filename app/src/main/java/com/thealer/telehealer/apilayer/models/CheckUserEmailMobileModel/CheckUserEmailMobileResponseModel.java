package com.thealer.telehealer.apilayer.models.CheckUserEmailMobileModel;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 08,October,2018
 */
public class CheckUserEmailMobileResponseModel extends BaseApiResponseModel {

    /**
     * available : true
     * user_exists : false
     */


    private boolean available;
    private boolean user_exists;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isUser_exists() {
        return user_exists;
    }

    public void setUser_exists(boolean user_exists) {
        this.user_exists = user_exists;
    }
}
