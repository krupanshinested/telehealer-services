package com.thealer.telehealer.apilayer.models.commonResponseModel;

import android.arch.lifecycle.ViewModel;

import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.apilayer.models.createuser.PracticesBean;
import com.thealer.telehealer.apilayer.models.createuser.SpecialtiesBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 26,November,2018
 */
public class DataBean extends ViewModel implements Serializable {

    private String npi;
    private String title;
    private String degree;
    private String diploma_certificate;
    private String certification;
    private String uid;
    private String image_url;
    private String liability;
    private String bio;
    private String driver_license;
    private String insurance_front;
    private String insurance_back;
    private String secondary_insurance_front;
    private String secondary_insurance_back;
    private String website;
    private List<LicensesBean> licenses = new ArrayList<>();
    private List<SpecialtiesBean> specialties = new ArrayList<>();
    private List<PracticesBean> practices = new ArrayList<>();
    private ClinicBean clinic;

    public DataBean() {

    }

    public DataBean(String npi, String title, String degree, String certification, String uid, String image_url, String liability,
                    String bio, String driver_license, String insurance_front, String insurance_back, String secondary_insurance_front, String secondary_insurance_back,
                    List<LicensesBean> licenses, List<SpecialtiesBean> specialties, List<PracticesBean> practices) {
        this.npi = npi;
        this.title = title;
        this.degree = degree;
        this.certification = certification;
        this.uid = uid;
        this.image_url = image_url;
        this.liability = liability;
        this.bio = bio;
        this.driver_license = driver_license;
        this.insurance_front = insurance_front;
        this.insurance_back = insurance_back;
        this.secondary_insurance_front = secondary_insurance_front;
        this.secondary_insurance_back = secondary_insurance_back;
        this.licenses = licenses;
        this.specialties = specialties;
        this.practices = practices;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getLiability() {
        return liability;
    }

    public void setLiability(String liability) {
        this.liability = liability;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDriver_license() {
        return driver_license;
    }

    public void setDriver_license(String driver_license) {
        this.driver_license = driver_license;
    }

    public List<LicensesBean> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<LicensesBean> licenses) {
        this.licenses = licenses;
    }

    public List<SpecialtiesBean> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<SpecialtiesBean> specialties) {
        this.specialties = specialties;
    }

    public List<PracticesBean> getPractices() {
        return practices;
    }

    public void setPractices(List<PracticesBean> practices) {
        this.practices = practices;
    }

    public String getInsurance_front() {
        return insurance_front;
    }

    public void setInsurance_front(String insurance_front) {
        this.insurance_front = insurance_front;
    }

    public String getInsurance_back() {
        return insurance_back;
    }

    public void setInsurance_back(String insurance_back) {
        this.insurance_back = insurance_back;
    }

    public String getSecondary_insurance_front() {
        return secondary_insurance_front;
    }

    public void setSecondary_insurance_front(String secondary_insurance_front) {
        this.secondary_insurance_front = secondary_insurance_front;
    }

    public String getSecondary_insurance_back() {
        return secondary_insurance_back;
    }

    public void setSecondary_insurance_back(String secondary_insurance_back) {
        this.secondary_insurance_back = secondary_insurance_back;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {

        return website;
    }

    public Boolean isInsurancePresent() {
        return getInsurance_front() != null && getInsurance_back() != null;
    }
    public Boolean isSecondaryInsurancePresent() {
        return getSecondary_insurance_front() != null && getSecondary_insurance_back() != null;
    }

    public String getDiploma_certificate() {
        return diploma_certificate;
    }

    public void setDiploma_certificate(String diploma_certificate) {
        this.diploma_certificate = diploma_certificate;
    }

    public ClinicBean getClinic() {
        return clinic;
    }

    public void setClinic(ClinicBean clinic) {
        this.clinic = clinic;
    }
}
