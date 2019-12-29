package com.thealer.telehealer.apilayer.models;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class DoctorGroupedAssociations extends BaseApiResponseModel implements Serializable {
    private String group_name;
    private ArrayList<CommonUserApiResponseModel> doctors;

    public String getGroup_name() {
        return group_name;
    }

    public ArrayList<CommonUserApiResponseModel> getDoctors() {
        return doctors;
    }
}
