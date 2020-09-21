package com.thealer.telehealer.apilayer.models.orders.lab;

import com.thealer.telehealer.apilayer.models.orders.OrdersApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCommonResultResponseModel;

import java.util.List;

/**
 * Created by Aswin on 22,November,2018
 */
public class OrdersLabApiResponseModel extends OrdersApiResponseModel {

    private List<LabsResponseBean> result;

    public List<LabsResponseBean> getLabsResponseBeanList() {
        return result;
    }

    public void setLabsResponseBeanList(List<LabsResponseBean> labsResponseBeanList) {
        this.result = labsResponseBeanList;
    }

    public class LabsResponseBean extends OrdersCommonResultResponseModel {

        private LabsDetailBean detail;

        public LabsDetailBean getDetail() {
            return detail;
        }

        public void setDetail(LabsDetailBean detail) {
            this.detail = detail;
        }

    }
}
