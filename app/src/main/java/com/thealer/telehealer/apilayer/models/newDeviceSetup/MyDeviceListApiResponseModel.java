package com.thealer.telehealer.apilayer.models.newDeviceSetup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.util.ArrayList;

public class MyDeviceListApiResponseModel extends BaseApiResponseModel {

    private String code;

    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private ArrayList<Devices> devices;

        public ArrayList<Devices> getDevices() {
            return devices;
        }

        public void setDevices(ArrayList<Devices> devices) {
            this.devices = devices;
        }
    }

    public class Devices
    {

        private String device_id;

        private String user_id;

        private Healthcare_device healthcare_device;

        private ArrayList<PhysicianNotification> physicianNotification = null;

        private String healthcare_device_id;

        private String termAndConditionLink;

        private String sms_enabled;

        private String created_at;

        private String id;

        public String gettermAndConditionLink() {
            return termAndConditionLink;
        }

        public void settermAndConditionLink(String termAndConditionLink) {
            this.termAndConditionLink = termAndConditionLink;
        }

        public String getsms_enabled() {
            return sms_enabled;
        }

        public void setsms_enabled(String sms_enabled) {
            this.sms_enabled = sms_enabled;
        }

        public String getcreated_at() {
            return created_at;
        }

        public void setcreated_at(String device_id) {
            this.created_at = created_at;
        }

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

            private String company_name;

            public String getCompany_name() {
                return company_name;
            }

            public void setCompany_name(String company_name) {
                this.company_name = company_name;
            }

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

        public ArrayList<PhysicianNotification> getPhysicianNotification() {
            return physicianNotification;
        }

        public void setPhysicianNotification(ArrayList<PhysicianNotification> physicianNotification) {
            this.physicianNotification = physicianNotification;
        }

        public class PhysicianNotification {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("healthcare_device_id")
            @Expose
            private Integer healthcareDeviceId;
            @SerializedName("user_id")
            @Expose
            private Integer userId;
            @SerializedName("user_healthcare_device_id")
            @Expose
            private Integer userHealthcareDeviceId;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getHealthcareDeviceId() {
                return healthcareDeviceId;
            }

            public void setHealthcareDeviceId(Integer healthcareDeviceId) {
                this.healthcareDeviceId = healthcareDeviceId;
            }

            public Integer getUserId() {
                return userId;
            }

            public void setUserId(Integer userId) {
                this.userId = userId;
            }

            public Integer getUserHealthcareDeviceId() {
                return userHealthcareDeviceId;
            }

            public void setUserHealthcareDeviceId(Integer userHealthcareDeviceId) {
                this.userHealthcareDeviceId = userHealthcareDeviceId;
            }

        }
    }
}
