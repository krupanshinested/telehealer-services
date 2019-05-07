package com.thealer.telehealer.common;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Base64;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.views.common.CustomDialogClickListener;
import com.thealer.telehealer.views.common.CustomDialogs.OptionSelectionDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.OptionsSelectionAdapter;
import com.thealer.telehealer.views.inviteUser.InviteContactUserActivity;
import com.thealer.telehealer.views.inviteUser.InviteUserActivity;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants;

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

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

import static com.thealer.telehealer.TeleHealerApplication.application;
import static com.thealer.telehealer.TeleHealerApplication.notificationChannelId;


/**
 * Created by Aswin on 12,October,2018
 */
public class Utils {

    public static final String UTCFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final TimeZone UtcTimezone = TimeZone.getTimeZone("UTC");
    public static final String defaultDateFormat = "dd MMM, yyyy";
    public static final String yyyy_mm = "yyyy-MM";
    public static final String yyyy_mm_dd = "yyyy-MM-dd";
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

    public static Dialog showDatePickerDialog(FragmentActivity activity, int type, DatePickerDialog.OnDateSetListener dateSetListener) {
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

    public static void showTimePickerDialog(FragmentActivity activity, TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    public static String getFormatedDate(int year, int month, int dayOfMonth) {
        return String.valueOf(dayOfMonth) + " " + months[month] + ", " + year;
    }

    public static boolean isEmailValid(String email) {
        String emailPattern = "[A-Z0-9a-z-._+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,64}";
        return Pattern.matches(emailPattern, email);
    }

    public static boolean isValidState(String state) {

        String statePattern = "A[LKSZRAEP]|C[AOT]|D[EC]|F[LM]|G[AU]|HI|I[ADLN]|K[SY]|LA|M[ADEHINOPST]|N[CDEHJMVY]|O[HKR]|P[ARW]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY]";

        return Pattern.matches(statePattern, state.toUpperCase());
    }

    public static boolean isDateExpired(String date) {
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
        Log.e("aswin", "isOneDayBefore: " + calendar.getTime());
        Log.e("aswin", "isOneDayBefore: " + input.getTime());
        return input.before(calendar);
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
            GlideUrl glideUrl;
            if (isUrlAuthNeeded) {
                glideUrl = getGlideUrlWithAuth(context, path, decrypt);
            } else {
                glideUrl = new GlideUrl(path);
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

    public static GlideUrl getGlideUrlWithAuth(Context context, String path, boolean decrypt) {
        path = context.getString(R.string.api_base_url) + context.getString(R.string.get_image_url) + path+"&decrypt="+decrypt;

        return new GlideUrl(path, new Headers() {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.HEADER_AUTH_TOKEN, TeleHealerApplication.appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN));
                return hashMap;
            }
        });
    }

    public static void setGenderImage(Context context, ImageView genderIv, String gender) {

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

    public static String getFormatedTime(String updated_at) {
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
        dateFormat.setTimeZone(UtcTimezone);

        DateFormat returnFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            Date date = dateFormat.parse(updated_at);
            return returnFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date getDateFromString(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
        dateFormat.setTimeZone(UtcTimezone);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getCurrentZoneDate(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat outFormat = new SimpleDateFormat(UTCFormat);
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
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getDateFromPossibleFormat(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
        try {
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            dateFormat = new SimpleDateFormat(defaultDateFormat);
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

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
        dateFormat.setTimeZone(UtcTimezone);
        DateFormat returnFormat = new SimpleDateFormat(defaultDateFormat, Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }

    public static String getYearMonthDay(String date) {

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
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

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
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

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
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

        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
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
        return "Dr. " + first_name + " " + last_name + " " + ((title != null) ? title : "");
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");
        DateFormat outDateFormat = new SimpleDateFormat(defaultDateFormat, Locale.ENGLISH);
        try {
            return outDateFormat.format(dateFormat.parse(new Timestamp(System.currentTimeMillis()).toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentFomatedDate(String outputFormat) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");
        DateFormat outDateFormat = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
        try {
            return outDateFormat.format(dateFormat.parse(new Timestamp(System.currentTimeMillis()).toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentFomatedTime() {

        DateFormat outDateFormat = new SimpleDateFormat("hh:mm aa");
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
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
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
        DateFormat dateFormat = new SimpleDateFormat(UTCFormat);
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
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (view.hasFocus()) {
            view.clearFocus();
            hideKeyboardFrom(activity, view);
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (imm != null && imm.isActive(view)) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getUTCfromGMT(String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        inputFormat.setTimeZone(TimeZone.getDefault());
        DateFormat outputFormat = new SimpleDateFormat(UTCFormat);
        outputFormat.setTimeZone(UtcTimezone);
        try {
            return outputFormat.format(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSlotDate(String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        inputFormat.setTimeZone(TimeZone.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("EE, dd MMM, yyyy");

        outputFormat.setTimeZone(UtcTimezone);
        try {
            return outputFormat.format(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSlotTime(String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        DateFormat outputFormat = new SimpleDateFormat("hh:mm aa");
        try {
            return outputFormat.format(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSlotTimeDate(String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat);
        inputFormat.setTimeZone(UtcTimezone);
        DateFormat outputFormat = new SimpleDateFormat("hh:mm aa EE dd MMM, yyyy");
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getIncreasedTime(int timeDifference, String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat);
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
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat);
        DateFormat outputFormat = new SimpleDateFormat("EE, dd MMM, yyyy");
        inputFormat.setTimeZone(UtcTimezone);
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(timeSlot));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSelectedSlotTime(String timeSlot) {
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat);
        DateFormat outputFormat = new SimpleDateFormat("hh:mm aa");
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
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat);
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
        DateFormat inputFormat = new SimpleDateFormat(UTCFormat);
        inputFormat.setTimeZone(UtcTimezone);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm aa, EE", Locale.ENGLISH);
        timeFormat.setTimeZone(TimeZone.getDefault());
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
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

        DateFormat inputFormat = new SimpleDateFormat(UTCFormat);
        DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
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
        DateFormat dateFormat = new SimpleDateFormat(inputFormat);
        DateFormat outFormat = new SimpleDateFormat(UTCFormat);
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

        String title = data.getAps().get(PubNubNotificationPayload.TITLE);
        String message = data.getAps().get(PubNubNotificationPayload.ALERT);
        String imageUrl = data.getAps().get(PubNubNotificationPayload.MEDIA_URL);


        if (imageUrl != null) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        GlideUrl glideUrl = Utils.getGlideUrlWithAuth(application, imageUrl, true);

                        FutureTarget<Bitmap> futureTarget = Glide.with(application).asBitmap().load(glideUrl).submit();

                        Bitmap imageBitmap = futureTarget.get();

                        displyNotification(title, message, imageBitmap, intent);

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

    public static void displyNotification(String title, String message, Bitmap imageBitmap, Intent intent) {

        NotificationCompat.Builder notification = new NotificationCompat.Builder(application, notificationChannelId)
                .setSmallIcon(R.drawable.app_icon)
                .setBadgeIconType(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(imageBitmap)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

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

    public static void showOptionsSelectionAlert(Context context, List<String> options, PickerListener pickerListener) {
        OptionSelectionDialog optionSelectionDialog = new OptionSelectionDialog(context, options, pickerListener);
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

    public static void showMonitoringFilter(FragmentActivity activity, OnListItemSelectInterface onListItemSelectInterface) {


        Calendar currentMonth = Calendar.getInstance();

        Calendar previousMonth = Calendar.getInstance();
        previousMonth.add(Calendar.MONTH, -1);

        String current = currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String previous = previousMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        List<String> monitoringFilterList = new ArrayList<>(Arrays.asList(activity.getString(R.string.last_week), current, previous, activity.getString(R.string.all)));

        showOptionsSelectionAlert(activity, monitoringFilterList, new PickerListener() {
            @Override
            public void didSelectedItem(int position) {
                String selectedItem = monitoringFilterList.get(position);

                Bundle bundle = new Bundle();

                if (selectedItem.equals(activity.getString(R.string.last_week)) || selectedItem.equals(activity.getString(R.string.all))) {
                    bundle.putString(Constants.SELECTED_ITEM, selectedItem);
                } else {
                    String startDate, endDate;
                    Calendar calendar = (Calendar) currentMonth.clone();

                    if (selectedItem.equals(previous)) {
                        calendar = (Calendar) previousMonth.clone();
                    }

                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                    startDate = getUTCfromGMT(new Timestamp(calendar.getTimeInMillis()).toString());
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    endDate = getUTCfromGMT(new Timestamp(calendar.getTimeInMillis()).toString());

                    bundle.putString(ArgumentKeys.START_DATE, startDate);
                    bundle.putString(ArgumentKeys.END_DATE, endDate);
                }

                onListItemSelectInterface.onListItemSelected(position, bundle);
            }

            @Override
            public void didCancelled() {

            }
        });
    }
}
