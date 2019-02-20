package com.thealer.telehealer.apilayer.models.diet.food;

import java.io.Serializable;

/**
 * Created by Aswin on 19,March,2019
 */
public class NutrientsDetailRequestModel implements Serializable {

    private int quantity;
    private String measureURI;
    private String foodId;

    public NutrientsDetailRequestModel() {
    }

    public NutrientsDetailRequestModel(int quantity, String measureURI, String foodId) {
        this.quantity = quantity;
        this.measureURI = measureURI;
        this.foodId = foodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMeasureURI() {
        return measureURI;
    }

    public void setMeasureURI(String measureURI) {
        this.measureURI = measureURI;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
}

