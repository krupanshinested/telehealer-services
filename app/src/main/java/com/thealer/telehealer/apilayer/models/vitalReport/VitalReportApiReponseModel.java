package com.thealer.telehealer.apilayer.models.vitalReport;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

import java.util.List;

/**
 * Created by Aswin on 04,February,2019
 */
public class VitalReportApiReponseModel extends BaseApiResponseModel {
    private List<CommonUserApiResponseModel> result;

    public List<CommonUserApiResponseModel> getResult() {
        return result;
    }

    public void setResult(List<CommonUserApiResponseModel> result) {
        this.result = result;
    }
}
