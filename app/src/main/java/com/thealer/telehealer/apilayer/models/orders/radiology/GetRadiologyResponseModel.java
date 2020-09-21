package com.thealer.telehealer.apilayer.models.orders.radiology;

import com.thealer.telehealer.apilayer.models.orders.FaxesBean;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCommonResultResponseModel;
import com.thealer.telehealer.views.home.orders.radiology.RadiologyListModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 12,December,2018
 */
public class GetRadiologyResponseModel extends OrdersApiResponseModel {
    private List<ResultBean> result;

    public List<ResultBean> getResultBeanList() {
        return result;
    }

    public void setResultBeanList(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean extends OrdersCommonResultResponseModel {

        private DetailBean detail;
        private List<FaxesBean> faxes;

        public DetailBean getDetail() {
            return detail;
        }

        public void setDetail(DetailBean detail) {
            this.detail = detail;
        }

        public List<FaxesBean> getFaxes() {
            return faxes;
        }

        public void setFaxes(List<FaxesBean> faxes) {
            this.faxes = faxes;
        }

        public static class DetailBean implements Serializable {

            private String comment;
            private CopyToBean copy_to;
            private String requested_date;
            private List<LabsBean> labs;

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public CopyToBean getCopy_to() {
                return copy_to;
            }

            public void setCopy_to(CopyToBean copy_to) {
                this.copy_to = copy_to;
            }

            public String getRequested_date() {
                return requested_date;
            }

            public void setRequested_date(String requested_date) {
                this.requested_date = requested_date;
            }

            public List<LabsBean> getLabs() {
                return labs;
            }

            public void setLabs(List<LabsBean> labs) {
                this.labs = labs;
            }

            public static class CopyToBean implements Serializable {

                private String address;
                private String name;
                private String npi;
                private String phone;

                public String getAddress() {
                    return address;
                }

                public void setAddress(String address) {
                    this.address = address;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getNpi() {
                    return npi;
                }

                public void setNpi(String npi) {
                    this.npi = npi;
                }

                public String getPhone() {
                    return phone;
                }

                public void setPhone(String phone) {
                    this.phone = phone;
                }
            }

            public static class LabsBean implements Serializable {

                private boolean stat;
                private List<String> ICD10_codes;
                private List<RadiologyListModel> XRayTests;

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
}
