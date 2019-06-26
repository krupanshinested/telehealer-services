package com.thealer.telehealer.apilayer.models.vitals;

import android.content.Context;

import com.google.gson.Gson;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
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
    private boolean abnormal;
    private String order_id;

    private String date;
    private String bundle_id;

    public VitalsApiResponseModel(String type,String value,String display_name, String mode,String date,String bundle_id) {
        this.type = type;
        this.display_name = display_name;
        this.value = value;
        this.mode = mode;
        this.date = date;
        this.bundle_id = bundle_id;
    }

    public VitalsApiResponseModel() {

    }

    public boolean isAbnormal() {
        return abnormal;
    }

    public void setAbnormal(boolean abnormal) {
        this.abnormal = abnormal;
    }

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
        if (date != null) {
            return date;
        } else {
            return created_at;
        }
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCapturedBy(Context context) {
        String prefix = context.getString(R.string.captured_by);
        if (bundle_id != null &&  !bundle_id.equals(BuildConfig.APPLICATION_ID)) {
            return String.format(context.getString(R.string.captured_via),display_name);
        } else if (getMode().equals(VitalsConstant.VITAL_MODE_DEVICE)) {
            return prefix + " " + context.getString(VitalsConstant.LABLE_DEVICE).toLowerCase();
        } else {
            if (UserType.isUserPatient()) {
                if (getMode().equals(VitalsConstant.VITAL_MODE_PATIENT)) {
                    return prefix + " " + context.getString(R.string.yourself);
                } else if (getDisplay_name() != null && !getDisplay_name().isEmpty()) {
                    return prefix + " " + getDisplay_name();
                } else {
                    return prefix + " " + context.getString(VitalsConstant.LABLE_DOCTOR);
                }
            } else {
                if (getDisplay_name() != null && !getDisplay_name().isEmpty()) {
                    return prefix + " " + getDisplay_name();
                } else if (getMode().equals(VitalsConstant.VITAL_MODE_DOCTOR)) {
                    return prefix + " " + context.getString(VitalsConstant.LABLE_DOCTOR);
                } else if (getMode().equals(VitalsConstant.VITAL_MODE_PATIENT)) {
                    return prefix + " " + context.getString(VitalsConstant.LABLE_PATIENT);
                } else {
                    return "";
                }
            }
        }
    }

    public int getStethIoImage() {
        boolean isContainsHeart = false, isContainsLung = false;

        if (stethBean != null && stethBean.getSegments() != null) {
            for (int j = 0; j < stethBean.getSegments().size(); j++) {
                if (stethBean.getSegments().get(j).getFilter_type().equals(VitalsConstant.heart)) {
                    isContainsHeart = true;
                }
                if (stethBean.getSegments().get(j).getFilter_type().equals(VitalsConstant.lung)) {
                    isContainsLung = true;
                }

                if (isContainsHeart && isContainsLung) {
                    break;
                }
            }
        }
        int drawable = R.drawable.steth_heart_lung;

        if (isContainsHeart && !isContainsLung) {
            drawable = R.drawable.steth_heart;
        } else if (!isContainsHeart && isContainsLung) {
            drawable = R.drawable.steth_lung;
        }

        return drawable;
    }
}
