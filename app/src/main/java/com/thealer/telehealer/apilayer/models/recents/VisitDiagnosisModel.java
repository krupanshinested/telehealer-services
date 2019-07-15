package com.thealer.telehealer.apilayer.models.recents;

import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 16,July,2019
 */
public class VisitDiagnosisModel implements Serializable {
    private List<IcdCodeApiResponseModel.ResultsBean> ICD10_codes;

    public List<IcdCodeApiResponseModel.ResultsBean> getICD10_codes() {
        return ICD10_codes;
    }

    public void setICD10_codes(List<IcdCodeApiResponseModel.ResultsBean> ICD10_codes) {
        this.ICD10_codes = ICD10_codes;
    }
}
