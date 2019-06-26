package com.thealer.telehealer.apilayer.models.createuser;



import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Aswin on 01,July,2019
 */
public class OtherInformation implements Serializable {

    private String yearOfRegistration;
    private String mci;
    private String registrationNumber;

    public OtherInformation(@NonNull String yearOfRegistration, @NonNull String mci, @NonNull String registrationNumber) {
        this.yearOfRegistration = yearOfRegistration;
        this.mci = mci;
        this.registrationNumber = registrationNumber;
    }

    public String getYearOfRegistration() {
        return yearOfRegistration;
    }

    public void setYearOfRegistration(String yearOfRegistration) {
        this.yearOfRegistration = yearOfRegistration;
    }

    public String getMci() {
        return mci;
    }

    public void setMci(String mci) {
        this.mci = mci;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
