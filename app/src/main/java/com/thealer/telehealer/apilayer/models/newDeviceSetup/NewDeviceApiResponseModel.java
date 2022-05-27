package com.thealer.telehealer.apilayer.models.newDeviceSetup;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import java.util.ArrayList;

public class NewDeviceApiResponseModel extends BaseApiResponseModel {

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

        private String image;

        private String product_info_link;

        private String is_active;

        private String updated_at;

        private String company_name;

        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        private String name;

        private String GUID;

        private String description;

        private String product_info_link_1;

        private String created_at;

        private String id;

        private String deleted_at;

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

        public String getIs_active() {
            return is_active;
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getName() {
            return name;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getProduct_info_link_1() {
            return product_info_link_1;
        }

        public void setProduct_info_link_1(String product_info_link_1) {
            this.product_info_link_1 = product_info_link_1;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(String deleted_at) {
            this.deleted_at = deleted_at;
        }
    }
}
