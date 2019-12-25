package config;

import android.content.Context;
import android.util.Log;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
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


    public AppConfig(Context context) {
        this.context = context;
    }

    public boolean isOtherThanTelehealer(Context context) {
        return !context.getString(R.string.organization_name).equals("Telehealer");
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
