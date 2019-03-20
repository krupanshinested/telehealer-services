package com.thealer.telehealer.apilayer.models.orders;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 22,November,2018
 */
public class OrdersPrescriptionApiResponseModel extends OrdersApiResponseModel {
    private List<OrdersResultBean> result;

    public List<OrdersResultBean> getOrdersResultBeanList() {
        return result;
    }

    public void setOrdersResultBeanList(List<OrdersResultBean> ordersResultBeanList) {
        this.result = ordersResultBeanList;
    }

    public static class OrdersResultBean extends OrdersCommonResultResponseModel{

        private DetailBean detail;

        public DetailBean getDetail() {
            return detail;
        }

        public void setDetail(DetailBean detail) {
            this.detail = detail;
        }

        public static class DetailBean implements Serializable {

            private String directions_select_two;
            private boolean label;
            private String directions_select_one;
            private int rx_strength;
            private String rx_drug_name;
            private String rx_form;
            private int directions_quantity;
            private int dispense_quantity;
            private boolean do_not_substitute;
            private int refill_quantity;
            private String rx_metric;

            public String getDirections_select_two() {
                return directions_select_two;
            }

            public void setDirections_select_two(String directions_select_two) {
                this.directions_select_two = directions_select_two;
            }

            public boolean isLabel() {
                return label;
            }

            public void setLabel(boolean label) {
                this.label = label;
            }

            public String getDirections_select_one() {
                return directions_select_one;
            }

            public void setDirections_select_one(String directions_select_one) {
                this.directions_select_one = directions_select_one;
            }

            public int getRx_strength() {
                return rx_strength;
            }

            public void setRx_strength(int rx_strength) {
                this.rx_strength = rx_strength;
            }

            public String getRx_drug_name() {
                return rx_drug_name;
            }

            public void setRx_drug_name(String rx_drug_name) {
                this.rx_drug_name = rx_drug_name;
            }

            public String getRx_form() {
                return rx_form;
            }

            public void setRx_form(String rx_form) {
                this.rx_form = rx_form;
            }

            public int getDirections_quantity() {
                return directions_quantity;
            }

            public void setDirections_quantity(int directions_quantity) {
                this.directions_quantity = directions_quantity;
            }

            public int getDispense_quantity() {
                return dispense_quantity;
            }

            public void setDispense_quantity(int dispense_quantity) {
                this.dispense_quantity = dispense_quantity;
            }

            public boolean isDo_not_substitute() {
                return do_not_substitute;
            }

            public void setDo_not_substitute(boolean do_not_substitute) {
                this.do_not_substitute = do_not_substitute;
            }

            public int getRefill_quantity() {
                return refill_quantity;
            }

            public void setRefill_quantity(int refill_quantity) {
                this.refill_quantity = refill_quantity;
            }

            public String getRx_metric() {
                return rx_metric;
            }

            public void setRx_metric(String rx_metric) {
                this.rx_metric = rx_metric;
            }

            public String getStrength(){
                return getRx_strength()+" "+getRx_metric();
            }

            public String getDirection(){
                return getDirections_quantity()+" "+getRx_form()+" ("+getDirections_select_one()+" "+getDirections_select_two()+") ";
            }

            public String getDispense(){
                return getDispense_quantity()+" "+getRx_form();
            }

            public String getRefil(){
                return getRefill_quantity()+" times";
            }
        }

    }
}
