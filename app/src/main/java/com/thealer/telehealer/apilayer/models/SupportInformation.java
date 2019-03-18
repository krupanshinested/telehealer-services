package com.thealer.telehealer.apilayer.models;

public class SupportInformation {

    private int titleId;
    private int descriptionId;
    private int iconId;

    public SupportInformation(int titleId,int descriptionId,int iconId) {
        this.titleId = titleId;
        this.descriptionId = descriptionId;
        this.iconId = iconId;
    }

    public int getTitleId() {
        return titleId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    public int getIconId() {
        return iconId;
    }
}
