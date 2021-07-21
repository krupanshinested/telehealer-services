package com.thealer.telehealer.apilayer.models.transaction.resp;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

public class AddChargeResp extends BaseApiResponseModel {

    private TransactionItem data;

    public TransactionItem getData() {
        return data;
    }

    public void setData(TransactionItem data) {
        this.data = data;
    }
}
