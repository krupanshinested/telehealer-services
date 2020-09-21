package com.thealer.telehealer.apilayer.models.createuser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 25,October,2018
 */
public class PracticesBean implements Serializable{

    private String name;
    private String website;
    private VisitAddressBean visit_address;
    private List<PhonesBean> phones = new ArrayList<>();

    public PracticesBean() {
    }

    public PracticesBean(String name, String website, VisitAddressBean visit_address, List<PhonesBean> phones) {
        this.name = name;
        this.website = website;
        this.visit_address = visit_address;
        this.phones = phones;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public VisitAddressBean getVisit_address() {
        return visit_address;
    }

    public void setVisit_address(VisitAddressBean visit_address) {
        this.visit_address = visit_address;
    }

    public List<PhonesBean> getPhones() {
        return phones;
    }

    public void setPhones(List<PhonesBean> phones) {
        this.phones = phones;
    }

}
