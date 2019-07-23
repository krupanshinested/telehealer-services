package com.thealer.telehealer.views.home.vitals;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.VitalsOrdersListAdapter;

import com.thealer.telehealer.views.home.vitals.iHealth.pairing.VitalCreationActivity;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.isContentViewProceed;
import static com.thealer.telehealer.TeleHealerApplication.isInForeGround;
import static com.thealer.telehealer.TeleHealerApplication.isVitalDeviceConnectionShown;

/**
 * Created by Aswin on 21,November,2018
 */
public class VitalsListFragment extends BaseFragment {
    private RecyclerView listRv;
    private FloatingActionButton fab;
    private View view;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private OnCloseActionInterface onCloseActionInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vitals_orders_list, container, false);
        initView(view);
        return view;
    }

    protected void initView(View view) {
        fab = view.findViewById(R.id.add_fab);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

        if (UserType.isUserPatient()) {
            if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_VITALS)) {
                appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_VITALS, true);
                Utils.showOverlay(getActivity(), fab, OverlayViewConstants.OVERLAY_NO_VITALS, null);
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserType.isUserPatient()) {

                        if (TokBox.shared.isActiveCallPreset()) {
                            Toast.makeText(getActivity(), getString(R.string.live_call_going_error), Toast.LENGTH_LONG).show();
                            return;
                        }

                        Intent intent = new Intent(getActivity(), VitalCreationActivity.class);
                        startActivityForResult(intent, RequestID.REQ_CREATE_NEW_VITAL);
                    }
                }
            });
        } else {
            fab.hide();
        }

        listRv = (RecyclerView) view.findViewById(R.id.vitals_orders_list_rv);
        listRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (getArguments() != null) {
            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR)) {
                appbarLayout.setVisibility(View.VISIBLE);
                toolbarTitle.setText(getString(R.string.vitals));
                onCloseActionInterface = (OnCloseActionInterface) getActivity();
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCloseActionInterface.onClose(false);
                    }
                });
            } else {
                appbarLayout.setVisibility(View.GONE);
            }
        }
            VitalsOrdersListAdapter vitalsOrdersListAdapter = new VitalsOrdersListAdapter(getActivity(), SupportedMeasurementType.getItems(), Constants.VIEW_VITALS, getArguments());
            listRv.setAdapter(vitalsOrdersListAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (UserType.isUserPatient() && !isContentViewProceed && appPreference.getInt(PreferenceConstants.DEVICE_CONNECTION_COUNT) < 3) {
            boolean result = checkVitalDeviceConnection();
            if (!result) {
                runAfterKnowYourNumber();
            }
        } else {
            runAfterKnowYourNumber();
        }

        if (isContentViewProceed && isInForeGround) {
            isContentViewProceed = false;
        }
    }

    private boolean checkVitalDeviceConnection() {
        if (!appPreference.getBoolean(PreferenceConstants.IS_VITAL_DEVICE_CONNECTED) &&
                !isVitalDeviceConnectionShown) {
            isVitalDeviceConnectionShown = true;
            showKnowYourNumber();
            return true;
        } else {
            return false;
        }
    }

    private void showKnowYourNumber() {

        int count = appPreference.getInt(PreferenceConstants.DEVICE_CONNECTION_COUNT);

        if (count < 1) {
            count = 1;
        } else {
            count = count + 1;
        }

        appPreference.setInt(PreferenceConstants.DEVICE_CONNECTION_COUNT, count);

        startActivityForResult(new Intent(getActivity(), ContentActivity.class)
                        .putExtra(ArgumentKeys.TITLE, getString(R.string.know_your_numbers))
                        .putExtra(ArgumentKeys.DESCRIPTION, getString(R.string.know_your_number_description))
                        .putExtra(ArgumentKeys.IS_SKIP_NEEDED, true)
                        .putExtra(ArgumentKeys.IS_BUTTON_NEEDED, true)
                        .putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.proceed))
                        .putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.ic_health_heart)
                , RequestID.REQ_CONNECT_VITAL_CONTENT_VIEW);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideOverlay();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestID.REQ_CONNECT_VITAL_CONTENT_VIEW:
                if (resultCode == Activity.RESULT_OK) {
                    isContentViewProceed = true;
                    isInForeGround = false;
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.vital_devices_url))));
                }
                break;
            case RequestID.REQ_CREATE_NEW_VITAL:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.getStringExtra(ArgumentKeys.MEASUREMENT_TYPE) != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ArgumentKeys.MEASUREMENT_TYPE, data.getStringExtra(ArgumentKeys.MEASUREMENT_TYPE));
                        bundle.putBoolean(Constants.IS_FROM_HOME, true);
                        Fragment fragment = new VitalsDetailListFragment();
                        fragment.setArguments(bundle);
                        if (getActivity() instanceof ShowSubFragmentInterface)
                            ((ShowSubFragmentInterface) getActivity()).onShowFragment(fragment);
                    }
                }
                break;
        }

    }

    protected void runAfterKnowYourNumber() {

    }
}
