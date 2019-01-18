package com.thealer.telehealer.apilayer.models.signature;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 18,January,2019
 */
public class SignatureApiResponseModel extends BaseApiResponseModel {

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
