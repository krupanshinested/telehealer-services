package com.thealer.telehealer.apilayer.models;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;

public class DefaultPhysicianModel extends BaseApiResponseModel implements Serializable {
    @SerializedName("data")
    private DefaultPhysician data;

    public DefaultPhysician getData() {
        return data;
    }

    public void setData(DefaultPhysician value) {
        this.data = value;
    }

    public class DefaultPhysician {
        @SerializedName("id")
        private Long id;
        @SerializedName("patient_id")
        private Long patientID;
        @SerializedName("physician_id")
        private Long physicianID;
        @SerializedName("is_active")
        private Boolean isActive;
        @SerializedName("deleted_at")
        private Object deletedAt;

        public Long getID() {
            return id;
        }

        public void setID(Long value) {
            this.id = value;
        }

        public Long getPatientID() {
            return patientID;
        }

        public void setPatientID(Long value) {
            this.patientID = value;
        }

        public Long getPhysicianID() {
            return physicianID;
        }

        public void setPhysicianID(Long value) {
            this.physicianID = value;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean value) {
            this.isActive = value;
        }

        public Object getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Object value) {
            this.deletedAt = value;
        }
    }
}

