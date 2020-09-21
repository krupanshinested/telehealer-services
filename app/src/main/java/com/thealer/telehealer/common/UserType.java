package com.thealer.telehealer.common;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 26,November,2018
 */
public class UserType {

    public static boolean isUserPatient(){
        return appPreference.getInt(Constants.USER_TYPE) == Constants.TYPE_PATIENT;
    }
    public static boolean isUserDoctor(){
        return appPreference.getInt(Constants.USER_TYPE) == Constants.TYPE_DOCTOR;
    }
    public static boolean isUserAssistant(){
        return appPreference.getInt(Constants.USER_TYPE) == Constants.TYPE_MEDICAL_ASSISTANT;
    }
    public static int getUserType(){
        return appPreference.getInt(Constants.USER_TYPE);
    }

}
