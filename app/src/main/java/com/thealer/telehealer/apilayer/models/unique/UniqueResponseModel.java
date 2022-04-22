package com.thealer.telehealer.apilayer.models.unique;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;

public class UniqueResponseModel extends BaseApiResponseModel implements Serializable {

    private String code;

    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private String external_id;

        public String getExternal_id() {
            return external_id;
        }

        public void setExternal_id(String external_id) {
            this.external_id = external_id;
        }
    }
}
