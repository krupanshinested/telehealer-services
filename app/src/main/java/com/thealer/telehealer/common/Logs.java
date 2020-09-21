package com.thealer.telehealer.common;

import android.util.Log;

/**
 * Created by Aswin on 09,October,2018
 */
public class Logs {
    public static void E(String tag, String msg){
        Log.e(tag, msg );
    }
    public static void D(String tag, String msg){
        Log.d(tag, msg );
    }
    public static void I(String tag, String msg){
        Log.i(tag, msg );
    }
    public static void W(String tag, String msg){
        Log.w(tag, msg );
    }
    public static void V(String tag, String msg){
        Log.v(tag, msg );
    }
}
