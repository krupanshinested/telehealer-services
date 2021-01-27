package com.thealer.telehealer.common;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Util.TeleCacheUrl;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.views.common.CustomDialogClickListener;
import com.thealer.telehealer.views.common.CustomDialogs.OptionSelectionDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.OptionsSelectionAdapter;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.home.pendingInvites.PendingInvitesActivity;
import com.thealer.telehealer.views.inviteUser.InviteContactUserActivity;
import com.thealer.telehealer.views.inviteUser.InviteUserActivity;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants;
import com.thealer.telehealer.views.signup.SignUpActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;

import config.AppConfig;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;
import static com.thealer.telehealer.TeleHealerApplication.notificationChannelId;


/**
 * Created by Aswin on 12,October,2018
 */
public class Utils {

    public static final String UTCFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final TimeZone UtcTimezone = TimeZone.getTimeZone("UTC");
    public static final TimeZone GmtTimezone = TimeZone.getTimeZone("GMT");
    public static final String defaultDateFormat = "dd MMM, yyyy";
    public static final String yyyy_mm = "yyyy-MM";
    public static final String yyyy_mm_dd = "yyyy-MM-dd";
    public static final String mmm_dd = "MMM dd";
    public static final String mmm_yyyy = "MMM yyyy";
    public static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private static FancyShowCaseView fancyShowCaseView;

    public static void showOverlay(FragmentActivity fragmentActivity, View view, int message, DismissListener dismissListener) {
        fancyShowCaseView = new FancyShowCaseView.Builder(fragmentActivity)
                .focusOn(view)
                .title(fragmentActivity.getString(message))
                .closeOnTouch(true)
                .enableAutoTextPosition()
                .titleSize(16, TypedValue.COMPLEX_UNIT_SP)
                .titleStyle(R.style.text_overlay_style, Gravity.BOTTOM)
                .focusCircleRadiusFactor(0.7)
                .focusShape(FocusShape.CIRCLE)
                .clickableOn(view)
                .build();

        fancyShowCaseView.setDismissListener(dismissListener);
        fancyShowCaseView.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideOverlay();
            }
        }, 8000);
    }

    public static void hideOverlay() {
        if (fancyShowCaseView != null && fancyShowCaseView.isShown())
            fancyShowCaseView.hide();
    }

    public static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }

    public static Dialog showDatePickerDialog(FragmentActivity activity, Calendar minCalendar, int type, DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog.OnDateSetListener onDateSetListener = dateSetListener;
        if (onDateSetListener == null) {
            onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    sendDateBroadCast(activity, year, month, dayOfMonth);
                }
            };
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, onDateSetListener, year, month, day);

        if (minCalendar != null) {
            datePickerDialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
        }

        switch (type) {
            case Constants.TYPE_DOB:
                datePickerDialog = new DatePickerDialog(activity, onDateSetListener, 2000, month, day);
                break;
            case Constants.TYPE_EXPIRATION:
                calendar.set(year, month, day + 1, 0, 0, 0);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                break;
            case Constants.TILL_CURRENT_DAY:
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                break;
            case Constants.TYPE_ORDER_CREATION:
                calendar.set(year, month, day + 1);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                calendar.set(year + 1, month, day - 1);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                break;
            case Constants.DIET_CALENDAR:
                calendar.set(2019, 0, 1);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                break;
        }

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setDateCancelBroadCast(activity);
            }
        });

        datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
        datePickerDialog.show();
        return datePickerDialog;
    }

    private static void setDateCancelBroadCast(Context context) {
        Intent intent = new Intent(Constants.DATE_PICKER_INTENT);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.DATE_PICKER_CANCELLED, true);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private static void sendDateBroadCast(Context context, int year, int month, int dayOfMonth) {
        Intent intent = new Intent(Constants.DATE_PICKER_INTENT);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.YEAR, year);
        bundle.putInt(Constants.MONTH, month);
        bundle.putInt(Constants.DAY, dayOfMonth);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void showTimePickerDialog(@Nullable String title,
                                            FragmentActivity activity, @Nullable String time, TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(time)) {
            int hour = Integer.parseInt(DateUtil.getLocalfromUTC(time, "hh:mm a", "kk"));
            int minute = Integer.parseInt(DateUtil.getLocalfromUTC(time, "hh:mm a", "mm"));
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
        } else {
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        if (!TextUtils.isEmpty(title)) {
            timePickerDialog.setTitle(title);
        }
        timePickerDialog.show();
    }

    public static String getFormatedDate(int year, int month, int dayOfMonth) {
        return String.valueOf(dayOfMonth) + " " + months[month] + ", " + year;
    }

    public static boolean isEmailValid(String email) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.matches(emailPattern, email);
    }

    public static String trimAndRemoveNewLineCharacters(@Nullable String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.trim().replace("\n", "");
        } else {
            return "";
        }
    }

    public static boolean isValidState(String state) {

        if (appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_STATE_VALIDATION))
            return true;

        String statePattern = "A[LKSZRAEP]|C[AOT]|D[EC]|F[LM]|G[AU]|HI|I[ADLN]|K[SY]|LA|M[ADEHINOPST]|N[CDEHJMVY]|O[HKR]|P[ARW]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY]";

        return Pattern.matches(statePattern, state.toUpperCase());
    }

    public static boolean isDateExpired(String date) {
        if (date == null)
            return true;

        try {
            DateFormat dateFormat = new SimpleDateFormat(defaultDateFormat, Locale.ENGLISH);
            Date inputDate = dateFormat.parse(date);
            Date currentDate = new Date();

            return inputDate.compareTo(currentDate) >= 0;

        } catch (ParseException e) {
            return isDateExpired(date, UTCFormat);
        }
    }

    public static boolean isDateExpired(String date, String format) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
            Date inputDate = dateFormat.parse(date);
            Date currentDate = new Date();

            return inputDate.compareTo(currentDate) >= 0;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isOneDayBefore(String date) {
        if (date == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        Calendar input = Calendar.getInstance();
        try {
            Date inputDate = dateFormat.parse(date);
            input.setTime(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return input.before(calendar);
    }

    public static boolean isOneHourBefore(String date) {
        if (date == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        Calendar input = Calendar.getInstance();
        try {
            Date inputDate = dateFormat.parse(date);
            input.setTime(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return input.before(calendar);
    }

    public static long getDateDifferceinHours(long date1, long date2) {

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        Date givendate1 = new Date();
        givendate1.setTime(date1);
        calendar1.setTime(givendate1);

        Date givendate2 = new Date();
        givendate2.setTime(date2);
        calendar2.setTime(givendate2);

        long millis1 = calendar1.getTimeInMillis();
        long millis2 = calendar2.getTimeInMillis();

        long diff = millis2 - millis1;
        long diffHours = diff / (60 * 60 * 1000);
        Log.d("getDateDifferceinHours", "" + diffHours);
        return diffHours;
    }

    public static boolean isDateTimeExpired(String date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
            dateFormat.setTimeZone(UtcTimezone);
            Date inputDate = dateFormat.parse(date);
            Date currentDate = new Date();

            return currentDate.compareTo(inputDate) >= 0;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isDateTimeExpired(Date date) {
        Date currentDate = new Date();
        return currentDate.compareTo(date) >= 0;

    }

    public static int getUserTypeFromRole(String role) {
        switch (role) {
            case Constants.ROLE_PATIENT:
                return Constants.TYPE_PATIENT;
            case Constants.ROLE_DOCTOR:
                return Constants.TYPE_DOCTOR;
            case Constants.ROLE_ASSISTANT:
                return Constants.TYPE_MEDICAL_ASSISTANT;
        }
        return -1;
    }

    public static void setImageWithGlide(Context context, ImageView imageView, String path, Drawable placeHolder, boolean isUrlAuthNeeded, boolean decrypt) {
        if (path != null && !path.isEmpty()) {
            TeleCacheUrl glideUrl;
            if (isUrlAuthNeeded) {
                glideUrl = getGlideUrlWithAuth(context, path, decrypt);
            } else {
                glideUrl = new TeleCacheUrl(path);
            }
            if (placeHolder != null) {
                Glide.with(context).load(glideUrl).apply(new RequestOptions().placeholder(placeHolder)).into(imageView);
            } else {
                Glide.with(context).load(glideUrl).into(imageView);
            }
        } else {
            if (placeHolder == null)
                imageView.setImageDrawable(context.getDrawable(R.drawable.profile_placeholder));
            else
                imageView.setImageDrawable(placeHolder);
        }
    }

    public static void setImageWithGlideWithoutDefaultPlaceholder(Context context, ImageView imageView, String path, Drawable placeHolder, boolean isUrlAuthNeeded, boolean decrypt) {
        if (path != null && !path.isEmpty()) {
            TeleCacheUrl glideUrl;
            if (isUrlAuthNeeded) {
                glideUrl = getGlideUrlWithAuth(context, path, decrypt);
            } else {
                glideUrl = new TeleCacheUrl(path);
            }
            if (placeHolder != null) {
                Glide.with(context).load(glideUrl).apply(new RequestOptions().placeholder(placeHolder)).into(imageView);
            } else {
                Glide.with(context).load(glideUrl).into(imageView);
            }
        }
    }

    public static TeleCacheUrl getGlideUrlWithAuth(Context context, String path, boolean decrypt) {
        /*if (path.contains("http:") || path.contains("https:")) {

        } else {
            path = context.getString(R.string.api_base_url) + context.getString(R.string.get_image_url) + path + "&decrypt=" + decrypt;
        }*/

        return new TeleCacheUrl(path, new Headers() {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.HEADER_AUTH_TOKEN, TeleHealerApplication.appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN));
                return hashMap;
            }
        });
    }

    public static void setGenderImage(Context context, ImageView genderIv, String gender) {
        if (TextUtils.isEmpty(gender)) {
            genderIv.setImageDrawable(context.getDrawable(R.drawable.gender_others));
            return;
        }

        switch (gender.toLowerCase()) {
            case Constants.GENDER_MALE:
                genderIv.setImageDrawable(context.getDrawable(R.drawable.gender_male));
                break;
            case Constants.GENDER_FEMALE:
                genderIv.setImageDrawable(context.getDrawable(R.drawable.gender_female));
                break;
            case Constants.GENDER_OTHERS:
                genderIv.setImageDrawable(context.getDrawable(R.drawable.gender_others));
                break;
        }
    }

    public static void setPlatformImage(Context context, ImageView PlatoformIv, String platform) {
        if (TextUtils.isEmpty(platform)) {
            PlatoformIv.setImageDrawable(context.getDrawable(R.drawable.android_icon));
            return;
        }

        switch (platform.toLowerCase()) {
            case Constants.ANDROID:
                PlatoformIv.setImageDrawable(context.getDrawable(R.drawable.android_icon));
                break;
            case Constants.IOS:
                PlatoformIv.setImageDrawable(context.getDrawable(R.drawable.ic_apple));
                break;
            case Constants.WEB:
                PlatoformIv.setImageDrawable(context.getDrawable(R.drawable.icon_web));
        }
    }

    public static String getFormatedTime(String updated_at) {
        if (updated_at != null) {
            DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
            dateFormat.setTimeZone(UtcTimezone);

            DateFormat returnFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
            returnFormat.setTimeZone(TimeZone.getDefault());
            try {
                Date date = dateFormat.parse(updated_at);
                return returnFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public static Date getDateFromString(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        try {
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getCurrentZoneDate(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat outFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        outFormat.setTimeZone(TimeZone.getDefault());
        try {
            dateString = outFormat.format(dateFormat.parse(dateString));
            return outFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getDateFromString(String dateString, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getDateFromPossibleFormat(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        try {
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            dateFormat = new SimpleDateFormat(defaultDateFormat, Locale.ENGLISH);
            try {
                return dateFormat.parse(dateString);
            } catch (Exception e1) {
                return new Date();
            }
        }
    }

    public static String getStringFromDate(Date date, String format) {
        DateFormat returnFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        return returnFormat.format(date);
    }

    public static String getDayMonthYear(String date) {

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat returnFormat = new SimpleDateFormat(defaultDateFormat, Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getYearMonthDay(String date) {

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat returnFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDayMonth(String date) {

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat returnFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDayMonthTime(String date) {

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat returnFormat = new SimpleDateFormat("dd MMM, hh:mm aa", Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDayMonthYearTime(String date) {

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat returnFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm aa", Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDoctorDisplayName(String first_name, String last_name, String title) {
        return first_name + " " + last_name + " " + ((title != null) ? title : "");
    }

    public static String getPatientDisplayName(String first_name, String last_name) {
        first_name = first_name.replace(first_name.charAt(0), String.valueOf(first_name.charAt(0)).toUpperCase().charAt(0));
        last_name = last_name.replace(last_name.charAt(0), String.valueOf(last_name.charAt(0)).toUpperCase().charAt(0));
        return first_name + " " + last_name;
    }

    public static Dialog showAlertDialog(Context context, String title, String message,
                                         @Nullable String positiveTitle,
                                         @Nullable String negativeTitle,
                                         @Nullable DialogInterface.OnClickListener positiveListener,
                                         @Nullable DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.custom_alert_dialog_style);

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        if (positiveTitle != null) {
            alertDialog.setPositiveButton(positiveTitle, positiveListener);
        }

        if (negativeTitle != null) {
            alertDialog.setNegativeButton(negativeTitle, negativeListener);
        }
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        return dialog;
    }

    public static String getAppType() {
        if (TeleHealerApplication.appPreference.getInt(Constants.USER_TYPE) == Constants.TYPE_PATIENT) {
            return Constants.BUILD_PATIENT;
        } else {
            return Constants.BUILD_MEDICAL;
        }
    }

    public static void setEditable(EditText editText, Boolean editable) {
        if (editable) {
            if (editText.getTag() instanceof KeyListener) {
                editText.setKeyListener((KeyListener) editText.getTag());
            }
        } else {
            editText.setTag(editText.getKeyListener());
            editText.setKeyListener(null);
        }
    }

    public static String getCurrentFomatedDate() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss", Locale.ENGLISH);
        DateFormat outDateFormat = new SimpleDateFormat(defaultDateFormat, Locale.ENGLISH);
        try {
            return outDateFormat.format(dateFormat.parse(new Timestamp(System.currentTimeMillis()).toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentUtcDate() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(GmtTimezone);

        SimpleDateFormat outputFormat = new SimpleDateFormat(UTCFormat);
        outputFormat.setTimeZone(UtcTimezone);

        try {
            return outputFormat.format(simpleDateFormat.parse(calendar.getTime().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentFomatedDate(String outputFormat) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss", Locale.ENGLISH);
        DateFormat outDateFormat = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
        try {
            return outDateFormat.format(dateFormat.parse(new Timestamp(System.currentTimeMillis()).toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentFomatedTime() {

        DateFormat outDateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        return outDateFormat.format(Calendar.getInstance().getTime());
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public static String getFormatedDateTime(String created_at) {
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat returnFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm aa", Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(created_at));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFormatedDateTime(String created_at, String format) {
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat returnFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(created_at));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void hideKeyboard(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            if (view == null) {
                return;
            }
            if (view.hasFocus()) {
                view.clearFocus();
                hideKeyboardFrom(activity, view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (imm != null && imm.isActive(view)) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getUTCfromGMT(String timeStamp) {
        return DateUtil.getUTCfromLocal(timeStamp, "yyyy-MM-dd HH:mm:ss.SSS", UTCFormat);
    }


    public static String getSlotDate(String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
        inputFormat.setTimeZone(TimeZone.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("EE, dd MMM, yyyy", Locale.ENGLISH);

        outputFormat.setTimeZone(UtcTimezone);
        try {
            return outputFormat.format(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSlotTime(String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
        DateFormat outputFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        try {
            return outputFormat.format(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSlotTimeDate(String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        inputFormat.setTimeZone(UtcTimezone);
        DateFormat outputFormat = new SimpleDateFormat("hh:mm aa EE dd MMM, yyyy", Locale.ENGLISH);
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getIncreasedTime(int timeDifference, String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(inputFormat.parse(timeStamp));
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + timeDifference);
            return inputFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSelectedSlotDate(String timeSlot) {
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        DateFormat outputFormat = new SimpleDateFormat("EE, dd MMM, yyyy", Locale.ENGLISH);
        inputFormat.setTimeZone(UtcTimezone);
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(timeSlot));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Nullable
    public static Date convertUTCTOLocal(String dateStr, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = df.parse(dateStr);
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date);
            return getDateFromString(formattedDate, pattern);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDatefromString(String dateStr, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) {
            return "";
        }
        SimpleDateFormat convetDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return convetDateFormat.format(date);
    }

    public static String getSelectedSlotTime(String timeSlot) {
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        DateFormat outputFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        inputFormat.setTimeZone(UtcTimezone);
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(timeSlot));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Calendar getCalendar(String timeStamp) {
        Calendar calendar = Calendar.getInstance();
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        inputFormat.setTimeZone(UtcTimezone);
        try {
            calendar.setTime(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }

    public static String serialize(Object object) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return Base64.encodeToString(json.getBytes("utf-8"), Base64.DEFAULT);
    }

    public static <T> T deserialize(String string, Type type) throws IOException {
        byte[] valueDecoded = Base64.decode(string.getBytes("utf-8"), Base64.DEFAULT);
        return new Gson().fromJson(new String(valueDecoded), type);
    }

    public static void showUserInputDialog(@NonNull Context context, @Nullable String title, @Nullable String message, @Nullable String editTextHint, @Nullable String positiveText, @Nullable String negativeText,
                                           @Nullable CustomDialogClickListener positiveClickListener, @Nullable CustomDialogClickListener negativeClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_user_input, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(context.getDrawable(android.R.drawable.screen_background_dark_transparent));

        EditText inputEt;
        LinearLayout inputLl;
        TextView titleTv, messageTv, cancelTv, doneTv;

        titleTv = (TextView) view.findViewById(R.id.title_tv);
        messageTv = (TextView) view.findViewById(R.id.message_tv);
        inputEt = (EditText) view.findViewById(R.id.input_et);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        doneTv = (TextView) view.findViewById(R.id.done_tv);
        inputLl = (LinearLayout) view.findViewById(R.id.input_ll);

        inputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    doneTv.setEnabled(true);
                    doneTv.setTextColor(context.getColor(R.color.color_blue));
                } else {
                    doneTv.setEnabled(false);
                    doneTv.setTextColor(context.getColor(R.color.colorGrey));
                }
            }
        });

        if (title != null) {
            titleTv.setText(title);
        }

        if (message != null) {
            messageTv.setText(message);
        }
        if (editTextHint != null) {
            inputEt.setHint(editTextHint);
        }

        if (positiveText != null) {
            doneTv.setText(positiveText);

            if (positiveClickListener != null) {
                doneTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveClickListener.onClick(dialog, inputEt.getText().toString());
                    }
                });
            }
            doneTv.setVisibility(View.VISIBLE);
        } else {
            doneTv.setVisibility(View.GONE);
        }

        if (negativeText != null) {
            cancelTv.setText(negativeText);
            if (negativeClickListener != null) {
                cancelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeClickListener.onClick(dialog, null);
                    }
                });
            }
            cancelTv.setVisibility(View.VISIBLE);
        } else {
            cancelTv.setVisibility(View.GONE);
        }

        dialog.setCancelable(false);
        dialog.show();
    }

    public static void showUserMultiInputDialog(@NonNull Context context, @Nullable String title, @Nullable String message, @Nullable List<String> editTextHintList, @Nullable String positiveText, @Nullable String negativeText,
                                                @Nullable CustomDialogClickListener positiveClickListener, @Nullable CustomDialogClickListener negativeClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_user_input, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(context.getDrawable(android.R.drawable.screen_background_dark_transparent));

        LinearLayout inputLl;
        TextView titleTv, messageTv, cancelTv, doneTv;

        EditText inputEt = (EditText) view.findViewById(R.id.input_et);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        messageTv = (TextView) view.findViewById(R.id.message_tv);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        doneTv = (TextView) view.findViewById(R.id.done_tv);
        inputLl = (LinearLayout) view.findViewById(R.id.input_ll);
        doneTv.setTextColor(context.getColor(R.color.color_blue));

        inputEt.setVisibility(View.GONE);

        if (editTextHintList == null || editTextHintList.size() == 0) {
            editTextHintList = new ArrayList<>();
            editTextHintList.add(context.getString(R.string.please_provide_the_details));
        }

        List<EditText> editTextList = new ArrayList<>();

        for (int i = 0; i < editTextHintList.size(); i++) {
            View inputView = LayoutInflater.from(context).inflate(R.layout.layout_user_input_edittext, null);

            EditText editText = inputView.findViewById(R.id.input_et);
            editText.setHint(editTextHintList.get(i));

            if (MedicalHistoryConstants.getInputTypeMap().containsKey(editTextHintList.get(i))) {
                editText.setInputType(MedicalHistoryConstants.getInputTypeMap().get(editTextHintList.get(i)));
            }

            editTextList.add(editText);
            inputLl.addView(inputView);
        }

        if (title != null) {
            titleTv.setText(title);
        }

        if (message != null) {
            messageTv.setText(message);
        }

        if (positiveText != null) {
            doneTv.setText(positiveText);

            if (positiveClickListener != null) {
                doneTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputs = " ";
                        for (int i = 0; i < editTextList.size(); i++) {
                            if (editTextList.get(i).getText().toString().isEmpty()) {
                                if (i != 0) {
                                    inputs = inputs.concat(", ");
                                }
                                inputs = inputs.concat(" ");
                            } else {
                                if (i != 0) {
                                    inputs = inputs.concat(",");
                                }
                                inputs = inputs.concat(editTextList.get(i).getText().toString());
                            }
                        }
                        positiveClickListener.onClick(dialog, inputs);
                    }
                });
            }
            doneTv.setVisibility(View.VISIBLE);
        } else {
            doneTv.setVisibility(View.GONE);
        }

        if (negativeText != null) {
            cancelTv.setText(negativeText);
            if (negativeClickListener != null) {
                cancelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeClickListener.onClick(dialog, null);
                    }
                });
            }
            cancelTv.setVisibility(View.VISIBLE);
        } else {
            cancelTv.setVisibility(View.GONE);
        }

        dialog.setCancelable(false);
        dialog.show();
    }

    public static String[] getNotificationSlotTime(String timeSlot) {

        String[] notificationSlotTimes = new String[2];
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        inputFormat.setTimeZone(UtcTimezone);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm aa, EE", Locale.ENGLISH);
        timeFormat.setTimeZone(TimeZone.getDefault());
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());

        try {
            notificationSlotTimes[0] = timeFormat.format(inputFormat.parse(timeSlot));
            notificationSlotTimes[1] = dateFormat.format(inputFormat.parse(timeSlot));

            return notificationSlotTimes;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new String[]{"", ""};
    }

    public static String getPushNotificationTimeFormat(String timeSlot) {

        DateFormat inputFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.ENGLISH);
        inputFormat.setTimeZone(UtcTimezone);
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(timeSlot));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUTCFormat(String date, String inputFormat) {
        DateFormat dateFormat = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
        DateFormat outFormat = new SimpleDateFormat(UTCFormat, Locale.ENGLISH);
        outFormat.setTimeZone(UtcTimezone);

        try {
            return outFormat.format(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isInternetEnabled(Context context) {
        // Initializing the connectivity Manager
        ConnectivityManager activeConnection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (activeConnection != null) {
            NetworkInfo networkInfo = activeConnection.getActiveNetworkInfo();
            return networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        } else {
            return false;
        }
    }

    public static void createNotification(APNSPayload data, Intent intent) {
        String title = (String) data.getAps().get(PubNubNotificationPayload.TITLE);
        String message = (String) data.getAps().get(PubNubNotificationPayload.ALERT);
        String imageUrl = data.getMedia_url();


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (imageUrl != null) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        TeleCacheUrl glideUrl = Utils.getGlideUrlWithAuth(application, imageUrl, true);

                        FutureTarget<Bitmap> futureTarget = Glide.with(application).asBitmap().load(glideUrl).submit();

                        try {
                            Bitmap imageBitmap = futureTarget.get();
                            displyNotification(title, message, imageBitmap, intent);
                        } catch (Exception e) {
                            displyNotification(title, message, null, intent);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        } else {
            Bitmap imageBitmap = BitmapFactory.decodeResource(application.getResources(), R.drawable.profile_placeholder);
            displyNotification(title, message, imageBitmap, intent);

        }
    }

    public static void createNotificationTop(APNSPayload data, Intent intent) {

        String title = (String) data.getAps().get(PubNubNotificationPayload.TITLE);
        String message = (String) data.getAps().get(PubNubNotificationPayload.ALERT);
        String imageUrl = data.getMedia_url();

        if (imageUrl != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TeleCacheUrl glideUrl = Utils.getGlideUrlWithAuth(application, imageUrl, true);
                        FutureTarget<Bitmap> futureTarget = Glide.with(application).asBitmap().load(glideUrl).submit();
                        try {
                            Bitmap imageBitmap = futureTarget.get();
                            displyNotificationOnTop(title, message, imageBitmap, intent);

                        } catch (Exception e) {
                            displyNotificationOnTop(title, message, null, intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            Bitmap imageBitmap = BitmapFactory.decodeResource(application.getResources(), R.drawable.profile_placeholder);
            displyNotificationOnTop(title, message, imageBitmap, intent);
        }

    }

    public static void displyNotification(String title, String message, Bitmap imageBitmap, Intent intent) {

        NotificationCompat.Builder notification = new NotificationCompat.Builder(application, notificationChannelId)
                .setSmallIcon(R.drawable.app_icon_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        if (imageBitmap != null) {
            notification.setLargeIcon(imageBitmap);
        }


        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(application);
            taskStackBuilder.addNextIntentWithParentStack(intent);

            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setContentIntent(pendingIntent);
        }

        Random random = new Random();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(application);
        notificationManagerCompat.notify(random.nextInt(1000), notification.build());
    }

    public static void displyNotificationOnTop(String title, String message, @Nullable Bitmap imageBitmap, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(application, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(application, notificationChannelId)
                .setSmallIcon(R.drawable.app_icon_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        if (imageBitmap != null) {
            builder.setLargeIcon(imageBitmap);
        }

        NotificationManager notifManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        Random random = new Random();
        notifManager.notify(random.nextInt(1000), notification);
    }


    public static void showOptionSelectionAlert(FragmentActivity activity,
                                                @NonNull List<String> optionList,
                                                @NonNull PickerListener pickerListener,
                                                @Nullable String positiveTitle,
                                                @Nullable String negativeTitle,
                                                @Nullable View.OnClickListener positiveListener,
                                                @Nullable View.OnClickListener negativeListener,
                                                @Nullable int positiveColor,
                                                @Nullable int negativeColor) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View alertView = LayoutInflater.from(activity).inflate(R.layout.view_option_select_alert, null);
        builder.setView(alertView);

        AlertDialog alertDialog = builder.create();

        RecyclerView optionsRv;
        CardView cancelCv;
        TextView negativeTv;
        CardView doneCv;
        TextView positiveTv;

        optionsRv = (RecyclerView) alertView.findViewById(R.id.options_rv);
        cancelCv = (CardView) alertView.findViewById(R.id.cancel_cv);
        negativeTv = (TextView) alertView.findViewById(R.id.negative_tv);
        doneCv = (CardView) alertView.findViewById(R.id.done_cv);
        positiveTv = (TextView) alertView.findViewById(R.id.positive_tv);

        alertDialog.setCanceledOnTouchOutside(false);
        optionsRv.setLayoutManager(new LinearLayoutManager(activity));
        optionsRv.setAdapter(new OptionsSelectionAdapter(activity, optionList, pickerListener, alertDialog));

        if (positiveTitle != null) {
            doneCv.setVisibility(View.VISIBLE);
            positiveTv.setText(positiveTitle);

            if (positiveListener != null) {
                doneCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveListener.onClick(v);
                        alertDialog.dismiss();
                    }
                });
            }
        }

        if (negativeTitle != null) {
            cancelCv.setVisibility(View.VISIBLE);
            negativeTv.setText(negativeTitle);

            if (negativeListener != null) {
                cancelCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeListener.onClick(v);
                        alertDialog.dismiss();
                    }
                });
            }
        }

        if (positiveColor != 0) {
            positiveTv.setTextColor(activity.getColor(positiveColor));
        }

        if (negativeColor != 0) {
            negativeTv.setTextColor(activity.getColor(negativeColor));
        }

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        alertDialog.show();
    }

    public static void showOptionsSelectionAlert(Context context, List<String> options, int selectedPosition, PickerListener pickerListener) {
        OptionSelectionDialog optionSelectionDialog = new OptionSelectionDialog(context, options, selectedPosition, pickerListener);
        optionSelectionDialog.show();
    }

    public static String getDisplayDuration(int durationInSecs) {
        String displayDuration;
        if (durationInSecs < 60) {
            displayDuration = durationInSecs + " sec";
        } else {
            displayDuration = (durationInSecs / 60) + " min " + (durationInSecs % 60) + " sec";
        }
        return displayDuration;
    }

    public static void showInviteAlert(FragmentActivity context, Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View alertView = LayoutInflater.from(context).inflate(R.layout.view_invite_alert, null);
        builder.setView(alertView);

        AlertDialog alertDialog = builder.create();

        TextView inviteManuallyTv;
        TextView inviteContactsTv;
        CardView cancelCv;

        inviteManuallyTv = (TextView) alertView.findViewById(R.id.invite_manually_tv);
        inviteContactsTv = (TextView) alertView.findViewById(R.id.invite_contacts_tv);
        cancelCv = (CardView) alertView.findViewById(R.id.cancel_cv);

        inviteManuallyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(context, InviteUserActivity.class);
                if (bundle != null) {
                    intent.putExtras(bundle);
                }
                context.startActivity(intent);
            }
        });

        inviteContactsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(context, InviteContactUserActivity.class);
                if (bundle != null) {
                    intent.putExtras(bundle);
                }
                context.startActivity(intent);
            }
        });

        cancelCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        alertDialog.show();
    }

    public static void showDoctorOverflowMenu(FragmentActivity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View alertView = LayoutInflater.from(context).inflate(R.layout.view_invite_alert, null);
        builder.setView(alertView);

        AlertDialog alertDialog = builder.create();

        TextView pendingInvitesTv;
        TextView broadCastMessageTv;
        CardView cancelCv;

        pendingInvitesTv = (TextView) alertView.findViewById(R.id.invite_manually_tv);
        broadCastMessageTv = (TextView) alertView.findViewById(R.id.invite_contacts_tv);
        pendingInvitesTv.setText(R.string.pending_invites);
        broadCastMessageTv.setVisibility(View.GONE);
        cancelCv = (CardView) alertView.findViewById(R.id.cancel_cv);

        pendingInvitesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                context.startActivity(new Intent(context, PendingInvitesActivity.class));

            }
        });


        cancelCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        alertDialog.show();
    }

    public static void showMonitoringFilter(ArrayList<String> printOptions, FragmentActivity activity, OnListItemSelectInterface onListItemSelectInterface) {


        Calendar currentMonth = Calendar.getInstance();

        Calendar previousMonth = Calendar.getInstance();
        previousMonth.add(Calendar.MONTH, -1);

        String current = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String previous = previousMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        List<String> monitoringFilterList;
        if (printOptions == null) {
            monitoringFilterList = new ArrayList<>(Arrays.asList(activity.getString(R.string.last_week), current, previous, activity.getString(R.string.custom_range)));
        } else {
            monitoringFilterList = printOptions;
        }

        showOptionsSelectionAlert(activity, monitoringFilterList, -1, new PickerListener() {
            @Override
            public void didSelectedItem(int position) {
                String selectedItem = monitoringFilterList.get(position);

                Bundle bundle = new Bundle();

                if (selectedItem.equals(activity.getString(R.string.custom_range))) {

                    Utils.showDatePickerDialog(activity, null, Constants.TILL_CURRENT_DAY, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar start = Calendar.getInstance();
                            start.set(year, month, dayOfMonth, 0, 0, 0);

                            String startDate = new Timestamp(start.getTimeInMillis()).toString();

                            Utils.showDatePickerDialog(activity, start, Constants.TILL_CURRENT_DAY, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    Calendar end = Calendar.getInstance();
                                    end.set(year, month, dayOfMonth, 23, 59, 59);

                                    String endDate = new Timestamp(end.getTimeInMillis()).toString();

                                    bundle.putString(ArgumentKeys.START_DATE, getUTCfromGMT(startDate));
                                    bundle.putString(ArgumentKeys.END_DATE, getUTCfromGMT(endDate));
                                    bundle.putString(Constants.SELECTED_ITEM, selectedItem);

                                    onListItemSelectInterface.onListItemSelected(position, bundle);

                                }
                            });

                        }
                    });

                } else if (selectedItem.equals(current) || selectedItem.equals(previous)) {
                    String startDate, endDate;
                    Calendar calendar = (Calendar) currentMonth.clone();

                    if (selectedItem.equals(previous)) {
                        calendar = (Calendar) previousMonth.clone();
                    }

                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                    startDate = getUTCfromGMT(new Timestamp(calendar.getTimeInMillis()).toString());

                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                    endDate = getUTCfromGMT(new Timestamp(calendar.getTimeInMillis()).toString());

                    bundle.putString(ArgumentKeys.START_DATE, startDate);
                    bundle.putString(ArgumentKeys.END_DATE, endDate);
                    bundle.putString(Constants.SELECTED_ITEM, selectedItem);

                    onListItemSelectInterface.onListItemSelected(position, bundle);

                } else {
                    bundle.putString(Constants.SELECTED_ITEM, selectedItem);
                    onListItemSelectInterface.onListItemSelected(position, bundle);

                }
            }

            @Override
            public void didCancelled() {

            }
        });
    }

    private static String getTimeStampFromDatePicker(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return new Timestamp(calendar.getTimeInMillis()).toString();
    }

    public static void greyoutProfile(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

    public static void removeGreyoutProfile(ImageView imageView) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(1);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

    public static void sendHelpEmail(Context context) {
        String phoneNumber = "", userName = "", appName = "", noteMessage = "";
        WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
        if (whoAmIApiResponseModel != null) {
            phoneNumber = whoAmIApiResponseModel.getPhone();
            userName = whoAmIApiResponseModel.getUserDisplay_name();
        }
        appName = application.getString(R.string.app_name);
        noteMessage = application.getString(R.string.helpmail_note_message);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String mailto = null;
        try {
            mailto = "mailto:" + context.getString(R.string.mail_to) +
                    "?cc=" +
                    "&subject=" +
                    "&body=" + Uri.encode(String.format("%s <br/><br />State your Issue : <br/><br /><br /><br />Phone Number : %s <br/><br /><br/><br />App Name : %s<br />App Version : " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + "<br />Device Type : " + Build.MODEL + "<br />OS Details : " + Build.VERSION.RELEASE + "<br />Region : " + Locale.getDefault().getLanguage() + ", " + TimeZone.getDefault().getID() + "<br /><br />Cheers! ", noteMessage, phoneNumber, appName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        intent.setData(Uri.parse(mailto));

        context.startActivity(intent);

    }

    public static void updateLastLogin() {
        String utcDate = Utils.getUTCfromGMT(new Timestamp(System.currentTimeMillis()).toString());
        String lastLogin = Utils.getDayMonthYearTime(utcDate);
        Log.e("aswin", "updateLastLogin: " + lastLogin);

        appPreference.setString(PreferenceConstants.LAST_LOGIN, lastLogin);
    }

    public static void showMultichoiseItemSelectAlertDialog(@NonNull Context context, @NonNull String title, @NonNull String[] itemsList, @NonNull boolean[] selectedList, @NonNull String positiveTitle, @NonNull String negativeTitle,
                                                            @NonNull OnMultipleChoiceInterface multipleChoiceInterface) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMultiChoiceItems(itemsList, selectedList, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedList[which] = isChecked;
            }
        });
        builder.setPositiveButton(positiveTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                multipleChoiceInterface.onSelected(selectedList);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(negativeTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public interface OnMultipleChoiceInterface {
        void onSelected(boolean[] selectedList);
    }

    public static String getPaginatedTitle(@NonNull String title, @NonNull int count) {
        if (count > 0) {
            return title + " ( " + count + " )";
        } else {
            return title;
        }
    }

    public static String getMonitoringTitle(@NonNull String startDate, @NonNull String endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(Utils.getDateFromString(startDate));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(Utils.getDateFromString(endDate));

        String outputFormat = Utils.mmm_dd;
        if (startCal.get(Calendar.YEAR) != endCal.get(Calendar.YEAR)) {
            outputFormat = Utils.mmm_yyyy;
        }
        return Utils.getFormatedDateTime(startDate, outputFormat) + " - " + Utils.getFormatedDateTime(endDate, outputFormat);
    }

    public static String replaceAmpersand(String input) {
        if (input != null && input.contains("&")) {
            return input.replace("&", "and");
        }
        return input;
    }

    public static Bitmap mergeBitmap(Bitmap fr, Bitmap sc) {
        Bitmap comboBitmap;

        int width, height;

        width = fr.getWidth() + sc.getWidth();
        height = fr.getHeight();

        comboBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(comboBitmap);


        comboImage.drawBitmap(fr, 0f, 0f, null);
        comboImage.drawBitmap(sc, fr.getWidth(), 0f, null);
        return comboBitmap;
    }

    public static void validUserToLogin(Context context) {
        WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();

        if (whoAmIApiResponseModel != null && whoAmIApiResponseModel.getUser_activated() != null &&
                whoAmIApiResponseModel.getUser_activated().equals(Constants.ACTIVATION_PENDING)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ArgumentKeys.IS_VERIFY_OTP, true);

            context.startActivity(new Intent(context, SignUpActivity.class).putExtras(bundle)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (UserDetailPreferenceManager.isProfileInComplete()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ArgumentKeys.IS_DETAIL_PENDING, true);

            context.startActivity(new Intent(context, SignUpActivity.class).putExtras(bundle)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            UserDetailPreferenceManager.didUserLoggedIn();
            Utils.updateLastLogin();
            context.startActivity(new Intent(context, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
