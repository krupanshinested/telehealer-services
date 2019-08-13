package Flavor.GoogleFit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.home.vitals.VitalsListFragment;

import java.util.ArrayList;
import java.util.Date;

import Flavor.GoogleFit.Activity.GoogleFitSourceSelectionActivity;
import Flavor.GoogleFit.Interface.GoogleFitResultFetcher;
import Flavor.GoogleFit.Models.GoogleFitData;
import Flavor.GoogleFit.Models.GoogleFitSource;

public class VitalsListWithGoogleFitFragment extends VitalsListFragment implements GoogleFitResultFetcher {
    GoogleFitManager googleFitManager;

    private boolean isAskedForPermission = false;
    private ArrayList<GoogleFitData> fitData;

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
                if (googleFitManager.isPermitted()) {

                    SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.health_source_success));
                    bundle.putBoolean(Constants.SUCCESS_VIEW_DONE_BUTTON, true);
                    successViewDialogFragment.setArguments(bundle);
                    successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
                    successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

                    googleFitManager.read(this);
                }
                break;
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (Activity.RESULT_OK != resultCode) {
                    return;
                }

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
        this.fitData = fitData;
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, "");
        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.health_source_fetching));
        bundle.putBoolean(Constants.SUCCESS_VIEW_AUTO_DISMISS, true);
        bundle.putBoolean(Constants.SUCCESS_VIEW_DONE_BUTTON, true);

        LocalBroadcastManager
                .getInstance(getActivity())
                .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                        .putExtras(bundle));
    }

    @Override
    public void didFailedToFetch(Exception e) {
        Log.d("PatientApplication", "Error while fetching google fit data " + e.getLocalizedMessage());
    }
}

