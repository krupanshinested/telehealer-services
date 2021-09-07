
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

        private Integer planId;
        private String name;
        private String description;
        private Integer price;
        private String currency;
        private String billingCycle;
        private Integer billingInterval;
        private Integer sequence;
        private Boolean isActive;
        private Integer rpmCount;
        private String createdAt;
        private String updatedAt;

        public Integer getPlanId() {
            return planId;
        }

        public void setPlanId(Integer planId) {
            this.planId = planId;
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

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getBillingCycle() {
            return billingCycle;
        }

        public void setBillingCycle(String billingCycle) {
            this.billingCycle = billingCycle;
        }

        public Integer getBillingInterval() {
            return billingInterval;
        }

        public void setBillingInterval(Integer billingInterval) {
            this.billingInterval = billingInterval;
        }

        public Integer getSequence() {
            return sequence;
        }

        public void setSequence(Integer sequence) {
            this.sequence = sequence;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public Integer getRpmCount() {
            return rpmCount;
        }

        public void setRpmCount(Integer rpmCount) {
            this.rpmCount = rpmCount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }
}