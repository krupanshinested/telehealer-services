package com.thealer.telehealer.views.notification;

import android.content.Context;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.UserType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aswin on 23,August,2019
 */
public class NotificationConstants {

    public static final String APPOINTMENT = "appointment";
    public static final String CALLS = "calls";
    public static final String EDUCATIONAL_VIDEO = "educational-videos";
    public static final String SUBSCRIPTION = "subscription";
    public static final String CONNECTIONS = "connection";
    public static final String ORDERS = "orders";
    public static final String MESSAGES = "messages";
    public static final String VITALS = "vitals";
    public static final String FORMS = "forms";
    public static final String ORDER_SUB_TYPE_PRESCRIPTIONS = "prescription";
    public static final String ORDER_SUB_TYPE_SPECIALIST = "specialist";
    public static final String ORDER_SUB_TYPE_LABS = "lab";
    public static final String ORDER_SUB_TYPE_X_RAY = "x-ray";
    public static final String ORDER_SUB_TYPE_MISC = "miscellaneous";

    public static List<String> getNotificationDisplayFilters(Context context){
        List<String> filterOptions = new ArrayList<>(Arrays.asList(context.getString(R.string.schedules),
                context.getString(R.string.connections), context.getString(R.string.vitals), context.getString(R.string.messages),
                context.getString(R.string.orders), context.getString(R.string.forms), context.getString(R.string.calls), context.getString(R.string.educational_video),context.getString(R.string.subscription)));

        if (!UserType.isUserPatient()){
            filterOptions.remove(context.getString(R.string.calls));
        }
        return filterOptions;
    }

    public static List<String> getNotificationFilterTypes(Context context){
        List<String> filterOptions = new ArrayList<>(Arrays.asList(APPOINTMENT, CONNECTIONS, VITALS, MESSAGES, ORDERS, FORMS, CALLS,EDUCATIONAL_VIDEO,SUBSCRIPTION));
        if (!UserType.isUserPatient()){
            filterOptions.remove(CALLS);
        }
        return filterOptions;
    }
}
