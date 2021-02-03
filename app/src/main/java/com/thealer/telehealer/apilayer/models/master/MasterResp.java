package com.thealer.telehealer.apilayer.models.master;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

public class MasterResp extends BaseApiResponseModel {

    @SerializedName("data")
    private List<MasterItem> data;


    public void setData(List<MasterItem> data) {
        this.data = data;
    }

    public List<MasterItem> getData() {
        return data;
    }

    public static class MasterItem {

        @SerializedName("image")
        private String image;

        @SerializedName("code")
        private String code;

        @SerializedName("is_active")
        private boolean isActive;

        @SerializedName("is_deleted")
        private boolean isDeleted;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("parent_id")
        private int parentId;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("id")
        private int id;

        public void setImage(String image) {
            this.image = image;
        }

        public String getImage() {
            return image;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setIsActive(boolean isActive) {
            this.isActive = isActive;
        }

        public boolean isIsActive() {
            return isActive;
        }

        public void setIsDeleted(boolean isDeleted) {
            this.isDeleted = isDeleted;
        }

        public boolean isIsDeleted() {
            return isDeleted;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public int getParentId() {
            return parentId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}