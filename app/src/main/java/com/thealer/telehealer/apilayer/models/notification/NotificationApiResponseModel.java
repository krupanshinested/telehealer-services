package com.thealer.telehealer.apilayer.models.notification;

import com.thealer.telehealer.apilayer.models.PaginationCommonResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 07,January,2019
 */
public class NotificationApiResponseModel extends PaginationCommonResponseModel implements Serializable {

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }


    public static class ResultBean implements Serializable {
        private int count;
        private int unread_count;
        private List<RequestsBean> requests = new ArrayList<>();

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getUnread_count() {
            return unread_count;
        }

        public void setUnread_count(int unread_count) {
            this.unread_count = unread_count;
        }

        public List<RequestsBean> getRequests() {
            return requests;
        }

        public void setRequests(List<RequestsBean> requests) {
            this.requests = requests;
        }

        public static class RequestsBean implements Serializable {

            private int request_id;
            private boolean requestor_read_status;
            private boolean requestee_read_status;
            private String type;
            private Integer entity_id;
            private String status;
            private boolean is_requestee;
            private String message;
            private DetailBean detail;
            private String created_at;
            private String updated_at;
            private CommonUserApiResponseModel requestor;
            private CommonUserApiResponseModel requestee;

            public int getRequest_id() {
                return request_id;
            }

            public void setRequest_id(int request_id) {
                this.request_id = request_id;
            }

            public boolean isRequestor_read_status() {
                return requestor_read_status;
            }

            public void setRequestor_read_status(boolean requestor_read_status) {
                this.requestor_read_status = requestor_read_status;
            }

            public boolean isRequestee_read_status() {
                return requestee_read_status;
            }

            public void setRequestee_read_status(boolean requestee_read_status) {
                this.requestee_read_status = requestee_read_status;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public Integer getEntity_id() {
                return entity_id;
            }

            public void setEntity_id(Integer entity_id) {
                this.entity_id = entity_id;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public boolean isIs_requestee() {
                return is_requestee;
            }

            public void setIs_requestee(boolean is_requestee) {
                this.is_requestee = is_requestee;
            }

            public DetailBean getDetail() {
                return detail;
            }

            public void setDetail(DetailBean detail) {
                this.detail = detail;
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

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public CommonUserApiResponseModel getRequestor() {
                return requestor;
            }

            public void setRequestor(CommonUserApiResponseModel requestor) {
                this.requestor = requestor;
            }

            public CommonUserApiResponseModel getRequestee() {
                return requestee;
            }

            public void setRequestee(CommonUserApiResponseModel requestee) {
                this.requestee = requestee;
            }

            public CommonUserApiResponseModel getDoctorModel() {
                if (getRequestor().getRole().equals(Constants.ROLE_DOCTOR)) {
                    return requestor;
                } else if (getRequestee().getRole().equals(Constants.ROLE_DOCTOR)) {
                    return requestee;
                } else {
                    return null;
                }
            }

            public CommonUserApiResponseModel getPatientModel() {
                if (getRequestor().getRole().equals(Constants.ROLE_PATIENT)) {
                    return requestor;
                } else if (getRequestee().getRole().equals(Constants.ROLE_PATIENT)) {
                    return requestee;
                } else {
                    return null;
                }
            }

            public CommonUserApiResponseModel getOtherUserModel() {
                WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
                if (!getRequestee().getUser_guid().equals(whoAmIApiResponseModel.getUser_guid())) {
                    return  getRequestee();
                } else if (!getRequestor().getUser_guid().equals(whoAmIApiResponseModel.getUser_guid())) {
                    return getRequestor();
                } else {
                    CommonUserApiResponseModel patient = getPatientModel();
                    CommonUserApiResponseModel doctor = getDoctorModel();
                    if (patient != null) {
                        return patient;
                    } else if (doctor.getUser_guid().equals(requestee.getUser_guid())) {
                        return requestor;
                    } else {
                        return requestee;
                    }
                }
            }

            public boolean isOwnNotification() {
                int id = UserDetailPreferenceManager.getWhoAmIResponse().getUser_id();
                return getRequestor().getUser_id() == id || getRequestee().getUser_id() == id;
            }

            public static class DetailBean implements Serializable {


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

                public static class DatesBean implements Serializable {

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

    }
}
