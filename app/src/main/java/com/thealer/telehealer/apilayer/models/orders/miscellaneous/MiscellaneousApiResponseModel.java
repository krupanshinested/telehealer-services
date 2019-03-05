package com.thealer.telehealer.apilayer.models.orders.miscellaneous;

import com.thealer.telehealer.apilayer.models.orders.OrdersApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCommonResultResponseModel;

import java.util.List;

/**
 * Created by Aswin on 05,March,2019
 */
public class MiscellaneousApiResponseModel extends OrdersApiResponseModel {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean extends OrdersCommonResultResponseModel {

        private Object doctor_id;
        private MiscellaneousDetailBean detail;

        public Object getDoctor_id() {
            return doctor_id;
        }

        public void setDoctor_id(Object doctor_id) {
            this.doctor_id = doctor_id;
        }

        public MiscellaneousDetailBean getDetail() {
            return detail;
        }

        public void setDetail(MiscellaneousDetailBean detail) {
            this.detail = detail;
        }
    }
}
