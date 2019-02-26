package com.thealer.telehealer.apilayer.models.userStatus;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 27,February,2019
 */
public class ConnectionStatusApiResponseModel extends BaseApiResponseModel {

    private String connection_status;

    public String getConnection_status() {
        return connection_status;
    }

    public void setConnection_status(String connection_status) {
        this.connection_status = connection_status;
    }
}
