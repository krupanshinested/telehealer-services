package com.thealer.telehealer.views.home.monitoring.diet;

import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;

/**
 * Created by Aswin on 21,February,2019
 */
public class DietListAdapterModel {
    private int type;
    private String title;
    private DietApiResponseModel data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DietApiResponseModel getData() {
        return data;
    }

    public void setData(DietApiResponseModel data) {
        this.data = data;
    }
}
