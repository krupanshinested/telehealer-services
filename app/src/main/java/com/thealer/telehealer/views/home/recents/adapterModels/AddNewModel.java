package com.thealer.telehealer.views.home.recents.adapterModels;

import java.io.Serializable;

/**
 * Created by Aswin on 26,April,2019
 */
public class AddNewModel implements Serializable {
    private String title;
    private int type;

    public AddNewModel(String title, int type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
