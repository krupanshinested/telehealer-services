package com.thealer.telehealer.apilayer.models.vitals;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;

/**
 * Created by Aswin on 21,November,2018
 */
public class VitalsApiResponseModel extends BaseApiResponseModel {

    private int user_vital_id;
    private Object value;
    private StethBean stethBean;
    private String type;
    private String display_name;
    private String mode;
    private int user_id;
    private String created_at;
    private String updated_at;

    public int getUser_vital_id() {
        return user_vital_id;
    }

    public void setUser_vital_id(int user_vital_id) {
        this.user_vital_id = user_vital_id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public StethBean getStethBean() {
        return new Gson().fromJson(new Gson().toJson(getValue()), StethBean.class);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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

    public String getCapturedBy() {
        String prefix = "Captured by ";
        if (getMode().equals(VitalsConstant.VITAL_MODE_DEVICE)) {
            return prefix + " " + VitalsConstant.LABLE_DEVICE.toLowerCase();
        } else {
            if (UserType.isUserPatient()) {
                if (getMode().equals(VitalsConstant.VITAL_MODE_PATIENT)) {
                    return prefix + " yourself";
                } else if (getDisplay_name() != null && !getDisplay_name().isEmpty()) {
                    return prefix + " " + getDisplay_name();
                } else {
                    return prefix + " " + VitalsConstant.LABLE_DOCTOR;
                }
            } else {
                if (getDisplay_name() != null && !getDisplay_name().isEmpty()) {
                    return prefix + " " + getDisplay_name();
                } else if (getMode().equals(VitalsConstant.VITAL_MODE_DOCTOR)) {
                    return prefix + " " + VitalsConstant.LABLE_DOCTOR;
                } else if (getMode().equals(VitalsConstant.VITAL_MODE_PATIENT)) {
                    return prefix + " " + VitalsConstant.LABLE_PATIENT;
                } else {
                    return "";
                }
            }
        }
    }

}
