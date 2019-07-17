package Flavor.iHealth;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.home.vitals.VitalsListFragment;

import java.util.ArrayList;
import java.util.Date;

import Flavor.GoogleFit.Activity.GoogleFitSourceSelectionActivity;
import Flavor.GoogleFit.GoogleFitDefaults;
import Flavor.GoogleFit.GoogleFitManager;
import Flavor.GoogleFit.Interface.GoogleFitResultFetcher;
import Flavor.GoogleFit.Models.GoogleFitData;
import Flavor.GoogleFit.Models.GoogleFitSource;

public class VitalsListWithGoogleFitFragment extends VitalsListFragment implements GoogleFitResultFetcher {
    GoogleFitManager googleFitManager;

    private boolean isAskedForPermission = false;

    protected void initView(View view) {
        super.initView(view);

        googleFitManager = new GoogleFitManager(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("VitalsListFitFragment", "account not already present");
        switch (requestCode) {
            case GoogleFitManager.REQUEST_OAUTH_REQUEST_CODE:
                Log.d("VitalsListFitFragment", "GoogleFitManager");
                googleFitManager.didFetchedPermissionResult();
        }
    }

    @Override
    protected void runAfterKnowYourNumber() {
        super.runAfterKnowYourNumber();

        if (!googleFitManager.isPermitted() && !isAskedForPermission) {
            this.isAskedForPermission = true;
            googleFitManager.read(this);
        }
    }

    //GoogleFitResultFetcher
    @Override
    public void didFinishFetch(ArrayList<GoogleFitData> fitData) {
        Log.d("PatientApplication", "didFinishFetch");
        GoogleFitDefaults.setPreviousFetchedData(new Date());

        if (fitData.size() == 0) {
            return;
        }

        ArrayList<GoogleFitSource> selectedSource = new ArrayList<>();
        ArrayList<String> selectedBundleIds = new ArrayList<>();

        for (GoogleFitSource source : GoogleFitDefaults.getPreviousFetchedSources()) {
            if (source.isSelected()) {
                selectedBundleIds.add(source.getBundleId());
                selectedSource.add(source);
            }
        }

        Intent val = googleFitManager.isChangeInSelectionSource(fitData, selectedBundleIds);

        Intent intent = new Intent(getActivity(), GoogleFitSourceSelectionActivity.class);
        intent.putExtra(ArgumentKeys.GOOGLE_FIT_DATA, fitData);
        ArrayList<GoogleFitSource> newAddedSources = (ArrayList<GoogleFitSource>) val.getSerializableExtra(ArgumentKeys.GOOGLE_FIT_SOURCE);
        if (newAddedSources == null) {
            newAddedSources = new ArrayList<>();
        }
        selectedSource.addAll(newAddedSources);
        intent.putExtra(ArgumentKeys.GOOGLE_FIT_SOURCE, selectedSource);
        startActivity(intent);
    }

    @Override
    public void didFailedToFetch(Exception e) {
        Log.d("PatientApplication", "Error while fetching google fit data " + e.getLocalizedMessage());
    }
}

