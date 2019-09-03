package Flavor.Utils;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.Util.Vital.BulkVitalUtil;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Flavor.GoogleFit.Activity.GoogleFitSourceSelectionActivity;
import Flavor.GoogleFit.GoogleFitDefaults;
import Flavor.GoogleFit.GoogleFitManager;
import Flavor.GoogleFit.Interface.GoogleFitResultFetcher;
import Flavor.GoogleFit.Models.GoogleFitData;
import Flavor.GoogleFit.Models.GoogleFitSource;

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
}
