package com.thealer.telehealer.apilayer.models.orders.specialist;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 29,November,2018
 */
public class AssignSpecialistRequestModel extends BaseApiResponseModel {

    private String user_guid;
    private String name = "Test Specialist Referral";
    private DetailBean detail;

    public AssignSpecialistRequestModel(String user_guid, DetailBean detail) {
        this.user_guid = user_guid;
        this.detail = detail;
    }

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public static class DetailBean {

        private String description;
        private String specialist;
        private CopyToBean copy_to;

        public DetailBean(String description, String specialist, CopyToBean copy_to) {
            this.description = description;
            this.specialist = specialist;
            this.copy_to = copy_to;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSpecialist() {
            return specialist;
        }

        public void setSpecialist(String specialist) {
            this.specialist = specialist;
        }

        public CopyToBean getCopy_to() {
            return copy_to;
        }

        public void setCopy_to(CopyToBean copy_to) {
            this.copy_to = copy_to;
        }

        public static class CopyToBean {

            private String name;
            private String address;
            private String phone;
            private String npi;

            public CopyToBean(String name, String address, String phone, String npi) {
                this.name = name;
                this.address = address;
                this.phone = phone;
                this.npi = npi;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getNpi() {
                return npi;
            }

            public void setNpi(String npi) {
                this.npi = npi;
            }
        }
    }
}
