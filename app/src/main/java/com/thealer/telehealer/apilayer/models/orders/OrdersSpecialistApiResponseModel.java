package com.thealer.telehealer.apilayer.models.orders;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 26,November,2018
 */
public class OrdersSpecialistApiResponseModel extends OrdersApiResponseModel {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean extends OrdersCommonResultResponseModel {

        private DetailBean detail;

        public DetailBean getDetail() {
            return detail;
        }

        public void setDetail(DetailBean detail) {
            this.detail = detail;
        }

        public static class DetailBean implements Serializable {

            private CopyToBean copy_to;
            private String description;
            private String specialist;

            public CopyToBean getCopy_to() {
                return copy_to;
            }

            public void setCopy_to(CopyToBean copy_to) {
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
        }
    }
}
