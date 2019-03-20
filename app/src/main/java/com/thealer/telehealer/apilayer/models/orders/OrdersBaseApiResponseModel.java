package com.thealer.telehealer.apilayer.models.orders;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 21,March,2019
 */
public class OrdersBaseApiResponseModel extends BaseApiResponseModel {

    private int referral_id;

    public int getReferral_id() {
        return referral_id;
    }

    public void setReferral_id(int referral_id) {
        this.referral_id = referral_id;
    }
}
