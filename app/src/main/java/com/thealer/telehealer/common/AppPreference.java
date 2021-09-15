package com.thealer.telehealer.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.thealer.telehealer.TeleHealerApplication;

import java.util.HashSet;
import java.util.Set;

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
        sharedPreferences = context.getSharedPreferences(TeleHealerApplication.appConfig.getAppPreference(),
                Context.MODE_PRIVATE);
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


    public String getStringWithDefault(String key, String defaultVal){
        return sharedPreferences.getString(key, defaultVal);
    }

    public void setStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value);
        editor.commit();
    }

    public Set<String> getStringSet(String key) {
        return sharedPreferences.getStringSet(key, new HashSet<>());
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void setBoolean(String key, boolean value) {

        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {

        return sharedPreferences.getBoolean(key, false);
    }

    public void setHashString(String key, Object value) {
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    public String getHashString(String key) {
        return sharedPreferences.getString(key, "");
    }
}
