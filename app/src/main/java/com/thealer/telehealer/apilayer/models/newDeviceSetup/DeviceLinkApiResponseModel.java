package com.thealer.telehealer.apilayer.models.newDeviceSetup;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.util.ArrayList;

public class DeviceLinkApiResponseModel extends BaseApiResponseModel {

    private String code;

    private ArrayList<Data> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public static class Data {

        private String device_id;

        private String user_id;

        private Healthcare_device healthcare_device;

        private String healthcare_device_id;

        private String id;

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public Healthcare_device getHealthcare_device() {
            return healthcare_device;
        }

        public void setHealthcare_device(Healthcare_device healthcare_device) {
            this.healthcare_device = healthcare_device;
        }

        public String getHealthcare_device_id() {
            return healthcare_device_id;
        }

        public void setHealthcare_device_id(String healthcare_device_id) {
            this.healthcare_device_id = healthcare_device_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public class Healthcare_device {
            private String image;

            private String product_info_link;

            private String name;

            private String description;

            private String GUID;

            private String product_info_link_1;

            private String id;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getProduct_info_link() {
                return product_info_link;
            }

            public void setProduct_info_link(String product_info_link) {
                this.product_info_link = product_info_link;
            }

            public String getName() {
                return name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getGUID() {
                return GUID;
            }

            public void setGUID(String GUID) {
                this.GUID = GUID;
            }

            public String getProduct_info_link_1() {
                return product_info_link_1;
            }

            public void setProduct_info_link_1(String product_info_link_1) {
                this.product_info_link_1 = product_info_link_1;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
