package com.thealer.telehealer.apilayer.models.orders.prescription;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 30,November,2018
 */
public class CreatePrescriptionResponseModel extends BaseApiResponseModel {

    private int referral_id;

    public int getReferral_id() {
        return referral_id;
    }

    public void setReferral_id(int referral_id) {
        this.referral_id = referral_id;
    }
}
