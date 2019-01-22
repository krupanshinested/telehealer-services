package com.thealer.telehealer.common;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.text.method.KeyListener;
import android.util.Base64;
import android.util.Base64InputStream;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by Aswin on 12,October,2018
 */
public class Utils {

    public static Dialog showDatePickerDialog(FragmentActivity activity, int type) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                sendDateBroadCast(activity, year, month, dayOfMonth);
            }
        }, year, month, day);


        switch (type) {
            case Constants.TYPE_DOB:
                calendar.set(year - 18, month, day);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                break;
            case Constants.TYPE_EXPIRATION:
                calendar.set(year, month, day + 1);
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
        }

        datePickerDialog.show();
        return datePickerDialog;
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

    public static String getFormatedDate(int year, int month, int dayOfMonth) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return String.valueOf(dayOfMonth) + " " + months[month] + ", " + year;
    }

    public static boolean isEmailValid(String email) {
        String emailPattern = "[A-Z0-9a-z-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}";
        return Pattern.matches(emailPattern, email);
    }

    public static boolean isValidState(String state) {

        String statePattern = "A[LKSZRAEP]|C[AOT]|D[EC]|F[LM]|G[AU]|HI|I[ADLN]|K[SY]|LA|M[ADEHINOPST]|N[CDEHJMVY]|O[HKR]|P[ARW]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY]";

        return Pattern.matches(statePattern, state.toUpperCase());
    }

    public static boolean isDateExpired(String date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH);
            Date inputDate = dateFormat.parse(date);
            Date currentDate = new Date();

            return inputDate.compareTo(currentDate) >= 0;

        } catch (ParseException e) {
            return isDateExpired(date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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

    public static boolean isDateTimeExpired(String date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date inputDate = dateFormat.parse(date);
            Date currentDate = new Date();

            return currentDate.compareTo(inputDate) >= 0;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
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

    public static void setImageWithGlide(Context context, ImageView imageView, String path, Drawable placeHolder, boolean isUrlAuthNeeded) {
        if (path != null && !path.isEmpty()) {
            GlideUrl glideUrl;
            if (isUrlAuthNeeded) {
                path = context.getString(R.string.api_base_url) + context.getString(R.string.get_image_url) + path;
                glideUrl = new GlideUrl(path, new Headers() {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put(Constants.HEADER_AUTH_TOKEN, TeleHealerApplication.appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN));
                        return hashMap;
                    }
                });
            } else {
                glideUrl = new GlideUrl(path);
            }
            if (placeHolder != null) {
                Glide.with(context).load(glideUrl).apply(new RequestOptions().placeholder(placeHolder)).into(imageView);
            } else {
                Glide.with(context).load(glideUrl).into(imageView);
            }
        } else {
            imageView.setImageDrawable(context.getDrawable(R.drawable.profile_placeholder));
        }
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dateFormat.parse(dateString);
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            dateFormat = new SimpleDateFormat("dd MMM, yyyy");
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat returnFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDayMonth(String date) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat returnFormat = new SimpleDateFormat("dd MMM, hh:mm aa", Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDoctorDisplayName(String first_name, String last_name, String title) {
        return "Dr. " + first_name + " " + last_name + " " + title;
    }

    public static String getPatientDisplayName(String first_name, String last_name) {
        first_name = first_name.replace(first_name.charAt(0), String.valueOf(first_name.charAt(0)).toUpperCase().charAt(0));
        last_name = last_name.replace(last_name.charAt(0), String.valueOf(last_name.charAt(0)).toUpperCase().charAt(0));
        return first_name + " " + last_name;
    }

    public static AlertDialog.Builder showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialog;
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
        DateFormat outDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat returnFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm aa", Locale.ENGLISH);
        returnFormat.setTimeZone(TimeZone.getDefault());
        try {
            return returnFormat.format(dateFormat.parse(created_at));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFormatedDateTime(String created_at,String format) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        hideKeyboardFrom(activity, view);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getUTCfromGMT(String timeStamp) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        inputFormat.setTimeZone(TimeZone.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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

        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateFormat outputFormat = new SimpleDateFormat("EE, dd MMM, yyyy");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(timeSlot));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSelectedSlotTime(String timeSlot) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateFormat outputFormat = new SimpleDateFormat("hh:mm aa");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(timeSlot));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String serialize(Object object) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return Base64.encodeToString(json.getBytes("utf-8"),Base64.DEFAULT);
    }

    public static <T> T deserialize(String string, Type type) throws IOException {
        byte[] valueDecoded = Base64.decode(string.getBytes("utf-8"),Base64.DEFAULT);
        return new Gson().fromJson(new String(valueDecoded), type);
    }
}
