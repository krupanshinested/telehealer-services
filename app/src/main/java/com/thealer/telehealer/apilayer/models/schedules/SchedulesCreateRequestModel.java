package com.thealer.telehealer.apilayer.models.schedules;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 21,December,2018
 */
public class SchedulesCreateRequestModel {

    private String requestee_id;
    private String type = "appointment";
    private String message;
    private Requestdetails detail;

    public String getRequestee_id() {
        return requestee_id;
    }

    public void setRequestee_id(String requestee_id) {
        this.requestee_id = requestee_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Requestdetails getDetail() {
        return detail;
    }

    public void setDetail(Requestdetails detail) {
        this.detail = detail;
    }

    public static class Requestdetails {
        private List<Dates> dates = new ArrayList<>();
        private String reason;
        private boolean change_demographic;
        private boolean insurance_to_date;
        private boolean change_medical_info;

        public List<Dates> getDates() {
            return dates;
        }

        public void setDates(List<Dates> dates) {
            this.dates = dates;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public boolean isChange_demographic() {
            return change_demographic;
        }

        public void setChange_demographic(boolean change_demographic) {
            this.change_demographic = change_demographic;
        }

        public boolean isInsurance_to_date() {
            return insurance_to_date;
        }

        public void setInsurance_to_date(boolean insurance_to_date) {
            this.insurance_to_date = insurance_to_date;
        }

        public boolean isChange_medical_info() {
            return change_medical_info;
        }

        public void setChange_medical_info(boolean change_medical_info) {
            this.change_medical_info = change_medical_info;
        }

        public static class Dates {
            private String start;
            private String end;

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getEnd() {
                return end;
            }

            public void setEnd(String end) {
                this.end = end;
            }
        }
    }
}
