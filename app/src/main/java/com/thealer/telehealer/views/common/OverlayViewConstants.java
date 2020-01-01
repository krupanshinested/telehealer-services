package com.thealer.telehealer.views.common;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.UserType;

/**
 * Created by Aswin on 04,February,2019
 */
public class OverlayViewConstants {
    public static final int OVERLAY_NO_MEDICAL_ASSISTANT = R.string.OVERLAY_NO_MEDICAL_ASSISTANT_MESSAGE;
    public static final int OVERLAY_NO_DOCTOR = R.string.OVERLAY_NO_DOCTOR_MESSAGE;
    public static final int OVERLAY_NO_PATIENT = R.string.OVERLAY_NO_PATIENT_MESSAGE;
    public static final int OVERLAY_NO_EDUCATIONAL_VIDEO = R.string.OVERLAY_NO_EDUCATIONAL_VIDEO;
    public static final int OVERLAY_NO_APPOINTMENT = R.string.OVERLAY_NO_APPOINTMENT_MESSAGE;
    public static final int OVERLAY_NO_BP = (UserType.isUserPatient() ? R.string.OVERLAY_NO_BP_MESSAGE : R.string.OVERLAY_NO_PATIENT_BP_MESSAGE);
    public static final int OVERLAY_NO_GLUCOSE = (UserType.isUserPatient() ? R.string.OVERLAY_NO_GLUCOSE_MESSAGE : R.string.OVERLAY_NO_PATIENT_GLUCOSE_MESSAGE);
    public static final int OVERLAY_NO_HEAR_RATE = (UserType.isUserPatient() ? R.string.OVERLAY_NO_HEAR_RATE_MESSAGE : R.string.OVERLAY_NO_PATIENT_HEAR_RATE_MESSAGE);
    public static final int OVERLAY_NO_TEMPERATURE = (UserType.isUserPatient() ? R.string.OVERLAY_NO_TEMPERATURE_MESSAGE : R.string.OVERLAY_NO_PATIENT_TEMPERATURE_MESSAGE);
    public static final int OVERLAY_NO_PULSE = (UserType.isUserPatient() ? R.string.OVERLAY_NO_PULSE_MESSAGE : R.string.OVERLAY_NO_PATIENT_PULSE_MESSAGE);
    public static final int OVERLAY_NO_VITALS = (UserType.isUserPatient() ? R.string.OVERLAY_NO_VITALS_MESSAGE : R.string.OVERLAY_NO_PATIENT_VITALS_MESSAGE);
    public static final int OVERLAY_NO_WEIGHT = (UserType.isUserPatient() ? R.string.OVERLAY_NO_WEIGHT_MESSAGE : R.string.OVERLAY_NO_PATIENT_WEIGHT_MESSAGE);
    public static final int OVERLAY_NO_PRESCRIPTION = R.string.OVERLAY_NO_PRESCRIPTION_MESSAGE;
    public static final int OVERLAY_NO_DOCUMENT = R.string.OVERLAY_NO_DOCUMENT_MESSAGE;
    public static final int OVERLAY_NO_LAB_RECORD = R.string.OVERLAY_NO_LAB_RECORD_MESSAGE;
    public static final int OVERLAY_NO_SPECIALIST = R.string.OVERLAY_NO_SPECIALIST_MESSAGE;
    public static final int OVERLAY_NO_RADIOLOGY = R.string.OVERLAY_NO_RADIOLOGY_MESSAGE;
    public static final int OVERLAY_NO_MISC = R.string.OVERLAY_NO_MISC_MESSAGE;
}
