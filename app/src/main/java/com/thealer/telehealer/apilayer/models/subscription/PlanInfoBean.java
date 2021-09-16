
package com.thealer.telehealer.apilayer.models.subscription;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.util.List;

public class PlanInfoBean extends BaseApiResponseModel {

    private Boolean flag;
    private List<Result> results = null;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }


    public class Result {

        private String plan_id;
        private String name;
        private String description;
        private String price;
        private String currency;
        private String billing_cycle;
        private String billing_interval;
        private String sequence;
        private Boolean is_active;
        private String rpm_count;
        private String created_at;
        private String updated_at;
        private boolean isPurchased=false;
        private boolean canReshedule=false;
        private boolean isSelected=false;
        private boolean isResubscribe=false;
        private boolean isUnsubscribe=false;

        public boolean isPurchased() {
            return isPurchased;
        }

        public void setPurchased(boolean purchased) {
            isPurchased = purchased;
        }

        public boolean isCanReshedule() {
            return canReshedule;
        }

        public void setCanReshedule(boolean canReshedule) {
            this.canReshedule = canReshedule;
        }

        public boolean isUnsubscribe() {
            return isUnsubscribe;
        }

        public void setUnsubscribe(boolean unsubscribe) {
            isUnsubscribe = unsubscribe;
        }

        public boolean isResubscribe() {
            return isResubscribe;
        }

        public void setResubscribe(boolean resubscribe) {
            isResubscribe = resubscribe;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public String getPlan_id() {
            return plan_id;
        }

        public void setPlan_id(String plan_id) {
            this.plan_id = plan_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getBilling_cycle() {
            return billing_cycle;
        }

        public void setBilling_cycle(String billing_cycle) {
            this.billing_cycle = billing_cycle;
        }

        public String getBilling_interval() {
            return billing_interval;
        }

        public void setBilling_interval(String billing_interval) {
            this.billing_interval = billing_interval;
        }

        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
        }

        public Boolean getIs_active() {
            return is_active;
        }

        public void setIs_active(Boolean is_active) {
            this.is_active = is_active;
        }

        public String getRpm_count() {
            return rpm_count;
        }

        public void setRpm_count(String rpm_count) {
            this.rpm_count = rpm_count;
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
    }
}