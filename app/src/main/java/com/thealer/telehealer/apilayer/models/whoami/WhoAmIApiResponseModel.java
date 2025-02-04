package com.thealer.telehealer.apilayer.models.whoami;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

/**
 * Created by Aswin on 11,November,2018
 */
public class WhoAmIApiResponseModel extends CommonUserApiResponseModel {

    private String version;
    private String user_activated;
    private String install_type;
    private boolean orders_enabled;
    private boolean integration_requests;
    private boolean isTrialExpired = false;
    private boolean isPlanActive = false;
    private boolean isCancelledPlan = false;
    private boolean first_time_subscription_purchased=false;

    public boolean getisTrialExpired() {
        return isTrialExpired;
    }

    public void setisTrialExpired(boolean isTrialExpired) {
        this.isTrialExpired = isTrialExpired;
    }

    public boolean getisPlanActive() {
        return isPlanActive;
    }

    public void setisPlanActive(boolean isPlanActive) {
        this.isPlanActive = isPlanActive;
    }

    public boolean getisCancelledPlan() {
        return isCancelledPlan;
    }

    public void setisCancelledPlan(boolean isCancelledPlan) {
        this.isCancelledPlan = isCancelledPlan;
    }

    public boolean isFirst_time_subscription_purchased() {
        return first_time_subscription_purchased;
    }

    public void setFirst_time_subscription_purchased(boolean first_time_subscription_purchased) {
        this.first_time_subscription_purchased = first_time_subscription_purchased;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUser_activated() {
        return user_activated;
    }

    public void setUser_activated(String user_activated) {
        this.user_activated = user_activated;
    }

    public String getInstall_type() {
        return install_type;
    }

    public void setInstall_type(String install_type) {
        this.install_type = install_type;
    }

    public boolean getOrders_enabled() {
        return orders_enabled;
    }

    public void setOrders_enabled(Boolean orders_enabled) {
        this.orders_enabled = orders_enabled;
    }

    public boolean getIntegration_requests() {
        return integration_requests;
    }

    public void setIntegration_requests(Boolean integration_requests) {
        this.integration_requests = integration_requests;
    }
}
