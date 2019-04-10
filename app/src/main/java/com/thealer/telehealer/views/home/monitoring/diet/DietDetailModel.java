package com.thealer.telehealer.views.home.monitoring.diet;

import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 15,April,2019
 */
public class DietDetailModel implements Serializable {
    private String date;
    private List<DietApiResponseModel> dietApiResponseModelList;

    public DietDetailModel(String date, List<DietApiResponseModel> dietApiResponseModelList) {
        this.date = date;
        this.dietApiResponseModelList = dietApiResponseModelList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<DietApiResponseModel> getDietApiResponseModelList() {
        return dietApiResponseModelList;
    }

    public void setDietApiResponseModelList(List<DietApiResponseModel> dietApiResponseModelList) {
        this.dietApiResponseModelList = dietApiResponseModelList;
    }
}
