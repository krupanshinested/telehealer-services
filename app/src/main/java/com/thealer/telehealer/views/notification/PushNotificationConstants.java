package com.thealer.telehealer.views.notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.common.Utils;

/**
 * Created by Aswin on 10,January,2019
 */
public class PushNotificationConstants {

    public static final String PUSH_SCHEDULE_REQUEST = "scheduleRequest";
    public static final String PUSH_ACCEPT_SCHEDULE = "acceptSchedule";
    public static final String PUSH_REJECT_SCHEDULE = "rejectSchedule";
    public static final String PUSH_CONNECTION_REQUEST = "connectionRequest";
    public static final String PUSH_ACCEPT_CONNECTION = "acceptConnection";
    public static final String PUSH_REJECT_CONNECTION = "rejectConnection";
    public static final String PUSH_CANCEL_SCHEDULE = "canceledSchedule";
    public static final String PUSH_CHAT = "chat";
    public static final String PUSH_WAITING_ROOM = "waitingRoom";

    public static String getTitle(@NonNull String push_type) {
        switch (push_type) {
            case PUSH_SCHEDULE_REQUEST:
                return "New Appointment request";
            case PUSH_CANCEL_SCHEDULE:
                return "Appointment cancelled";
            case PUSH_CONNECTION_REQUEST:
                return "New Connection request";
            case PUSH_ACCEPT_SCHEDULE:
                return "Appointment request accepted";
            case PUSH_REJECT_SCHEDULE:
                return "Appointment request rejected";
            case PUSH_ACCEPT_CONNECTION:
                return "Connection request accepted";
            case PUSH_REJECT_CONNECTION:
                return "Connection request rejected";
            case PUSH_CHAT:
                return "Message";
            case PUSH_WAITING_ROOM:
                return "Waiting for your call";
        }
        return null;
    }

    public static String getMessage(@NonNull String push_type, @Nullable String time) {
        String formatedTime = null;
        if (time != null) {
            formatedTime = Utils.getPushNotificationTimeFormat(time);
        }

        switch (push_type) {
            case PUSH_SCHEDULE_REQUEST:
                return "You have an appointment request";
            case PUSH_CANCEL_SCHEDULE:
                return String.format("Your appointment of %s , has been cancelled.", formatedTime);
            case PUSH_CONNECTION_REQUEST:
                return "You have an new connection request";
            case PUSH_ACCEPT_SCHEDULE:
                return String.format("Your appointment on %s has been accepted.", formatedTime);
            case PUSH_REJECT_SCHEDULE:
                return String.format("Your appointment on %s has been rejected.", formatedTime);
            case PUSH_ACCEPT_CONNECTION:
                return "Your connection request has been accepted.";
            case PUSH_REJECT_CONNECTION:
                return "Your connection request has been rejected.";
            case PUSH_CHAT:
                return "You have a new message";
            case PUSH_WAITING_ROOM:
                return "Your patient is waiting for you in the virtual room.";
        }
        return null;
    }
}