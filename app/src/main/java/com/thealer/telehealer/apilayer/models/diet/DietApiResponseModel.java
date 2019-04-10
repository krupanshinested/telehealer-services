package com.thealer.telehealer.apilayer.models.diet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.food.NutrientsDetailBean;
import com.thealer.telehealer.views.home.monitoring.diet.FoodConstant;

import java.util.HashMap;
import java.util.List;
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
    private String order_id;

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

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
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

    public static Map<String, Double> getDisplayDiets(@NonNull List<DietApiResponseModel> dietApiResponseModelList) {
        Map<String, Double> dietMap = new HashMap<>();
        double calories = 0, carbs = 0, fat = 0, protien = 0;

        for (int j = 0; j < dietApiResponseModelList.size(); j++) {

            if (dietApiResponseModelList.get(j).getFood() != null &&
                    dietApiResponseModelList.get(j).getFood().getTotalNutrients() != null) {
                if (dietApiResponseModelList.get(j).getFood().getTotalNutrients().get(FoodConstant.FOOD_ENERGY) != null)
                    calories = calories + dietApiResponseModelList.get(j).getFood().getTotalNutrients().get(FoodConstant.FOOD_ENERGY).getQuantity();

                if (dietApiResponseModelList.get(j).getFood().getTotalNutrients().get(FoodConstant.FOOD_CARBS) != null)
                    carbs = carbs + dietApiResponseModelList.get(j).getFood().getTotalNutrients().get(FoodConstant.FOOD_CARBS).getQuantity();

                if (dietApiResponseModelList.get(j).getFood().getTotalNutrients().get(FoodConstant.FOOD_FAT) != null)
                    fat = fat + dietApiResponseModelList.get(j).getFood().getTotalNutrients().get(FoodConstant.FOOD_FAT).getQuantity();

                if (dietApiResponseModelList.get(j).getFood().getTotalNutrients().get(FoodConstant.FOOD_PROTEIN) != null)
                    protien = protien + dietApiResponseModelList.get(j).getFood().getTotalNutrients().get(FoodConstant.FOOD_PROTEIN).getQuantity();
            }
        }

        dietMap.put(FoodConstant.FOOD_ENERGY, calories);
        dietMap.put(FoodConstant.FOOD_CARBS, carbs);
        dietMap.put(FoodConstant.FOOD_FAT, fat);
        dietMap.put(FoodConstant.FOOD_PROTEIN, protien);
        return dietMap;
    }

    public static String getDisplayValue(double value) {
        if (value == 0)
            return "-";

        if (value >= 1000) {
            return String.format("%.1f", (float) value / 1000);
        } else {
            return String.valueOf((int) value);
        }

    }

    public static String getDisplayUnit(Context context, double value) {
        return value > 1000 ? context.getString(R.string.kg) : context.getString(R.string.g);
    }

    public static String getCalorieValue(double calories) {
        if (calories == 0)
            return "-";

        return String.valueOf((int) calories);
    }


    public static String getCalorieUnit(FragmentActivity activity) {
        return activity.getString(R.string.cal);
    }
}
