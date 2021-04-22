package com.thealer.telehealer.apilayer.models.chat;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 15,April,2021
 **/
public class BroadCastUserApiResponseModel extends BaseApiResponseModel implements Serializable {

    List<String> patients;

    public List<String> getPatients() {
        return patients;
    }

    public void setPatients(List<String> patients) {
        this.patients = patients;
    }
}
