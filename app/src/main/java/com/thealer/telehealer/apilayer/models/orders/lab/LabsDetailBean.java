package com.thealer.telehealer.apilayer.models.orders.lab;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 01,December,2018
 */
public class LabsDetailBean implements Serializable {

    public String requested_date;
    public List<LabsBean> labs;
    private CopyToBean copy_to;

    public String getRequested_date() {
        return requested_date;
    }

    public void setRequested_date(String requested_date) {
        this.requested_date = requested_date;
    }

    public List<LabsBean> getLabs() {
        return labs;
    }

    public void setLabs(List<LabsBean> labs) {
        this.labs = labs;
    }

    public String getDisplayName(){
        String name = "";
        for (int i = 0; i < getLabs().size(); i++) {
            name = name.concat(getLabs().get(i).getTest_description());
            if (i != getLabs().size() - 1)
                name = name.concat(",");
        }
        return name;
    }


    public CopyToBean getCopy_to() {
        return copy_to;
    }

    public void setCopy_to(CopyToBean copy_to) {
        this.copy_to = copy_to;
    }

    public static class CopyToBean implements Serializable{

        private String name;
        private String address;
        private String phone;
        private String npi;

        public CopyToBean() {
        }

        public CopyToBean(String name, String address, String phone, String npi) {
            this.name = name;
            this.address = address;
            this.phone = phone;
            this.npi = npi;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNpi() {
            return npi;
        }

        public void setNpi(String npi) {
            this.npi = npi;
        }
    }

}
