package com.thealer.telehealer.apilayer.models.addConnection;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 13,April,2021
 **/
public class DesignationResponseModel extends BaseApiResponseModel implements Serializable {
    private List<String> result = new ArrayList<>();

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }
}
