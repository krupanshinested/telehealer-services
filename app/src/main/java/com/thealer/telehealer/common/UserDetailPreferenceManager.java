package com.thealer.telehealer.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Util.TeleCacheUrl;
import com.thealer.telehealer.common.pubNub.TelehealerFirebaseMessagingService;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;

/**
 * Created by Aswin on 04,December,2018
 */
public class UserDetailPreferenceManager {

    public static void didUserLoggedIn() {
        appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, true);
        TelehealerFirebaseMessagingService.refresh();
    }


    public static final String PROFILE_INCOMPLETE = "PROFILE_INCOMPLETE";

    public static String getUserDisplayName() {
        if (UserType.isUserDoctor()) {
            return getFirst_name() + " " + getLast_name() + " , " + getTitle();
        } else {
            return getFirst_name() + " " + getLast_name();
        }
    }

    public static String getUser_guid() {
        return appPreference.getString(PreferenceConstants.USER_GUID);
    }

    public static void setUser_guid(String user_guid) {
        appPreference.setString(PreferenceConstants.USER_GUID, user_guid);
    }

    public static String getVersion() {
        return appPreference.getString(PreferenceConstants.VERSION);
    }

    public static void setVersion(String version) {
        appPreference.setString(PreferenceConstants.VERSION, version);
    }

    public static String getUser_activated() {
        return appPreference.getString(PreferenceConstants.USER_ACTIVATED);
    }

    public static void setUser_activated(String user_activated) {
        if (user_activated != null) {
            switch (user_activated) {
                case Constants.ACTIVATION_PENDING:
                    appPreference.setBoolean(PreferenceConstants.IS_USER_VALIDATED, false);
                    break;
                case Constants.ONBOARDING_PENDING:
                    appPreference.setBoolean(PreferenceConstants.IS_USER_ACTIVATED, false);
                    break;
                case Constants.OFFLINE:
                case Constants.AVAILABLE:
                case Constants.ACTIVATED:
                    appPreference.setBoolean(PreferenceConstants.IS_USER_ACTIVATED, true);
                    appPreference.setBoolean(PreferenceConstants.IS_USER_VALIDATED, true);
                    break;
            }
        }
        appPreference.setString(PreferenceConstants.USER_ACTIVATED, user_activated);
    }

    public static String getFirst_name() {
        return appPreference.getString(PreferenceConstants.USER_FIRST_NAME);
    }

    public static void setFirst_name(String first_name) {
        appPreference.setString(PreferenceConstants.USER_FIRST_NAME, first_name);
    }

    public static String getLast_name() {
        if (appPreference.getString(PreferenceConstants.USER_LAST_NAME)!=null)
            return appPreference.getString(PreferenceConstants.USER_LAST_NAME);
        else
            return "";
    }

    public static void setLast_name(String last_name) {
        appPreference.setString(PreferenceConstants.USER_LAST_NAME, last_name);
    }

    public static String getStatus() {
        return appPreference.getString(PreferenceConstants.USER_STATUS);
    }

    public static void setStatus(String status) {
        appPreference.setString(PreferenceConstants.USER_STATUS, status);
    }

    public static String getEmail() {
        return appPreference.getString(PreferenceConstants.USER_EMAIL);
    }

    public static void setEmail(String email) {
        appPreference.setString(PreferenceConstants.USER_EMAIL, email);
    }

    public static String getUser_avatar() {
        return appPreference.getString(PreferenceConstants.USER_AVATAR);
    }

    public static void setUser_avatar(String user_avatar) {
        appPreference.setString(PreferenceConstants.USER_AVATAR, user_avatar);
    }

    public static String getRole() {
        return appPreference.getString(PreferenceConstants.USER_ROLE);
    }

    public static void setRole(String role) {
        appPreference.setString(PreferenceConstants.USER_ROLE, role);
    }

    public static String getPhone() {
        return appPreference.getString(PreferenceConstants.USER_PHONE);
    }

    public static void setPhone(String phone) {
        appPreference.setString(PreferenceConstants.USER_PHONE, phone);
    }

    public static String getGender() {
        return appPreference.getString(PreferenceConstants.USER_GENDER);
    }

    public static void setGender(String gender) {
        appPreference.setString(PreferenceConstants.USER_GENDER, gender);
    }

    public static String getDob() {
        return appPreference.getString(PreferenceConstants.USER_DOB);
    }

    public static void setDob(String dob) {
        appPreference.setString(PreferenceConstants.USER_DOB, dob);
    }

    public static int getAppt_length() {
        return appPreference.getInt(PreferenceConstants.USER_APPT_LENGTH);
    }

    public static void setAppt_length(int appt_length) {
        appPreference.setInt(PreferenceConstants.USER_APPT_LENGTH, appt_length);
    }

    public  static String getAppt_start_time() {
        return appPreference.getString(PreferenceConstants.USER_APPT_START_TIME);
    }

    public static  void setAppt_start_time(String appt_start_time) {
        appPreference.setString(PreferenceConstants.USER_APPT_START_TIME, appt_start_time);
    }

    public static String getAppt_end_time() {
        return appPreference.getString(PreferenceConstants.USER_APPT_END_TIME);
    }

    public static void setAppt_end_time(String appt_end_time) {
        appPreference.setString(PreferenceConstants.USER_APPT_END_TIME, appt_end_time);
    }
    public static String getNpi() {
        return appPreference.getString(PreferenceConstants.USER_NPI);
    }

    public static void setNpi(String npi) {
        appPreference.setString(PreferenceConstants.USER_NPI, npi);
    }

    public static String getTitle() {
        return appPreference.getString(PreferenceConstants.USER_TITLE);
    }

    public static void setTitle(String title) {
        appPreference.setString(PreferenceConstants.USER_TITLE, title);
    }

    public static String getSignature() {
        return appPreference.getString(PreferenceConstants.USER_SIGNATURE);
    }

    public static void setSignature(String signature) {
        appPreference.setString(PreferenceConstants.USER_SIGNATURE, signature);
    }

    public static String getSpeciality() {
        return appPreference.getString(PreferenceConstants.USER_SPECIALITY);
    }

    public static void setSpeciality(String speciality) {
        appPreference.setString(PreferenceConstants.USER_SPECIALITY, speciality);
    }

    public static List<LicensesBean> getLicenses() {
        String licenscesList = appPreference.getString(PreferenceConstants.USER_LICENSES);
        Gson gson = new Gson();

        Type type = new TypeToken<List<LicensesBean>>() {
        }.getType();
        List<LicensesBean> license = gson.fromJson(licenscesList, type);

        if (license != null) {
            return license;
        } else {
            return new ArrayList<>();
        }
    }

    public static void setLicenses(List<LicensesBean> licenses) {
        Gson gson = new Gson();
        String license = gson.toJson(licenses);
        appPreference.setString(PreferenceConstants.USER_LICENSES, license);
    }

    public static WhoAmIApiResponseModel getWhoAmIResponse() {
        return new Gson().fromJson(appPreference.getString(PreferenceConstants.WHO_AM_I_RESPONSE), WhoAmIApiResponseModel.class);
    }

    public static void insertUserDetail(WhoAmIApiResponseModel whoAmIApiResponseModel) {
        String response = new Gson().toJson(whoAmIApiResponseModel);
        appPreference.setString(PreferenceConstants.WHO_AM_I_RESPONSE, response);

        setAppt_start_time(whoAmIApiResponseModel.getAppt_start_time());
        setAppt_end_time(whoAmIApiResponseModel.getAppt_end_time());
        setAppt_length(whoAmIApiResponseModel.getAppt_length());
        setDob(whoAmIApiResponseModel.getDob());
        setEmail(whoAmIApiResponseModel.getEmail());
        setFirst_name(whoAmIApiResponseModel.getFirst_name());
        setLast_name(whoAmIApiResponseModel.getLast_name());
        setGender(whoAmIApiResponseModel.getGender());
        setNpi(whoAmIApiResponseModel.getDoctorNpi());
        setPhone(whoAmIApiResponseModel.getPhone());
        setRole(whoAmIApiResponseModel.getRole());
        setSpeciality(whoAmIApiResponseModel.getDoctorSpecialist());
        setStatus(whoAmIApiResponseModel.getStatus());
        setUser_activated(whoAmIApiResponseModel.getUser_activated());
        setUser_avatar(whoAmIApiResponseModel.getUser_avatar());
        setUser_guid(whoAmIApiResponseModel.getUser_guid());
        setVersion(whoAmIApiResponseModel.getVersion());
        setFirstTimePurchase(whoAmIApiResponseModel.isFirst_time_subscription_purchased());

        if (whoAmIApiResponseModel.getUser_detail() != null) {

            setSignature(whoAmIApiResponseModel.getUser_detail().getSignature());

            if (whoAmIApiResponseModel.getUser_detail().getData() != null) {
                setTitle(whoAmIApiResponseModel.getUser_detail().getData().getTitle());
                setLicenses(whoAmIApiResponseModel.getUser_detail().getData().getLicenses());
            }
        }

        deleteUserImageCaches(application);
    }

    private static void setFirstTimePurchase(boolean isPurchase) {
        appPreference.setBoolean(PreferenceConstants.IS_USER_PURCHASED, isPurchase);
    }

    public static void invalidateUser() {
        appPreference.setString(PreferenceConstants.WHO_AM_I_RESPONSE, null);
        appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, null);
        appPreference.setString(PreferenceConstants.USER_REFRESH_TOKEN, null);
        appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, false);
        appPreference.setInt(Constants.QUICK_LOGIN_TYPE, -1);
        appPreference.setString(Constants.QUICK_LOGIN_PIN, null);
    }

    public static String getInstallType() {
        return appPreference.getString(PreferenceConstants.APP_INSTALL_TYPE);
    }

    public static void setInstallType(String installType) {
        String type = getInstallType();
        if (type != null && !type.isEmpty() && !type.equalsIgnoreCase(installType)) {
            invalidateUser();
        }
        appPreference.setString(PreferenceConstants.APP_INSTALL_TYPE, installType);
    }

    public static String getCountryCode() {
        return appPreference.getString(PreferenceConstants.APP_INSTALL_TYPE_COUNTRY_CODE);
    }

    public static void setCountryCode(String countryCode) {
        appPreference.setString(PreferenceConstants.APP_INSTALL_TYPE_COUNTRY_CODE, countryCode);
    }

    public static void deleteAllPreference() {
        UserDetailPreferenceManager.invalidateUser();
    }


    private static void deleteUserImageCaches(Context context) {
        WhoAmIApiResponseModel whoAmIApiResponseModel = getWhoAmIResponse();
        if (whoAmIApiResponseModel == null) {
            return;
        }

        try {
            String path = whoAmIApiResponseModel.getUser_avatar();
            if (!TextUtils.isEmpty(path)) {
                TeleCacheUrl avatar = new TeleCacheUrl(path);
                File avatarCacheFile = Glide.getPhotoCacheDir(context, avatar.getCacheKey());
                if (avatarCacheFile != null) {
                    avatarCacheFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = whoAmIApiResponseModel.getUser_detail().getSignature();
            if (!TextUtils.isEmpty(path)) {
                TeleCacheUrl signature = new TeleCacheUrl(path);
                File signatureCacheFile = Glide.getPhotoCacheDir(context, signature.getCacheKey());
                if (signatureCacheFile != null) {
                    signatureCacheFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = whoAmIApiResponseModel.getUser_detail().getData().getCertification();
            if (!TextUtils.isEmpty(path)) {
                TeleCacheUrl certificate = new TeleCacheUrl(path);
                File certificateCacheFile = Glide.getPhotoCacheDir(context, certificate.getCacheKey());
                if (certificateCacheFile != null) {
                    certificateCacheFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = whoAmIApiResponseModel.getUser_detail().getData().getDegree();
            if (!TextUtils.isEmpty(path)) {
                TeleCacheUrl degree = new TeleCacheUrl(path);
                File degreeCacheFile = Glide.getPhotoCacheDir(context, degree.getCacheKey());
                if (degreeCacheFile != null) {
                    degreeCacheFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = whoAmIApiResponseModel.getUser_detail().getData().getDiploma_certificate();
            if (!TextUtils.isEmpty(path)) {
                TeleCacheUrl url = new TeleCacheUrl(path);
                File cacheFile = Glide.getPhotoCacheDir(context, url.getCacheKey());
                if (cacheFile != null) {
                    cacheFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = whoAmIApiResponseModel.getUser_detail().getData().getInsurance_front();
            if (!TextUtils.isEmpty(path)) {
                TeleCacheUrl url = new TeleCacheUrl(path);
                File cacheFile = Glide.getPhotoCacheDir(context, url.getCacheKey());
                if (cacheFile != null) {
                    cacheFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = whoAmIApiResponseModel.getUser_detail().getData().getInsurance_back();
            if (!TextUtils.isEmpty(path)) {
                TeleCacheUrl url = new TeleCacheUrl(path);
                File cacheFile = Glide.getPhotoCacheDir(context, url.getCacheKey());
                if (cacheFile != null) {
                    cacheFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = whoAmIApiResponseModel.getUser_detail().getData().getSecondary_insurance_front();
            if (!TextUtils.isEmpty(path)) {
                TeleCacheUrl url = new TeleCacheUrl(path);
                File cacheFile = Glide.getPhotoCacheDir(context, url.getCacheKey());
                if (cacheFile != null) {
                    cacheFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = whoAmIApiResponseModel.getUser_detail().getData().getSecondary_insurance_back();
            if (!TextUtils.isEmpty(path)) {
                TeleCacheUrl url = new TeleCacheUrl(path);
                File cacheFile = Glide.getPhotoCacheDir(context, url.getCacheKey());
                if (cacheFile != null) {
                    cacheFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(context.getString(R.string.profile_picture_updated)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isProfileInComplete() {
        WhoAmIApiResponseModel whoAmIApiResponseModel = getWhoAmIResponse();
        return whoAmIApiResponseModel != null &&  whoAmIApiResponseModel.getStatus() != null && whoAmIApiResponseModel.getStatus().equals(PROFILE_INCOMPLETE);
    }

    public static String getJoinedNotficationPushTime(String guid) {
        return appPreference.getHashString(PreferenceConstants.PATIENT_JOINED_WAITING_ROOM_NOTFICATION+guid);
    }

    public static void setJoinedNotficationPushTime(String date,String guid) {
        appPreference.setHashString(PreferenceConstants.PATIENT_JOINED_WAITING_ROOM_NOTFICATION+guid,date);
    }
}
