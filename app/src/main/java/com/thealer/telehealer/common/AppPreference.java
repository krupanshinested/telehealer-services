package com.thealer.telehealer.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Aswin on 11,October,2018
 */

public class AppPreference {
    private static AppPreference appPreference;
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private AppPreference(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public static synchronized AppPreference getInstance(Context context) {
        if (appPreference == null) {
            appPreference = new AppPreference(context);
        }
        return appPreference;
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {

        return sharedPreferences.getString(key, "");
    }

    public void setInt(String key, int value){
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key){
        return sharedPreferences.getInt(key, -1);
    }

    public void setBoolean(String key, boolean value) {

        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {

        return sharedPreferences.getBoolean(key, false);
    }

    public void deletePreference() {
        editor.clear().commit();
    }
}
