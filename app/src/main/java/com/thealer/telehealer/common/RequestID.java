package com.thealer.telehealer.common;

/**
 * Created by Aswin on 22,October,2018
 */

public class RequestID {
    public static final int REQ_BIO = 1;
    public static final int REQ_PRACTICE = 2;
    public static final int REQ_LICENSE = 3;

    public static final int REQ_SHOW_SUCCESS_VIEW = 5000;
    public static final int REQ_UPDATE_DOCUMENT = 5001;
    public static final int REQ_SELECT_ASSOCIATION = 5002;
    public static final int REQ_SEND_FAX = 5003;
    public static final int REQ_SELECT_ASSOCIATION_PATIENT = 5004;
    public static final int REQ_SELECT_PATIENT = 4004;
    public static final int REQ_SELECT_ASSOCIATION_DOCTOR = 5005;
    public static final int REQ_SELECT_ICD_CODE = 5006;
    public static final int REQ_PROFILE_UPDATE = 5007;
    public static final int REQ_SLOT_SELECTION = 5008;
    public static final int REQ_SIGNATURE = 5009;
    public static final int REQ_HISTORY_UPDATE = 6000;
    public static final int REQ_CONTENT_VIEW = 7000;
    public static final int REQ_SELECT_DIET = 8000;
    public static final int REQ_CONNECT_VITAL_CONTENT_VIEW = 5010;
    public static final int REQ_LICENSE_EXPIRED = 8001;
    public static final int REQ_VISIT_RECENT = 8002;
    public static final int REQ_CREATE_QUICK_LOGIN = 6001;
    public static final int REQ_VISIT_UPDATE = 6003;
    public static final int REQ_CREATE_NEW_VITAL = 9003;
    public static final int REQ_SELECT_CPT_CODE = 9004;
    public static final int REQ_OTP_VALIDATION = 4001;
    public static final int REQ_PROFILE_INCOMPLETE = 4002;
    public static final int REQ_GUEST_LOGIN = 4003;

    public static final String REQ_PASSWORD_RESET_OTP = "REQ_RESET_OTP";
    public static final String REQ_RESET_PASSWORD = "REQ_RESET_PASSWORD";
    public static final String REQ_FORGOT_PASSWORD = "REQ_FORGOT_PASSWORD";
    public static final String FORGOT_PASSWORD_OTP_VALIDATED = "FORGOT_PASSWORD_OTP_VALIDATED";
    public static final String RESET_PASSWORD_OTP_VALIDATED = "RESET_PASSWORD_OTP_VALIDATED";

    public static final String PROFILE_UPDATED = "PROFILE_UPDATED";
    public static final String QUICK_LOGIN_PIN_CREATED = "QUICK_LOGIN_PIN_CREATED";
    public static final String REQ_QUICK_LOGIN_PIN = "REQ_QUICK_LOGIN_PIN";

    public static final String TRIGGER_MANUAL_ENTRY = "TRIGGER_MANUAL_ENTRY";
    public static final String SET_UP_DEVICE = "SET_UP_DEVICE";
    public static final String VITAL_DEVICES = "VITAL_DEVICES";
    public static final String OPEN_NOT_CONNECTED_DEVICE = "OPEN_NOT_CONNECTED_DEVICE";
    public static final String OPEN_CONNECTED_DEVICE = "OPEN_CONNECTED_DEVICE";
    public static final String OPEN_INITIAL_FRAGMENT = "OPEN_INITIAL_FRAGMENT";
    public static final String OPEN_VITAL_SETUP = "OPEN_VITAL_SETUP";
    public static final String TRIGGER_DEVICE_CONNECTION = "TRIGGER_DEVICE_CONNECTION";
    public static final String VITAL_READY_FOR_MEASURE = "VITAL_READY_FOR_MEASURE";
    public static final String OPEN_VITAL_INFO = "OPEN_VITAL_INFO";
    public static final String OPEN_QR_READER = "OPEN_QR_READER";
    public static final String REQUEST_INSURANCE_CHANGE = "REQUEST_INSURANCE_CHANGE";
    public static final String INSURANCE_CHANGE_RESULT = "INSURANCE_CHANGE_RESULT";
    public static final String INSURANCE_REQUEST_IMAGE = "INSURANCE_REQUEST_IMAGE";
    public static final String CARD_INFORMATION_VIEW = "CARD_INFORMATION_VIEW";
    public static final String TRANSACTION_DETAIL = "TRANSACTION_DETAIL";

    public static final String REQ_SHOW_DETAIL_VIEW = "REQ_SHOW_DETAIL_VIEW";
    public static final String REQ_ADD_CONNECTION = "REQ_ADD_CONNECTION";
}
