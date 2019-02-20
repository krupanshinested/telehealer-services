package com.thealer.telehealer.apilayer.models.diet.food;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 21,February,2019
 */
public class FoodListApiResponseModel extends BaseApiResponseModel implements Serializable {

    private String text;
    private LinksBean _links;
    private List<HintsBean> hints;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LinksBean get_links() {
        return _links;
    }

    public void set_links(LinksBean _links) {
        this._links = _links;
    }

    public List<HintsBean> getHints() {
        return hints;
    }

    public void setHints(List<HintsBean> hints) {
        this.hints = hints;
    }

    public static class LinksBean implements Serializable {

        private NextBean next;

        public NextBean getNext() {
            return next;
        }

        public void setNext(NextBean next) {
            this.next = next;
        }

        public static class NextBean implements Serializable {

            private String title;
            private String href;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }
        }
    }

    public static class HintsBean implements Serializable {

        private FoodBean food;
        private List<MeasuresBean> measures;

        public FoodBean getFood() {
            return food;
        }

        public void setFood(FoodBean food) {
            this.food = food;
        }

        public List<MeasuresBean> getMeasures() {
            return measures;
        }

        public void setMeasures(List<MeasuresBean> measures) {
            this.measures = measures;
        }

        public static class FoodBean implements Serializable {

            private String foodId;
            private String uri;
            private String label;
            private NutrientsBean nutrients;
            private String brand;
            private String category;
            private String categoryLabel;
            private String foodContentsLabel;
            private String image;

            public String getFoodId() {
                return foodId;
            }

            public void setFoodId(String foodId) {
                this.foodId = foodId;
            }

            public String getUri() {
                return uri;
            }

            public void setUri(String uri) {
                this.uri = uri;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public NutrientsBean getNutrients() {
                return nutrients;
            }

            public void setNutrients(NutrientsBean nutrients) {
                this.nutrients = nutrients;
            }

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public String getCategoryLabel() {
                return categoryLabel;
            }

            public void setCategoryLabel(String categoryLabel) {
                this.categoryLabel = categoryLabel;
            }

            public String getFoodContentsLabel() {
                return foodContentsLabel;
            }

            public void setFoodContentsLabel(String foodContentsLabel) {
                this.foodContentsLabel = foodContentsLabel;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public static class NutrientsBean implements Serializable {

                private double ENERC_KCAL;
                private double PROCNT;
                private double FAT;
                private double CHOCDF;
                private double FIBTG;

                public double getENERC_KCAL() {
                    return ENERC_KCAL;
                }

                public void setENERC_KCAL(double ENERC_KCAL) {
                    this.ENERC_KCAL = ENERC_KCAL;
                }

                public double getPROCNT() {
                    return PROCNT;
                }

                public void setPROCNT(double PROCNT) {
                    this.PROCNT = PROCNT;
                }

                public double getFAT() {
                    return FAT;
                }

                public void setFAT(double FAT) {
                    this.FAT = FAT;
                }

                public double getCHOCDF() {
                    return CHOCDF;
                }

                public void setCHOCDF(double CHOCDF) {
                    this.CHOCDF = CHOCDF;
                }

                public double getFIBTG() {
                    return FIBTG;
                }

                public void setFIBTG(double FIBTG) {
                    this.FIBTG = FIBTG;
                }
            }
        }

        public static class MeasuresBean implements Serializable {

            private String uri;
            private String label;

            public String getUri() {
                return uri;
            }

            public void setUri(String uri) {
                this.uri = uri;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }
        }
    }
}
