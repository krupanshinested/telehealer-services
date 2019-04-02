package com.thealer.telehealer.views.home;

import android.content.Context;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.UserType;

/**
 * Created by Aswin on 28,January,2019
 */
class HelpContent {

    public static final int HELP_DOC_PATIENT = 0;
    public static final int HELP_ORDERS = 1;
    public static final int HELP_VITALS = 2;
    public static final int HELP_RECENTS = 3;
    public static final int HELP_SCHEDULES = 4;
    public static final int HELP_VITAL_REPORT = 5;
    public static final int HELP_MONITORING = 6;

    private Context context;

    HelpContent(Context context) {
        this.context = context;
    }

    String getTitle(int type) {
        switch (type) {
            case HELP_DOC_PATIENT:
                return context.getString(R.string.associations);
            case HELP_ORDERS:
                return context.getString(R.string.orders);
            case HELP_VITALS:
            case HELP_VITAL_REPORT:
                return context.getString(R.string.vitals);
            case HELP_RECENTS:
                return context.getString(R.string.recents);
            case HELP_SCHEDULES:
                return context.getString(R.string.schedules);
            case HELP_MONITORING:
                return context.getString(R.string.monitoring);
        }
        return "";
    }

    String getContent(int type) {
        String user;
        if (UserType.isUserDoctor()) {
            user = context.getString(R.string.patients).toLowerCase();
        } else {
            user = context.getString(R.string.Doctors).toLowerCase();
        }

        switch (type) {
            case HELP_DOC_PATIENT:
                return String.format(context.getString(R.string.help_content_doctor_patient), user, user, user, user);
            case HELP_ORDERS:
                return context.getString(R.string.help_content_orders);
            case HELP_VITAL_REPORT:
                return context.getString(R.string.help_content_vital_report);
            case HELP_VITALS:
                return context.getString(R.string.help_content_vitals);
            case HELP_RECENTS:
                return String.format(context.getString(R.string.help_content_recents), user);
            case HELP_SCHEDULES:
                return context.getString(R.string.help_content_schedules);
            case HELP_MONITORING:
                if (UserType.isUserPatient()){
                    return context.getString(R.string.help_content_monitoring_patient);
                }else {
                    return context.getString(R.string.help_content_monitoring_doctor);
                }
        }
        return "";
    }
}
