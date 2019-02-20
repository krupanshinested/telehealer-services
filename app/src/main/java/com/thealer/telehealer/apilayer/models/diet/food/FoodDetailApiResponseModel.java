package com.thealer.telehealer.apilayer.models.diet.food;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 21,February,2019
 */
public class FoodDetailApiResponseModel extends BaseApiResponseModel {

    private String uri;
    private int yield;
    private double calories;
    private double totalWeight;
    private Map<String, NutrientsDetailBean> totalNutrients;
    private Map<String, NutrientsDetailBean> totalDaily;
    private List<String> dietLabels;
    private List<String> healthLabels;
    private List<String> cautions;
    private List<IngredientsBean> ingredients;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getYield() {
        return yield;
    }

    public void setYield(int yield) {
        this.yield = yield;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Map<String, NutrientsDetailBean> getTotalNutrients() {
        return totalNutrients;
    }

    public void setTotalNutrients(Map<String, NutrientsDetailBean> totalNutrients) {
        this.totalNutrients = totalNutrients;
    }

    public Map<String, NutrientsDetailBean> getTotalDaily() {
        return totalDaily;
    }

    public void setTotalDaily(Map<String, NutrientsDetailBean> totalDaily) {
        this.totalDaily = totalDaily;
    }

    public List<String> getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(List<String> dietLabels) {
        this.dietLabels = dietLabels;
    }

    public List<String> getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(List<String> healthLabels) {
        this.healthLabels = healthLabels;
    }

    public List<String> getCautions() {
        return cautions;
    }

    public void setCautions(List<String> cautions) {
        this.cautions = cautions;
    }

    public List<IngredientsBean> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientsBean> ingredients) {
        this.ingredients = ingredients;
    }

    public static class IngredientsBean implements Serializable{
        private List<ParsedBean> parsed;

        public List<ParsedBean> getParsed() {
            return parsed;
        }

        public void setParsed(List<ParsedBean> parsed) {
            this.parsed = parsed;
        }

        public static class ParsedBean implements Serializable{

            private double quantity;
            private String measure;
            private String food;
            private String foodId;
            private double weight;
            private double retainedWeight;
            private String measureURI;
            private String status;

            public double getQuantity() {
                return quantity;
            }

            public void setQuantity(double quantity) {
                this.quantity = quantity;
            }

            public double getWeight() {
                return weight;
            }

            public void setWeight(double weight) {
                this.weight = weight;
            }

            public double getRetainedWeight() {
                return retainedWeight;
            }

            public void setRetainedWeight(double retainedWeight) {
                this.retainedWeight = retainedWeight;
            }

            public String getMeasure() {
                return measure;
            }

            public void setMeasure(String measure) {
                this.measure = measure;
            }

            public String getFood() {
                return food;
            }

            public void setFood(String food) {
                this.food = food;
            }

            public String getFoodId() {
                return foodId;
            }

            public void setFoodId(String foodId) {
                this.foodId = foodId;
            }

            public String getMeasureURI() {
                return measureURI;
            }

            public void setMeasureURI(String measureURI) {
                this.measureURI = measureURI;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
}

