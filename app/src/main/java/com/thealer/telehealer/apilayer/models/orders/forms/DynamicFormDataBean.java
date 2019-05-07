package com.thealer.telehealer.apilayer.models.orders.forms;

import android.arch.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 07,May,2019
 */
public class DynamicFormDataBean extends ViewModel implements Serializable {

    private ScoreDetailsBean score_details;
    private List<DataBean> data = new ArrayList<>();
    private String total_score;

    public ScoreDetailsBean getScore_details() {
        return score_details;
    }

    public void setScore_details(ScoreDetailsBean score_details) {
        this.score_details = score_details;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public String getTotal_score() {
        return total_score;
    }

    public String getDisplayScore() {
        String displayScore = "N/A";

        if (total_score != null && !total_score.equals("null")) {
            Double score = Double.parseDouble(total_score);

            score = Math.floor(score);
            displayScore = String.valueOf(score.intValue());

//            double remainder = score % 1;
//            if (remainder == 0) {
//                displayScore = String.valueOf(score.intValue());
//            } else {
//                displayScore = String.valueOf(score);
//            }

        }
        return displayScore;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public static class ScoreDetailsBean implements Serializable {

        private String title;
        private String col1;
        private String col2;
        private PropertiesBean properties;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCol1() {
            return col1;
        }

        public void setCol1(String col1) {
            this.col1 = col1;
        }

        public String getCol2() {
            return col2;
        }

        public void setCol2(String col2) {
            this.col2 = col2;
        }

        public PropertiesBean getProperties() {
            return properties;
        }

        public void setProperties(PropertiesBean properties) {
            this.properties = properties;
        }

        public static class PropertiesBean implements Serializable {
            private List<ValuesBean> values;

            public List<ValuesBean> getValues() {
                return values;
            }

            public void setValues(List<ValuesBean> values) {
                this.values = values;
            }

            public static class ValuesBean implements Serializable {

                private String score;
                private String value;

                public String getScore() {
                    return score;
                }

                public void setScore(String score) {
                    this.score = score;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }

    public static class DataBean implements Serializable {
        private String title;
        private List<ItemsBean> items;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public static class ItemsBean implements Serializable {
            private String question;
            private String value;
            private Float score = null;
            private PropertiesBean properties;

            public String getQuestion() {
                return question;
            }

            public void setQuestion(String question) {
                this.question = question;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public PropertiesBean getProperties() {
                return properties;
            }

            public void setProperties(PropertiesBean properties) {
                this.properties = properties;
            }

            public Float getScore() {
                return score;
            }

            public void setScore(Float score) {
                this.score = score;
            }

            public static class PropertiesBean implements Serializable {

                private boolean is_required;
                private String type;
                private List<OptionsBean> options;

                public boolean isIs_required() {
                    return is_required;
                }

                public void setIs_required(boolean is_required) {
                    this.is_required = is_required;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public List<OptionsBean> getOptions() {
                    return options;
                }

                public void setOptions(List<OptionsBean> options) {
                    this.options = options;
                }

                public static class OptionsBean implements Serializable {

                    private String value;
                    private float score;

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public float getScore() {
                        return score;
                    }

                    public void setScore(float score) {
                        this.score = score;
                    }
                }
            }
        }
    }
}
