package com.thealer.telehealer.apilayer.models.vitals;

import com.thealer.telehealer.apilayer.models.PaginationApiResponseModel;

import java.util.List;

/**
 * Created by Aswin on 02,July,2019
 */
public class VitalsPaginatedApiResponseModel extends PaginationApiResponseModel {
    private List<VitalsApiResponseModel> result;

    public List<VitalsApiResponseModel> getResult() {
        return result;
    }

    public void setResult(List<VitalsApiResponseModel> result) {
        this.result = result;
    }
}
