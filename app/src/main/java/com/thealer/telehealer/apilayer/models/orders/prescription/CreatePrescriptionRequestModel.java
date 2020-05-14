package com.thealer.telehealer.apilayer.models.orders.prescription;

import java.io.Serializable;

/**
 * Created by Aswin on 30,November,2018
 */
public class CreatePrescriptionRequestModel implements Serializable {

    private String user_guid;
    private String name;
    private String order_id;
    private DetailBean detail;


    public CreatePrescriptionRequestModel(String user_guid, String name, String order_id, DetailBean detail) {
        this.user_guid = user_guid;
        this.name = name;
        this.order_id = order_id;
        this.detail = detail;
    }

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public static class DetailBean implements Serializable {

        private String rx_drug_name;
        private float rx_strength;
        private String rx_metric;
        private String rx_form;
        private float directions_quantity;
        private String directions_select_one;
        private String directions_select_two;
        private float dispense_quantity;
        private int refill_quantity;
        private boolean do_not_substitute;
        private boolean label;

        public DetailBean(String rx_drug_name, String rx_strength, String rx_metric, String rx_form, String directions_quantity, String directions_select_one, String directions_select_two, String dispense_quantity, String refill_quantity, boolean do_not_substitute, boolean label) {
            this.rx_drug_name = rx_drug_name;
            this.rx_strength = Float.parseFloat(rx_strength);
            this.rx_metric = rx_metric;
            this.rx_form = rx_form;
            this.directions_quantity = Float.parseFloat(directions_quantity);
            this.directions_select_one = directions_select_one;
            this.directions_select_two = directions_select_two;
            this.dispense_quantity = Float.parseFloat(dispense_quantity);
            this.refill_quantity = Integer.parseInt(refill_quantity);
            this.do_not_substitute = do_not_substitute;
            this.label = label;
        }

        public String getRx_drug_name() {
            return rx_drug_name;
        }

        public void setRx_drug_name(String rx_drug_name) {
            this.rx_drug_name = rx_drug_name;
        }

        public String getRx_metric() {
            return rx_metric;
        }

        public void setRx_metric(String rx_metric) {
            this.rx_metric = rx_metric;
        }

        public String getRx_form() {
            return rx_form;
        }

        public void setRx_form(String rx_form) {
            this.rx_form = rx_form;
        }

        public String getDirections_select_one() {
            return directions_select_one;
        }

        public void setDirections_select_one(String directions_select_one) {
            this.directions_select_one = directions_select_one;
        }

        public String getDirections_select_two() {
            return directions_select_two;
        }

        public void setDirections_select_two(String directions_select_two) {
            this.directions_select_two = directions_select_two;
        }

        public float getRx_strength() {
            return rx_strength;
        }

        public void setRx_strength(int rx_strength) {
            this.rx_strength = rx_strength;
        }

        public float getDirections_quantity() {
            return directions_quantity;
        }

        public void setDirections_quantity(int directions_quantity) {
            this.directions_quantity = directions_quantity;
        }

        public float getDispense_quantity() {
            return dispense_quantity;
        }

        public void setDispense_quantity(int dispense_quantity) {
            this.dispense_quantity = dispense_quantity;
        }

        public int getRefill_quantity() {
            return refill_quantity;
        }

        public void setRefill_quantity(int refill_quantity) {
            this.refill_quantity = refill_quantity;
        }

        public boolean isDo_not_substitute() {
            return do_not_substitute;
        }

        public void setDo_not_substitute(boolean do_not_substitute) {
            this.do_not_substitute = do_not_substitute;
        }

        public boolean isLabel() {
            return label;
        }

        public void setLabel(boolean label) {
            this.label = label;
        }
    }
}
