package com.thealer.telehealer.apilayer.models.createuser;

import java.io.Serializable;

/**
 * Created by Aswin on 25,October,2018
 */
public class EducationsBean implements Serializable {

    private String school;
    private String graduation_year;
    private String degree;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGraduation_year() {
        return graduation_year;
    }

    public void setGraduation_year(String graduation_year) {
        this.graduation_year = graduation_year;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
}
