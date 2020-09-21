package com.thealer.telehealer.views.home.orders.radiology;

import java.io.Serializable;

/**
 * Created by Aswin on 10,December,2018
 */
public class RadiologyListModel implements Serializable {

    private String id;
    private String header = null;
    private String subItem = null;
    private String selectAll = null;
    private int rlTypeString = 2;
    private boolean isRLType;
    private boolean isAdditionalTextRequired;
    private String additionalInformation = null;
    private String item = null;
    private String section = null;
    private int type;

    public RadiologyListModel() {
    }

    public RadiologyListModel(String header, String subItem, String selectAll, int rlTypeString,
                              boolean isRLType, boolean isAdditionalTextRequired, String additionalInformation, String item, String section) {
        this.header = header;
        this.subItem = subItem;
        this.selectAll = selectAll;
        this.rlTypeString = rlTypeString;
        this.isRLType = isRLType;
        this.isAdditionalTextRequired = isAdditionalTextRequired;
        this.additionalInformation = additionalInformation;
        this.item = item;
        this.section = section;
    }

    public String getId() {
        return id;
    }

    public void setId(int id) {
        this.id = String.valueOf(id);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSubItem() {
        return subItem;
    }

    public void setSubItem(String subItem) {
        this.subItem = subItem;
    }

    public String getSelectAll() {
        return selectAll;
    }

    public void setSelectAll(String selectAll) {
        this.selectAll = selectAll;
    }

    public int getRlTypeString() {
        return rlTypeString;
    }

    public void setRlTypeString(int rlTypeString) {
        this.rlTypeString = rlTypeString;
    }

    public boolean isIsRLType() {
        return isRLType;
    }

    public void setIsRLType(boolean isRLType) {
        this.isRLType = isRLType;
    }

    public boolean isIsAdditionalTextRequired() {
        return isAdditionalTextRequired;
    }

    public void setIsAdditionalTextRequired(boolean isAdditionalTextRequired) {
        this.isAdditionalTextRequired = isAdditionalTextRequired;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDisplayText() {
        if (isIsRLType()) {
            switch (getRlTypeString()) {
                case RadiologyConstants.RL_TYPE_R:
                    return getItem().replace("L", "");
                case RadiologyConstants.RL_TYPE_L:
                    return getItem().replace("R", "");
                default:
                    return getItem();
            }
        } else if (isIsAdditionalTextRequired()) {
            return getItem() + " (" + getAdditionalInformation() + ")";
        } else {
            return getItem();
        }
    }
}
