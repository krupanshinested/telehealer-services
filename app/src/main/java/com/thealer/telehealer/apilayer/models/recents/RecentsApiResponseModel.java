package com.thealer.telehealer.apilayer.models.recents;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 15,November,2018
 */
public class RecentsApiResponseModel extends BaseApiResponseModel implements Serializable {

    private int count;
    private Object prev;
    private Object next;
    private List<ResultBean> result;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getPrev() {
        return prev;
    }

    public void setPrev(Object prev) {
        this.prev = prev;
    }

    public Object getNext() {
        return next;
    }

    public void setNext(Object next) {
        this.next = next;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean extends BaseApiResponseModel implements Serializable{

        private UserBean patient;
        private UserBean doctor;
        private UserBean medical_assistant;
        private String corr_type;
        private int caller_id;
        private int callee_id;
        private Object doctor_id;
        private String updated_at;
        private String created_at;
        private String order_id;
        private int transcription_id;
        private String status;
        private String type;
        private String category;
        private String start_time;
        private Object cost;
        private int durationInSecs;

        public String getCorr_type() {
            return corr_type;
        }

        public void setCorr_type(String corr_type) {
            this.corr_type = corr_type;
        }

        public int getCaller_id() {
            return caller_id;
        }

        public void setCaller_id(int caller_id) {
            this.caller_id = caller_id;
        }

        public int getCallee_id() {
            return callee_id;
        }

        public void setCallee_id(int callee_id) {
            this.callee_id = callee_id;
        }

        public Object getDoctor_id() {
            return doctor_id;
        }

        public void setDoctor_id(Object doctor_id) {
            this.doctor_id = doctor_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public int getTranscription_id() {
            return transcription_id;
        }

        public void setTranscription_id(int transcription_id) {
            this.transcription_id = transcription_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public Object getCost() {
            return cost;
        }

        public void setCost(Object cost) {
            this.cost = cost;
        }

        public int getDurationInSecs() {
            return durationInSecs;
        }

        public void setDurationInSecs(int durationInSecs) {
            this.durationInSecs = durationInSecs;
        }

        public UserBean getPatient() {
            return patient;
        }

        public void setPatient(UserBean patient) {
            this.patient = patient;
        }

        public UserBean getDoctor() {
            return doctor;
        }

        public void setDoctor(UserBean doctor) {
            this.doctor = doctor;
        }

        public UserBean getMedical_assistant() {
            return medical_assistant;
        }

        public void setMedical_assistant(UserBean medical_assistant) {
            this.medical_assistant = medical_assistant;
        }

        public String getFormattedDuration() {
            int seconds = getDurationInSecs();
            if (seconds < 60) {
                return getDurationInSecs() + " secs";
            } else {
                return (seconds / 60) + " mins " + (seconds % 60) + " secs";
            }
        }

        public String getStartMonthYear() {
            return Utils.getFormatedDateTime(getStart_time(),"MMM yyyy");
        }
    }
}
