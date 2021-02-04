package com.thealer.telehealer.apilayer.models.transaction;

public class ReasonOption {

    private String title;

    private int value;

    public ReasonOption(int value, String title) {
        this.value = value;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
