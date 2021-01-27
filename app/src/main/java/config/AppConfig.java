package config;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Aswin on 24,June,2019
 */
public class AppConfig {
    private Context context;
    public static final int FEATURE_DOCTOR_SEARCH = 1;
    public static final int FEATURE_PHARMACY_FAX = 2;
    public static final int FEATURE_NPI = 3;
    public static final int FEATURE_LICENSE = 4;
    public static final int FEATURE_CCM = 4;
    public static final int FEATURE_SPECIALIST = 5;
    public static final int FEATURE_STATE_VALIDATION = 6;
    public static final int FEATURE_ZIP_VALIDATION = 7;
    public static final int FEATURE_PAYMENT = 8;
    public static final int FEATURE_STETHOSCOPE = 9;

    public static final String TELEHEALER_PARENT = "telehealer";
    public static final String PEPTALK_PARENT = "peptalk";

    public AppConfig(Context context) {
        this.context = context;
    }

    public boolean isOtherThanTelehealer(Context context) {
        return !BuildConfig.PARENT_APP.equals(TELEHEALER_PARENT);
    }

    public boolean isLocaleIndia() {
        return getLocaleCountry().equalsIgnoreCase("IN");
    }

    public boolean isIndianUser(Context context) {
        return UserDetailPreferenceManager.getInstallType().equals(context.getString(R.string.install_type_india));
    }

    public String getLocaleCountry() {
        Locale locale = Locale.getDefault();
        return locale.getCountry();
    }

    public String getInstallType(String country) {
        if (country == null) {
            return null;
        }
        String type;
        if (country.equalsIgnoreCase("india")) {
            type = context.getString(R.string.install_type_india);
        } else {
            type = context.getString(R.string.install_type);
        }
        return type;
    }

    @Nullable
    public String getVitalPemFileName() {
        String fileName = null;
        if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_PATIENT)) {
            fileName = "com_thealer_android.pem";
        } else {
            fileName = "com_thealer_pro_android_med.pem";
        }
        return fileName;
    }

    public ArrayList<String> getOtherParentBundleIds() {
        ArrayList<String> bundleIds = new ArrayList<>();
        if (BuildConfig.PARENT_APP.equals(PEPTALK_PARENT)) {
            if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_PATIENT)) {
                bundleIds.add("com.peptalkhealth.doctor");
            } else {
                bundleIds.add("com.peptalkhealth.patient");
            }
        } else {
            if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_PATIENT)) {
                bundleIds.add("com.thealer.pro");
            } else {
                bundleIds.add("com.thealer");
            }
        }
        return bundleIds;
    }

    public String getVoipChannel() {
        if (BuildConfig.PARENT_APP.equals(PEPTALK_PARENT)) {
            return "peptalk-call-voip";
        } else {
            return "thealer-call-voip";
        }
    }

    public String getCallChannel() {
        if (BuildConfig.PARENT_APP.equals(PEPTALK_PARENT)) {
            return "peptalk-call";
        } else {
            return "thealer-call";
        }
    }

    public String getApnsChannel() {
        if (BuildConfig.PARENT_APP.equals(PEPTALK_PARENT)) {
            return "peptalk";
        } else {
            return "thealer";
        }
    }

    public String getAppPreference() {
        if (BuildConfig.PARENT_APP.equals(PEPTALK_PARENT)) {
            return "peptalk";
        } else {
            return "thealer";
        }
    }


    public String getInstallType() {
        if (isOtherThanTelehealer(context)) {
            return "peptalk";
        } else {
            String type = UserDetailPreferenceManager.getInstallType();

            if (type != null && !type.isEmpty()) {
                return type;
            } else {
                WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
                if (whoAmIApiResponseModel != null && whoAmIApiResponseModel.getInstall_type() != null && !whoAmIApiResponseModel.getInstall_type().isEmpty()) {
                    return whoAmIApiResponseModel.getInstall_type();
                } else {
                    if (isLocaleIndia()) {
                        type = context.getString(R.string.install_type_india);
                    } else {
                        type = context.getString(R.string.install_type);
                    }
                    UserDetailPreferenceManager.setInstallType(type);
                    UserDetailPreferenceManager.setCountryCode(getLocaleCountry());
                    return type;
                }
            }
        }
    }

    public List<Integer> getRemovedFeatures() {
        List<Integer> removedList;
        if (isOtherThanTelehealer(context)) {
            removedList = new ArrayList<>();
        } else if (UserDetailPreferenceManager.getInstallType().equals(context.getString(R.string.install_type_india))) {
            removedList = new ArrayList<>(Arrays.asList(FEATURE_DOCTOR_SEARCH, FEATURE_PHARMACY_FAX, FEATURE_LICENSE, FEATURE_NPI, FEATURE_CCM, FEATURE_SPECIALIST,
                    FEATURE_STATE_VALIDATION, FEATURE_ZIP_VALIDATION, FEATURE_PAYMENT, FEATURE_STETHOSCOPE));
        } else {
            removedList = new ArrayList<>(Arrays.asList(FEATURE_STETHOSCOPE));
        }
        return removedList;
    }
}
