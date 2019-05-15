package com.thealer.telehealer.apilayer.models.chat;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;

import java.io.Serializable;

/**
 * Created by Aswin on 15,May,2019
 */
public class UserKeysApiResponseModel extends BaseApiResponseModel implements Serializable {

    private SignalKey data;

    public SignalKey getData() {
        return data;
    }

    public void setData(SignalKey data) {
        this.data = data;
    }
}
