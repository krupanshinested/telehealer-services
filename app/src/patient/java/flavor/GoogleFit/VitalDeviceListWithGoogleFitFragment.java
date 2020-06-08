package flavor.GoogleFit;

import android.content.Intent;

import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.views.home.vitals.iHealth.pairing.VitalDeviceListFragment;

import flavor.GoogleFit.Activity.GoogleFitSourceSelectionActivity;

public class VitalDeviceListWithGoogleFitFragment extends VitalDeviceListFragment {

    private GoogleFitManager googleFitManager;

    @Override
    public void openGoogleFitSourceActivity() {
        if (PermissionChecker.with(getActivity()).checkPermissionForFragment(PermissionConstants.PERMISSION_GOOGLE_FIT, this)) {

            if (googleFitManager == null) {
                googleFitManager = new GoogleFitManager(this);
            }

            if (googleFitManager.isPermitted()) {
                openSourceActivity();
            } else {
                googleFitManager.requestPermission();
            }
        }
    }

    private void openSourceActivity() {
        Intent intent = new Intent(getActivity(), GoogleFitSourceSelectionActivity.class);
        intent.putExtra(ArgumentKeys.GOOGLE_FIT_SOURCE, GoogleFitDefaults.getPreviousFetchedSources());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GoogleFitManager.REQUEST_OAUTH_REQUEST_CODE:
                if (googleFitManager.isPermitted()) {
                    openSourceActivity();
                }
        }
    }
}
