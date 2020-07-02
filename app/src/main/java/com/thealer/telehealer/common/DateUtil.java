package com.thealer.telehealer.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    public static String getUTCfromLocal(String timeStamp,String inputDateFormat,String outputDateFormat) {
        DateFormat inputFormat = new SimpleDateFormat(inputDateFormat, Locale.ENGLISH);
        inputFormat.setTimeZone(TimeZone.getDefault());
        DateFormat outputFormat = new SimpleDateFormat(outputDateFormat, Locale.ENGLISH);
        outputFormat.setTimeZone(Utils.UtcTimezone);
        try {
            return outputFormat.format(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLocalfromUTC(String timeStamp,String inputDateFormat,String outputDateFormat) {
        DateFormat inputFormat = new SimpleDateFormat(inputDateFormat, Locale.ENGLISH);
        inputFormat.setTimeZone(Utils.UtcTimezone);
        DateFormat outputFormat = new SimpleDateFormat(outputDateFormat, Locale.ENGLISH);
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
