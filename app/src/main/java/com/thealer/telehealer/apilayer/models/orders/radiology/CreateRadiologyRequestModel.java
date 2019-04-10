package com.thealer.telehealer.apilayer.models.orders.radiology;

import com.thealer.telehealer.views.home.orders.radiology.RadiologyListModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 12,December,2018
 */
public class CreateRadiologyRequestModel implements Serializable {

    private String user_guid;
    private String name = "X-Ray Referral";
    private String order_id;
    private DetailBean detail;

    public CreateRadiologyRequestModel() {
    }

    public CreateRadiologyRequestModel(String user_guid, String order_id, DetailBean detail) {
        this.user_guid = user_guid;
        this.order_id = order_id;
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

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public static class DetailBean implements Serializable{

        private String comment;
        private String requested_date;
        private CopyToBean copy_to;
        private List<LabsBean> labs;

        public DetailBean() {
        }

        public DetailBean(String comment, String requested_date, CopyToBean copy_to, List<LabsBean> labs) {
            this.comment = comment;
            this.requested_date = requested_date;
            this.copy_to = copy_to;
            this.labs = labs;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getRequested_date() {
            return requested_date;
        }

        public void setRequested_date(String requested_date) {
            this.requested_date = requested_date;
        }

        public CopyToBean getCopy_to() {
            return copy_to;
        }

        public void setCopy_to(CopyToBean copy_to) {
            this.copy_to = copy_to;
        }

        public List<LabsBean> getLabs() {
            return labs;
        }

        public void setLabs(List<LabsBean> labs) {
            this.labs = labs;
        }

        public static class CopyToBean implements Serializable{

            private String name;
            private String address;
            private String phone;
            private String npi;

            public CopyToBean() {
            }

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

        public static class LabsBean implements Serializable{

            private boolean stat;
            private List<String> ICD10_codes;
            private List<RadiologyListModel> XRayTests;

            public LabsBean() {
            }

            public LabsBean(boolean stat, List<String> ICD10_codes, List<RadiologyListModel> XRayTests) {
                this.stat = stat;
                this.ICD10_codes = ICD10_codes;
                this.XRayTests = XRayTests;
            }

            public boolean isStat() {
                return stat;
            }

            public void setStat(boolean stat) {
                this.stat = stat;
            }

            public List<String> getICD10_codes() {
                return ICD10_codes;
            }

            public void setICD10_codes(List<String> ICD10_codes) {
                this.ICD10_codes = ICD10_codes;
            }

            public List<RadiologyListModel> getXRayTests() {
                return XRayTests;
            }

            public void setXRayTests(List<RadiologyListModel> XRayTests) {
                this.XRayTests = XRayTests;
            }
        }
    }
}
