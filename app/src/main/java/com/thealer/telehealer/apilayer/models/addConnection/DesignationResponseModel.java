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
    private List<String> designations = new ArrayList<>();

    public List<String> getDesignations() {
        return designations;
    }

    public void setDesignations(List<String> designations) {
        this.designations = designations;
    }
}
