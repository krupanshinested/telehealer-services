package com.thealer.telehealer.apilayer.models.orders;

import java.io.Serializable;

/**
 * Created by Aswin on 12,December,2018
 */
public class FaxesBean implements Serializable {

    private int fax_id;
    private String fax_sid;
    private String media_url;
    private String fax_number;
    private String status;
    private DetailBean detail;
    private int referral_id;
    private int sent_by;
    private int sent_for;
    private String created_at;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public int getReferral_id() {
        return referral_id;
    }

    public void setReferral_id(int referral_id) {
        this.referral_id = referral_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getFax_id() {
        return fax_id;
    }

    public void setFax_id(int fax_id) {
        this.fax_id = fax_id;
    }

    public String getFax_sid() {
        return fax_sid;
    }

    public void setFax_sid(String fax_sid) {
        this.fax_sid = fax_sid;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getFax_number() {
        return fax_number;
    }

    public void setFax_number(String fax_number) {
        this.fax_number = fax_number;
    }


    public int getSent_by() {
        return sent_by;
    }

    public void setSent_by(int sent_by) {
        this.sent_by = sent_by;
    }

    public int getSent_for() {
        return sent_for;
    }

    public void setSent_for(int sent_for) {
        this.sent_for = sent_for;
    }


    public static class DetailBean {

        private PharmacyBean pharmacy;

        public PharmacyBean getPharmacy() {
            return pharmacy;
        }

        public void setPharmacy(PharmacyBean pharmacy) {
            this.pharmacy = pharmacy;
        }

        public static class PharmacyBean {

            private String addr2;
            private String addr1;
            private String npi;
            private String city;
            private String contact_name;
            private String fax;
            private String alt_fax;
            private String state;
            private String phone;
            private String speciality;
            private String company;

            public String getAddr2() {
                return addr2;
            }

            public void setAddr2(String addr2) {
                this.addr2 = addr2;
            }

            public String getAddr1() {
                return addr1;
            }

            public void setAddr1(String addr1) {
                this.addr1 = addr1;
            }

            public String getNpi() {
                return npi;
            }

            public void setNpi(String npi) {
                this.npi = npi;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getContact_name() {
                return contact_name;
            }

            public void setContact_name(String contact_name) {
                this.contact_name = contact_name;
            }

            public String getFax() {
                return fax;
            }

            public void setFax(String fax) {
                this.fax = fax;
            }

            public String getAlt_fax() {
                return alt_fax;
            }

            public void setAlt_fax(String alt_fax) {
                this.alt_fax = alt_fax;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getSpeciality() {
                return speciality;
            }

            public void setSpeciality(String speciality) {
                this.speciality = speciality;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }
        }
    }
}
