package com.thealer.telehealer.apilayer.models.commonResponseModel;

import java.io.Serializable;

/**
 * Created by Aswin on 26,November,2018
 */
public class HistoryBean implements Serializable {

    private boolean isYes;
    private String question;

    public HistoryBean() {
    }

    public HistoryBean(boolean isYes, String question) {
        this.isYes = isYes;
        this.question = question;
    }

    public boolean isIsYes() {
        return isYes;
    }

    public void setIsYes(boolean isYes) {
        this.isYes = isYes;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}

