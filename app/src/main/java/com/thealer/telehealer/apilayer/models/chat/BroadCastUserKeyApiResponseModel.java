package com.thealer.telehealer.apilayer.models.chat;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;

import java.io.Serializable;
import java.util.ArrayList;

public class BroadCastUserKeyApiResponseModel extends BaseApiResponseModel implements Serializable {

    private ArrayList<SignalKey> data;

    public ArrayList<SignalKey> getData() {
        return data;
    }

    public void setData(ArrayList<SignalKey> data) {
        this.data = data;
    }
}
