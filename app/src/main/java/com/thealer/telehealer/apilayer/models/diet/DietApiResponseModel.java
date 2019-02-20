package com.thealer.telehealer.apilayer.models.diet;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.NutrientsDetailBean;

import java.util.Map;

/**
 * Created by Aswin on 20,February,2019
 */
public class DietApiResponseModel extends BaseApiResponseModel {

    private int user_diet_id;
    private String meal_type;
    private String date;
    private String image_url;
    private int serving;
    private String serving_unit;
    private FoodBean food;
    private int user_id;
    private String created_at;
    private String updated_at;

    public int getUser_diet_id() {
        return user_diet_id;
    }

    public void setUser_diet_id(int user_diet_id) {
        this.user_diet_id = user_diet_id;
    }

    public String getMeal_type() {
        return meal_type;
    }

    public void setMeal_type(String meal_type) {
        this.meal_type = meal_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getServing() {
        return serving;
    }

    public void setServing(int serving) {
        this.serving = serving;
    }

    public String getServing_unit() {
        return serving_unit;
    }

    public void setServing_unit(String serving_unit) {
        this.serving_unit = serving_unit;
    }

    public FoodBean getFood() {
        return food;
    }

    public void setFood(FoodBean food) {
        this.food = food;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public static class FoodBean {

        private String image_url;
        private String name;
        private Map<String, NutrientsDetailBean> totalNutrients;
        private String ingredients;

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, NutrientsDetailBean> getTotalNutrients() {
            return totalNutrients;
        }

        public void setTotalNutrients(Map<String, NutrientsDetailBean> totalNutrients) {
            this.totalNutrients = totalNutrients;
        }

        public String getIngredients() {
            return ingredients;
        }

        public void setIngredients(String ingredients) {
            this.ingredients = ingredients;
        }
    }

}
