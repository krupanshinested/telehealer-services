package com.thealer.telehealer.apilayer.models.orders.documents;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 29,November,2018
 */
public class DocumentsApiResponseModel extends OrdersApiResponseModel {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean extends BaseApiResponseModel implements Serializable {

        private int user_file_id;
        private String name;
        private String path;
        private String created_at;
        private String updated_at;
        private UserBean creator;
        private UserBean doctor;
        private String order_id;

        public int getUser_file_id() {
            return user_file_id;
        }

        public void setUser_file_id(int user_file_id) {
            this.user_file_id = user_file_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
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

        public UserBean getCreator() {
            return creator;
        }

        public void setCreator(UserBean creator) {
            this.creator = creator;
        }

        public UserBean getDoctor() {
            return doctor;
        }

        public void setDoctor(UserBean doctor) {
            this.doctor = doctor;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }
    }
}
