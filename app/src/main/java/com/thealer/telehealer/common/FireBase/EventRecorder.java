package com.thealer.telehealer.common.FireBase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

import java.util.Arrays;
import java.util.Date;

import static com.thealer.telehealer.TeleHealerApplication.application;

/**
 * Created by rsekar on 1/18/19.
 */

public class EventRecorder {

    static private FirebaseAnalytics analytics = application.firebaseAnalytics;

    public static void recordPermissionDenined(String name) {
        analytics.logEvent(name,null);
    }

    public static void recordUserStatus(String status) {
        Bundle bundle = new Bundle();
        bundle.putString("user_status",status);
        analytics.logEvent("user_status",bundle);
    }

    public static void recordUserRole(String role) {
        analytics.setUserProperty(role,"USER_ROLE");
    }

    public static void updateUserId(@Nullable String userGuid) {

        if (userGuid != null)
            Crashlytics.setUserIdentifier(userGuid);

        analytics.setUserId(userGuid);
    }

    public static void recordAppUpgrade() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,"id-app_upgrade");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"app_upgrade");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"app_upgrade");

        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,bundle);
    }

    public static void recordLastUpdate(String eventType) {
        Bundle bundle = new Bundle();
        bundle.putString(eventType, Utils.getStringFromDate(new Date(),"yyyy-MM-dd"));

        analytics.logEvent(eventType,bundle);
    }

    public static void recordConnection(String event) {
        analytics.logEvent(event,null);
    }

    public static void recordCallUpdates(String event,@Nullable String value) {

        Bundle bundle = new Bundle();
        bundle.putString("call_event", event);
        if (value != null) {
            bundle.putString("call_value", value);
        }

        analytics.logEvent(event,bundle);
    }

    public static void recordRegistration(String event,@Nullable String userGuid){
        Bundle bundle = new Bundle();
        bundle.putString("event", event);
        analytics.logEvent("REGISTRATION_ACTION",bundle);
    }

    public static void recordUserSession(String event){
        Bundle bundle = new Bundle();
        bundle.putString("user_session_status", event);
        analytics.logEvent("user_session_status",bundle);
    }

    public static void recordNotification(String event){
        Bundle bundle = new Bundle();
        bundle.putString("notification_type", event);
        analytics.logEvent("notification_event",bundle);
    }

    public static void recordAPIEvent(String path,String method,int code){
        Log.d("EventRecorder","recordAPIEvent");
        String[] components = path.split("/");
        Boolean shouldContinue = true;
        String pathElement = null;

        while (shouldContinue ) {
            if (components.length == 0) {
                shouldContinue = false;
            } else if (components[components.length - 1].contains(":")){
                components = Arrays.copyOf(components, components.length - 1);
                shouldContinue = true;
            } else {
                pathElement = components[components.length - 1];
                shouldContinue = false;
            }
        }


        if (pathElement != null) {
            String attr1 = pathElement + "_" + method;

            Bundle bundle = new Bundle();
            bundle.putString("PATH_METHOD", attr1);
            bundle.putString("PATH_METHOD_STATUSCODE", attr1+"_"+code+"");
            bundle.putString("STATUSCODE", code+"");

            analytics.logEvent("api_event",bundle);
            Log.d("EventRecorder","STATUSCODE : "+code);
        }
    }

    public static void recordVitals(String event,String device){
        Bundle bundle = new Bundle();
        bundle.putString("VITALS_DEVICE_ACTION", device+"_"+event);
        bundle.putString("VITALS_ACTION", event);
        bundle.putString("VITALS_DEVICE", device);
        analytics.logEvent("vitals_event",bundle);
    }

}
