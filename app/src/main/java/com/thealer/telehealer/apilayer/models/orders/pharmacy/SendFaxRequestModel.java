package com.thealer.telehealer.apilayer.models.orders.pharmacy;

import java.io.Serializable;

/**
 * Created by Aswin on 30,November,2018
 */
public class SendFaxRequestModel implements Serializable {

    private String fax_number;
    private String referral_id;
    private DetailBean detail;

    public SendFaxRequestModel(String fax_number, String referral_id, DetailBean detail) {
        this.fax_number = fax_number;
        this.referral_id = referral_id;
        this.detail = detail;
    }

    public String getFax_number() {
        return fax_number;
    }

    public void setFax_number(String fax_number) {
        this.fax_number = fax_number;
    }

    public String getReferral_id() {
        return referral_id;
    }

    public void setReferral_id(String referral_id) {
        this.referral_id = referral_id;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public static class DetailBean implements Serializable{

        private GetPharmaciesApiResponseModel.ResultsBean pharmacy;

        public DetailBean(GetPharmaciesApiResponseModel.ResultsBean pharmacy) {
            this.pharmacy = pharmacy;
        }

        public GetPharmaciesApiResponseModel.ResultsBean getPharmacy() {
            return pharmacy;
        }

        public void setPharmacy(GetPharmaciesApiResponseModel.ResultsBean pharmacy) {
            this.pharmacy = pharmacy;
        }
    }
}
