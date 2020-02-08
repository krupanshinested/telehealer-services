package Flavor.Utils;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.example.smartlinklib.MainActivity;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Util.Vital.BulkVitalUtil;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.home.monitoring.MonitoringFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import Flavor.GoogleFit.Activity.GoogleFitSourceSelectionActivity;
import Flavor.GoogleFit.GoogleFitDefaults;
import Flavor.GoogleFit.GoogleFitManager;
import Flavor.GoogleFit.Interface.GoogleFitResultFetcher;
import Flavor.GoogleFit.Models.GoogleFitData;
import Flavor.GoogleFit.Models.GoogleFitSource;

import static java.util.Arrays.*;

public class PatientApplication extends TeleHealerApplication implements GoogleFitResultFetcher {

    GoogleFitManager googleFitManager = new GoogleFitManager(this);

    @Override
    public void onMoveToForeground() {
        super.onMoveToForeground();
        checkGoogleFitData();
    }

    @Override
    public void onMoveToBackground() {
        super.onMoveToBackground();
    }

    private void checkGoogleFitData() {
        if (googleFitManager.isPermitted() && GoogleFitDefaults.getPreviousFetchedData() != null
                && appPreference.getBoolean(PreferenceConstants.IS_USER_LOGGED_IN)) {
            googleFitManager.read(this);
        }
    }

    //GoogleFitResultFetcher
    @Override
    public void didFinishFetch(ArrayList<GoogleFitData> fitData) {
        Log.d("PatientApplication","didFinishFetch");
        GoogleFitDefaults.setPreviousFetchedData(new Date());
        if (fitData.size() == 0) {
            return;
        }

        Log.d("PatientApplication","fetch size - "+fitData.size());
        ArrayList<GoogleFitSource> selectedSource = new ArrayList<>();
        ArrayList<String> selectedBundleIds = new ArrayList<>();

        for (GoogleFitSource source : GoogleFitDefaults.getPreviousFetchedSources()) {
            if (source.isSelected()) {
                selectedBundleIds.add(source.getBundleId());
                selectedSource.add(source);
            }
        }

        Intent val = googleFitManager.isChangeInSelectionSource(fitData,selectedBundleIds);
        if (val.getBooleanExtra(ArgumentKeys.GOOGLE_FIT_CHANGE,false)) {
            Intent intent = new Intent(getApplicationContext(),GoogleFitSourceSelectionActivity.class);
            intent.putExtra(ArgumentKeys.GOOGLE_FIT_DATA,fitData);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ArrayList<GoogleFitSource> newAddedSources = (ArrayList<GoogleFitSource>) val.getSerializableExtra(ArgumentKeys.GOOGLE_FIT_SOURCE);
            if (newAddedSources == null) {
                newAddedSources = new ArrayList<>();
            }
            selectedSource.addAll(newAddedSources);
            intent.putExtra(ArgumentKeys.GOOGLE_FIT_SOURCE,selectedSource);
            startActivity(intent);
            Log.d("PatientApplication","open fit source");
        } else {
            ArrayList<VitalsApiResponseModel> vitals = new ArrayList<>();
            for (GoogleFitData data : fitData) {

                DateFormat outputFormat = new SimpleDateFormat(Utils.UTCFormat);
                outputFormat.setTimeZone(Utils.UtcTimezone);
                String date = outputFormat.format(data.getDate());
                vitals.add(new VitalsApiResponseModel(data.getType(),data.getValue(),data.getSource().getAppName(), VitalsConstant.VITAL_MODE_PATIENT,date,data.getSource().getBundleId()));
            }

            Log.d("PatientApplication","vital api called");
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    BulkVitalUtil.getInstance().uploadAllVitals(1,vitals,null,null,null);
                }
            });
        }

    }

    @Override
    public void didFailedToFetch(Exception e) {
        Log.d("PatientApplication","Error while fetching google fit data "+e.getLocalizedMessage());
    }

    @Override
    public void removeShortCuts() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            shortcutManager.disableShortcuts(asList(MonitoringFragment.VITAL_OPEN_TYPE, MonitoringFragment.DIET_OPEN_TYPE));
        }
    }

    @Override
    public void addShortCuts() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

            Intent vitalIntent = new Intent(this, MainActivity.class);
            vitalIntent.setAction(Intent.ACTION_VIEW);
            vitalIntent.setClassName(application,"com.thealer.telehealer.views.home.HomeActivity");
            vitalIntent.setPackage("com.thealer");
            vitalIntent.putExtra(ArgumentKeys.OPEN_AUTOMATICALLY, MonitoringFragment.VITAL_OPEN_TYPE);
            ShortcutInfo vitalShortcut = new ShortcutInfo.Builder(application, MonitoringFragment.VITAL_OPEN_TYPE)
                    .setShortLabel(application.getString(R.string.vitals))
                    .setLongLabel(application.getString(R.string.vitals))
                    .setIcon(Icon.createWithResource(application, R.drawable.ic_vitals_app_tint))
                    .setIntent(vitalIntent)
                    .build();

            Intent dietIntent = new Intent(this, MainActivity.class);
            dietIntent.setAction(Intent.ACTION_VIEW);
            dietIntent.putExtra(ArgumentKeys.OPEN_AUTOMATICALLY, MonitoringFragment.DIET_OPEN_TYPE);
            dietIntent.setClassName(application,"com.thealer.telehealer.views.home.HomeActivity");
            dietIntent.setPackage("com.thealer");
            ShortcutInfo dietShortcut = new ShortcutInfo.Builder(application, MonitoringFragment.DIET_OPEN_TYPE)
                    .setShortLabel(application.getString(R.string.diet))
                    .setLongLabel(application.getString(R.string.diet))
                    .setIcon(Icon.createWithResource(application, R.drawable.ic_diet))
                    .setIntent(dietIntent)
                    .build();
            shortcutManager.setDynamicShortcuts(asList(vitalShortcut,dietShortcut));
        }
    }
}
