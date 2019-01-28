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
                return context.getString(R.string.vitals);
            case HELP_RECENTS:
                return context.getString(R.string.recents);
            case HELP_SCHEDULES:
                return context.getString(R.string.schedules);
        }
        return "";
    }

    String getContent(int type) {
        switch (type) {
            case HELP_DOC_PATIENT:
                return context.getString(R.string.help_content_doctor_patient);
            case HELP_ORDERS:
                return context.getString(R.string.help_content_orders);
            case HELP_VITALS:
                return context.getString(R.string.help_content_vitals);
            case HELP_RECENTS:
                return context.getString(R.string.help_content_recents);
            case HELP_SCHEDULES:
                return context.getString(R.string.help_content_schedules);
        }
        return "";
    }
}
