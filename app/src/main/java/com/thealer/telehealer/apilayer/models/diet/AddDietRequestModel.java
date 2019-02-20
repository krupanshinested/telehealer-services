package com.thealer.telehealer.apilayer.models.diet;

/**
 * Created by Aswin on 21,February,2019
 */
public class AddDietRequestModel {
    private String meal_type;
    private String serving;
    private String date;
    private DietApiResponseModel.FoodBean foodBean;
    private String serving_unit;
    private String uploadUrl;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getMeal_type() {
        return meal_type;
    }

    public void setMeal_type(String meal_type) {
        this.meal_type = meal_type;
    }

    public String getServing() {
        return serving;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DietApiResponseModel.FoodBean getFoodBean() {
        return foodBean;
    }

    public void setFoodBean(DietApiResponseModel.FoodBean foodBean) {
        this.foodBean = foodBean;
    }

    public String getServing_unit() {
        return serving_unit;
    }

    public void setServing_unit(String serving_unit) {
        this.serving_unit = serving_unit;
    }
}
