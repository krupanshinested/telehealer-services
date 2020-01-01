package com.thealer.telehealer.apilayer.models.EducationalVideo;

import com.thealer.telehealer.apilayer.models.orders.OrdersApiResponseModel;
import java.util.List;

public class EducationalVideoApiResponseModel extends OrdersApiResponseModel {

    private List<EducationalVideoOrder> result;

    public List<EducationalVideoOrder> getResult() {
        return result;
    }

    public void setResult(List<EducationalVideoOrder> result) {
        this.result = result;
    }

}
