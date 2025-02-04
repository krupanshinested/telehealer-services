package com.thealer.telehealer.views.home.orders;

import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.models.orders.OrdersCommonResultResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;

/**
 * Created by Aswin on 22,November,2018
 */
public class OrdersDetailListAdapterModel {
    private String avatar;
    private String title;
    private String subTitle;
    private OrdersCommonResultResponseModel commonResultResponseModel;
    private OrdersUserFormsApiResponseModel ordersUserFormsApiResponseModel;
    private boolean isForm;

    @Nullable
    private String otherImageUrl;

    public boolean isForm() {
        return isForm;
    }

    public void setForm(boolean form) {
        isForm = form;
    }

    public OrdersUserFormsApiResponseModel getOrdersFormsApiResponseModel() {
        return ordersUserFormsApiResponseModel;
    }

    public void setOrdersFormsApiResponseModel(OrdersUserFormsApiResponseModel ordersFormsApiResponseModel) {
        this.ordersUserFormsApiResponseModel = ordersFormsApiResponseModel;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public OrdersCommonResultResponseModel getCommonResultResponseModel() {
        return commonResultResponseModel;
    }

    public void setCommonResultResponseModel(OrdersCommonResultResponseModel commonResultResponseModel) {
        this.commonResultResponseModel = commonResultResponseModel;
    }

    public OrdersUserFormsApiResponseModel getOrdersUserFormsApiResponseModel() {
        return ordersUserFormsApiResponseModel;
    }

    public void setOrdersUserFormsApiResponseModel(OrdersUserFormsApiResponseModel ordersUserFormsApiResponseModel) {
        this.ordersUserFormsApiResponseModel = ordersUserFormsApiResponseModel;
    }

    @Nullable
    public String getOtherImageUrl() {
        return otherImageUrl;
    }

    public void setOtherImageUrl(@Nullable String otherImageUrl) {
        this.otherImageUrl = otherImageUrl;
    }
}
