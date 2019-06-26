package Flavor.GoogleFit;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import Flavor.GoogleFit.Models.GoogleFitSource;
import androidx.annotation.Nullable;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

public class GoogleFitDefaults {

    @Nullable
    public static Date getPreviousFetchedData() {
        String dateString = TeleHealerApplication.appPreference.getString(PreferenceConstants.GOOGLE_FIT_PRE_FETCHED_DATE);
        if (TextUtils.isEmpty(dateString)) {
            return null;
        } else {
            return Utils.getDateFromString(dateString,Utils.UTCFormat);
        }
    }

    public static void setPreviousFetchedData(Date fetchedData) {
        TeleHealerApplication.appPreference.setString(PreferenceConstants.GOOGLE_FIT_PRE_FETCHED_DATE, Utils.getStringFromDate(fetchedData,Utils.UTCFormat));
    }

    public static ArrayList<GoogleFitSource> getPreviousFetchedSources() {
        String prefetchedSources = TeleHealerApplication.appPreference.getString(PreferenceConstants.GOOGLE_FIT_PRE_FETCHED_SOURCES);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<GoogleFitSource>>() {}.getType();
        ArrayList<GoogleFitSource> sources = gson.fromJson(prefetchedSources, type);
        if (sources != null) {
            return sources;
        } else {
            return new ArrayList<>();
        }
    }

    public static void setPreviousFetchedSources(ArrayList<GoogleFitSource> sources) {
        Gson gson = new Gson();
        String source = gson.toJson(sources);
        appPreference.setString(PreferenceConstants.GOOGLE_FIT_PRE_FETCHED_SOURCES, source);
    }

}
