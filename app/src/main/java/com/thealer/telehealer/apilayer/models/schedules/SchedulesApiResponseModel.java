package com.thealer.telehealer.apilayer.models.schedules;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.PaginationCommonResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 18,December,2018
 */
public class SchedulesApiResponseModel extends PaginationCommonResponseModel {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean extends BaseApiResponseModel{

        private int schedule_id;
        private Object response_id;
        private String start;
        private String end;
        private int scheduled_by;
        private DetailBean detail;
        private int scheduled_with;
        private CommonUserApiResponseModel scheduled_with_user;
        private CommonUserApiResponseModel scheduled_by_user;
        private Object order_id;

        public int getSchedule_id() {
            return schedule_id;
        }

        public void setSchedule_id(int schedule_id) {
            this.schedule_id = schedule_id;
        }

        public Object getResponse_id() {
            return response_id;
        }

        public void setResponse_id(Object response_id) {
            this.response_id = response_id;
        }

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

        public int getScheduled_by() {
            return scheduled_by;
        }

        public void setScheduled_by(int scheduled_by) {
            this.scheduled_by = scheduled_by;
        }

        public DetailBean getDetail() {
            return detail;
        }

        public void setDetail(DetailBean detail) {
            this.detail = detail;
        }

        public int getScheduled_with() {
            return scheduled_with;
        }

        public void setScheduled_with(int scheduled_with) {
            this.scheduled_with = scheduled_with;
        }

        public Object getOrder_id() {
            return order_id;
        }

        public void setOrder_id(Object order_id) {
            this.order_id = order_id;
        }

        public CommonUserApiResponseModel getScheduled_with_user() {
            return scheduled_with_user;
        }

        public void setScheduled_with_user(CommonUserApiResponseModel scheduled_with_user) {
            this.scheduled_with_user = scheduled_with_user;
        }

        public CommonUserApiResponseModel getScheduled_by_user() {
            return scheduled_by_user;
        }

        public void setScheduled_by_user(CommonUserApiResponseModel scheduled_by_user) {
            this.scheduled_by_user = scheduled_by_user;
        }

        public CommonUserApiResponseModel getPatient() {
            if (scheduled_by_user.getRole().equals(Constants.ROLE_PATIENT)) {
                return scheduled_by_user;
            } else {
                return scheduled_with_user;
            }
         }
        public CommonUserApiResponseModel getDoctor() {
            if (scheduled_by_user.getRole().equals(Constants.ROLE_DOCTOR)) {
                return scheduled_by_user;
            } else {
                return scheduled_with_user;
            }
         }

        public static class DetailBean implements Serializable{

            private boolean change_demographic;
            private String reason;
            private boolean insurance_to_date;
            private boolean change_medical_info;
            private List<DatesBean> dates;

            public boolean isChange_demographic() {
                return change_demographic;
            }

            public void setChange_demographic(boolean change_demographic) {
                this.change_demographic = change_demographic;
            }

            public String getReason() {
                return reason;
            }

            public void setReason(String reason) {
                this.reason = reason;
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

            public List<DatesBean> getDates() {
                return dates;
            }

            public void setDates(List<DatesBean> dates) {
                this.dates = dates;
            }

            public static class DatesBean implements Serializable{

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

        public CommonUserApiResponseModel getScheduledToUser(){

            CommonUserApiResponseModel patientModel = null;
            CommonUserApiResponseModel doctorModel = null;

            if (getScheduled_by_user().getRole().equals(Constants.ROLE_PATIENT)){
                patientModel = getScheduled_by_user();
            }else {
                doctorModel = getScheduled_by_user();
            }

            if (getScheduled_with_user().getRole().equals(Constants.ROLE_PATIENT)){
                patientModel = getScheduled_with_user();
            }else {
                doctorModel = getScheduled_with_user();
            }

            if (UserType.isUserDoctor()){
                return patientModel;
            }else {
                return doctorModel;
            }

        }
    }
}
