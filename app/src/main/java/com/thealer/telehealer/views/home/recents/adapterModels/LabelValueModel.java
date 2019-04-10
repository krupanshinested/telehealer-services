package com.thealer.telehealer.views.home.recents.adapterModels;

import java.io.Serializable;

/**
 * Created by Aswin on 26,April,2019
 */
public class LabelValueModel implements Serializable {
    private String label;
    private String value;
    private boolean showBottomView;

    public LabelValueModel(String label, String value, boolean showBottomView) {
        this.label = label;
        this.value = value;
        this.showBottomView = showBottomView;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isShowBottomView() {
        return showBottomView;
    }

    public void setShowBottomView(boolean showBottomView) {
        this.showBottomView = showBottomView;
    }
}
