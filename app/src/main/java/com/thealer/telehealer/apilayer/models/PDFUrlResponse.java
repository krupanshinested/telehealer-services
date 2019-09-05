package com.thealer.telehealer.apilayer.models;

import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.HashMap;

public class PDFUrlResponse extends BaseApiResponseModel implements Serializable {
    HashMap<String,String > result;

    @Nullable
    public String getUrl() {
        return result.get("summary");
    }
}
