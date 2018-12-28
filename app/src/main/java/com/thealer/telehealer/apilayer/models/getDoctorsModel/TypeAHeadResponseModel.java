package com.thealer.telehealer.apilayer.models.getDoctorsModel;

import com.thealer.telehealer.apilayer.models.PaginationApiResponseModel;

import java.util.List;

/**
 * Created by Aswin on 27,December,2018
 */
public class TypeAHeadResponseModel extends PaginationApiResponseModel {

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
