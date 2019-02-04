package com.thealer.telehealer.views.common;

import com.thealer.telehealer.common.UserType;

/**
 * Created by Aswin on 04,February,2019
 */
public class OverlayViewConstants {
    public static final String OVERLAY_NO_MEDICAL_ASSISTANT = "You can add a Medical Assistant by tapping the '+' button";
    public static final String OVERLAY_NO_DOCTOR = "You can add a Doctor by tapping the '+' button";
    public static final String OVERLAY_NO_PATIENT = "You can add a Patient by tapping the '+' button";
    public static final String OVERLAY_NO_APPOINTMENT = "You can add an appointment by tapping the '+' button";
    public static final String OVERLAY_NO_BP = (UserType.isUserPatient() ? "You can connect your vitals device to measure your Blood Pressure" : "You can add Blood pressure measurement of a patient by tapping '+' button");
    public static final String OVERLAY_NO_GLUCOSE = (UserType.isUserPatient() ? "You can connect your vitals device to measure your Blood Glucose" : "You can add Blood Glucose measurement of a patient by tapping '+' button");
    public static final String OVERLAY_NO_HEAR_RATE = (UserType.isUserPatient() ? "You can connect your vitals device to measure your Heart Rate" : "You can add Hear Rate measurement of a patient by tapping '+' button");
    public static final String OVERLAY_NO_TEMPERATURE = (UserType.isUserPatient() ? "You can connect your vitals device to measure your Body Temperature" : "You can add Body temperature measurement of a patient by tapping '+' button");
    public static final String OVERLAY_NO_PULSE = (UserType.isUserPatient() ? "You can connect your vitals device to measure your Pulse" : "You can add pulse measurement of a patient by tapping '+' button");
    public static final String OVERLAY_NO_VITALS = (UserType.isUserPatient() ? "You can connect your vitals device to measure your vitals by tapping the '+' button" : "You can add Vital measurement of a patient by tapping '+' button");
    public static final String OVERLAY_NO_WEIGHT = (UserType.isUserPatient() ? "You can connect your vitals device to measure your Weight" : "You can add Weight measurement of a patient by tapping '+' button");
    public static final String OVERLAY_NO_PRESCRIPTION = "You can add a Prescription by tapping the '+' button";
    public static final String OVERLAY_NO_DOCUMENT = "You can add a Document by tapping the '+' button";
    public static final String OVERLAY_NO_LAB_RECORD = "You can add a Lab record by tapping the '+' button";
    public static final String OVERLAY_NO_SPECIALIST = "You can add a Specialist by tapping the '+' button";
    public static final String OVERLAY_NO_RADIOLOGY = "You can add a Radiology by tapping the '+' button";
}
