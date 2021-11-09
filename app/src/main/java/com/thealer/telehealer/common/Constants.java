package com.thealer.telehealer.common;

import com.thealer.telehealer.apilayer.models.subscription.PlanInfo;
import com.thealer.telehealer.apilayer.models.subscription.PlanInfoBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 09,October,2018
 */
public class Constants {

    public static final long callCapTime = 3600000; //1 hour

    public final static int MAX_VOICE_RESULTS = 5;
    public final static int MAX_PAUSE_TIME = 500;
    public final static int PARTIAL_DELAY_TIME = 500;
    public final static int ERROR_TIMEOUT = 5000;
    public final static int AUDIO_BEEP_DISABLED_TIMEOUT = 40000;


    public static final String HEADER_AUTH_TOKEN = "X-Access-Token";

    public static final String BUILD_PATIENT = "patient";
    public static final String BUILD_MEDICAL = "medical";
    public static final String BUILD_DOCTOR = "doctor";

    public static final String BUILD_TYPE_DEBUG = "debug";

    public static final int SHOW_NOTHING = 0;
    public static final int SHOW_PROGRESS = 1;
    public static final int SHOW_SCREEN = 2;

    public static final int PAGINATION_SIZE = 20;

    //User Activation status
    public static final String ACTIVATION_PENDING = "ACTIVATION_PENDING";
    public static final String ONBOARDING_PENDING = "ONBOARDING_PENDING";
    public static final String OFFLINE = "OFFLINE";
    public static final String AVAILABLE = "AVAILABLE";
    public static final String ACTIVATED = "ACTIVATED";
    public static final String BUSY = "BUSY";
    public static final String NO_DATA = "NO_DATA";
    //Gender
    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";
    public static final String GENDER_OTHERS = "others";

    public static final String ANDROID = "android";
    public static final String IOS = "ios";

    public static final String WEB = "web";
    //Date picker
    public static final String DATE_PICKER_TYPE = "date_picker_type";
    public static final int TYPE_DOB = 1;
    public static final int TYPE_EXPIRATION = 2;
    public static final int TILL_CURRENT_DAY = 3;
    public static final int TYPE_ORDER_CREATION = 4;
    public static final int DIET_CALENDAR = 5;
    public static final String DATE_PICKER_INTENT = "datepicker";
    public static final String DIET_REMOVED_INTENT = "diet_removed";
    public static final String YEAR = "year";
    public static final String DAY = "day";
    public static final String MONTH = "month";

    public static final String IS_FROM_HOME = "isFromHome";
    public static final String VIEW_TYPE = "view_type";
    public static final String VIEW_VITALS = "view_vitals";
    public static final String VIEW_ORDERS = "view_orders";
    public static final String VIEW_CONNECTION = "connection_view";
    public static final String VIEW_ASSOCIATION_DETAIL = "association_detail_view";
    public static final String ADD_CONNECTION_REQ_TYPE = "connection";
    public static final String ADD_CONNECTION_REQ_MSG = "Hi, Can we connect?";
    public static final String ADD_CONNECTION_ID = "add_connection_id";
    public static final String SELECTED_ITEM = "selected_item";


    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static  int Fail_Count = 0;

    //Success view constants
    public static final String SUCCESS_VIEW_STATUS = "success_view_status";
    public static final String SUCCESS_VIEW_TITLE = "success_view_title";
    public static final String SUCCESS_VIEW_DESCRIPTION = "success_view_description";
    public static final String SUCCESS_VITAL_VIEW_DESCRIPTION = "success_vital_view_description";
    public static final String SUCCESS_VIEW_SUCCESS_IMAGE = "SUCCESS_VIEW_SUCCESS_IMAGE";
    public static final String SUCCESS_VIEW_SUCCESS_IMAGE_TINT = "SUCCESS_VIEW_SUCCESS_IMAGE_TINT";

    //OTP Verification
    public static final String IS_API_REQUESTED = "isApiRequested";
    public static final String REMAINING_SECONDS = "remainingSeconds";

    //user roles
    public static final String ROLE_PATIENT = "USER";
    public static final String ROLE_DOCTOR = "BUSER";
    public static final String ROLE_ASSISTANT = "medical_assistant";

    // App user types
    public static final String USER_TYPE = "user_type";
    public static final int TYPE_PATIENT = 0;
    public static final int TYPE_MEDICAL_ASSISTANT = 1;
    public static final int TYPE_DOCTOR = 2;

    //Doctor registration flow constants
    public static final String IS_CREATE_MANUALLY = "isCreateManually";
    public static final String SEARCH_KEY = "search_key";
    public static final String DOCTOR_DATA = "doctor_data";
    public static final String PRACTICE_ID = "practice_id";
    public static final String DOCTOR_ID = "doctor_id";
    public static final String IS_NEW_PRACTICE = "isNewPractice";
    public static final String SELECTE_POSITION = "selected_position";
    public static final String LICENSE_ID = "license_id";
    public static final String CAMERA_INTENT = "camera_intent";
    public static final String PICTURE_PATH = "picture_path";
    public static final String LICENSE_IMAGE_PATH = "license_image_path";

    //Bio metric callback codes
    public static final int BIOMETRIC_ERROR = 7;
    public static final int BIOMETRIC_SUCCESS = 1;
    public static final int BIOMETRIC_FAILED = 2;
    public static final int BIOMETRIC_CANCEL = 0;

    //Quick login type
    public static final String QUICK_LOGIN_TYPE = "quick_login_type";
    public static final int QUICK_LOGIN_TYPE_PIN = 1;
    public static final int QUICK_LOGIN_TYPE_TOUCH = 2;
    public static final int QUICK_LOGIN_TYPE_NONE = 3;
    public static final int QUICK_LOGIN_TYPE_PASSWORD = 4;
    public static final String QUICK_LOGIN_PIN = "quick_login_pin";

    public static final String CURRENT_STEP = "current_step";
    public static final String WHERE_FROM = "where_from";
    public static final String EMAIL = "email";
    public static final String USER_DETAIL = "user_detail";
    public static final String DOCTOR_DETAIL = "doctor_detail";

    public static final String VITAL_DETAIL = "VITAL_DETAIL";


    public static final int CREATE_MODE = 0;
    public static final int EDIT_MODE = 1;
    public static final int VIEW_MODE = 2;
    public static final int SCHEDULE_CREATION_MODE = 3;

    public static final int LOCATION_SETTINGS_REQUEST = 108;

    //Screen Type
    public static final int forRegistration = 1;
    public static final int forProfileUpdate = 2;
    public static final String DATE_PICKER_CANCELLED = "datePickerCancelled";
    public static final String NOTIFICATION_COUNT_RECEIVER = "notification_count_receiver";
    public static final String CALL = "call";
    public static final String CHAT = "chat";
    public static final String CONNECTION_STATUS_RECEIVER = "connectionStatusReceiver";
    public static final String SUCCESS_VIEW_AUTO_DISMISS = "SUCCESS_VIEW_AUTO_DISMISS";
    public static final String SUCCESS_VIEW_DONE_BUTTON = "SUCCESS_VIEW_DONE_BUTTON";
    public static String male = "male";
    public static String female = "female";
    public static String others = "others";

    //share intent bundle
    public static List<String> sharedPath;

    public static final String CONNECTION_STATUS_OPEN = "open";
    public static final String CONNECTION_STATUS_PENDING = "pending";
    public static final String CONNECTION_STATUS_ACCEPTED = "accepted";
    public static final String CONNECTION_STATUS_REJECTED = "rejected";

    public static final String CALL_STARTED_BROADCAST = "CALL_STARTED_BROADCAST";
    public static final String CALL_ENDED_BROADCAST = "CALL_ENDED_BROADCAST";
    public static final String CALL_SCREEN_MAXIMIZE = "CALL_SCREEN_MAXIMIZE";
    public static final String CALL_ACTIVITY_RESUMED = "CALL_ACTIVITY_RESUMED";
    public static final String EXTRA_REMOVED_DATE = "EXTRA_REMOVED_DATE";
    public static String DESIGNATION="DESIGNATION";

    //Vital States
    public static final int idle = 1;
    public static final int measuring = 2;

    public static final List<String> genderList = new ArrayList<>(Arrays.asList(male, female, others));

    public static final String message_broadcast = "message_broadcast";

    public static final String inValidState = "N/A";

    public static final String did_subscriber_connected = "did_subscriber_connected";

    public static final double STRIPE_MIN_AMOUNT = 0.50;
    public static boolean isRedirectProfileSetting=false;
    public  static  boolean DisplayQuickLogin = false;
    public static final long IdealTime=30*60*1000;
    public static long ExpireTime=24*60*1000;
    public static boolean isFromBackground=true;
    public static final int TotalCount=3;
    public static String ChildHood_Asthma="Childhood Asthma Control Test";

    public static int activatedPlan=-1;
    public static boolean isFromSubscriptionPlan=false;


    public interface MasterCodes {
        String TYPE_OF_CHARGE = "TYPE_OF_CHARGE";
        String REASON = "REASON";
    }

    public interface ChargeReason {
        int VISIT = 1;
        int MEDICINE = 2;
        int SUPPLIES = 3;
        int CCM = 4;
        int RPM = 5;
        int BHI = 6;
        int CONCIERGE = 7;
    }

    public interface ChargeStatus {
        int CHARGE_PENDING = 1;
        int CHARGE_ADDED = 2;
        int CHARGE_PROCESS_INITIATED = 3;
        int CHARGE_PROCESS_IN_STRIPE = 4;
        int CHARGE_PROCESSED = 5;
        int CHARGE_PROCESS_FAILED = 6;
    }

    public static final int MAX_TRANSACTION_RETRY = 3;

    public interface OAuthStatus {
        String NOT_CONNECTED = "not_connected";
        String CONNECTED = "connected";
        String PAYOUT_DISABLED = "payouts_disabled";

    }

    public interface PaymentMode {
        int STRIPE = 1;
        int CASH = 2;
    }
}
