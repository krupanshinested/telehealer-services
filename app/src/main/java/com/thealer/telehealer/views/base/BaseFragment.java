package com.thealer.telehealer.views.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CallPopupDialogFragment;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.popUpSchedulesId;

/**
 * Created by Aswin on 10,October,2018
 */
public class BaseFragment extends Fragment {

    public static final String TAG = "aswin";
    public Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onStop() {
        closeDailogs();
        super.onStop();
    }

    private void closeDailogs() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public boolean isDeviceXLarge() {
        return getActivity().getResources().getBoolean(R.bool.isXlarge);
    }

    public boolean isModeLandscape() {
        return getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public boolean isSplitModeNeeded() {
        return isDeviceXLarge() && isModeLandscape();
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public Bitmap getBitmpaFromPath(String profileImgPath) {

        return BitmapFactory.decodeFile(profileImgPath);
    }

    public void showOrHideSoftInputWindow(boolean showOrHide) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (showOrHide) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_SHOWN);
        } else {
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, InputMethodManager.RESULT_HIDDEN);
        }
    }

    public void showSuccessView(Fragment fragment, int requestId, Bundle bundle) {
        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        if (bundle != null) {
            successViewDialogFragment.setArguments(bundle);
        }
        if (fragment != null) {
            successViewDialogFragment.setTargetFragment(fragment, requestId);
        }
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
    }

    public void sendSuccessViewBroadCast(Context context, boolean status, String title, String description) {

        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, status);
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, title);
        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, description);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public void showSnack(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = snackbarLayout.findViewById(R.id.snackbar_text);
        textView.setMaxLines(10);
        snackbar.setDuration(BaseTransientBottomBar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            switch (requestCode) {
                case PermissionConstants.PERMISSION_CAM_MIC:
                    EventRecorder.recordPermissionDenined("cam_mic_disabled");
                    break;
                case PermissionConstants.PERMISSION_MICROPHONE:
                    EventRecorder.recordPermissionDenined("mic_disabled");
                    break;
                case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
                    EventRecorder.recordPermissionDenined("cam_mic_notification_disabled");
                    break;
                case PermissionConstants.PERMISSION_CAM_PHOTOS:
                    EventRecorder.recordPermissionDenined("cam_photos_disabled");
                    break;
                case PermissionConstants.PERMISSION_CAMERA:
                    EventRecorder.recordPermissionDenined("cam_disabled");
                    break;
                case PermissionConstants.PERMISSION_GALLERY:
                    EventRecorder.recordPermissionDenined("photos_disabled");
                    break;
                case PermissionConstants.PERMISSION_LOCATION:
                    EventRecorder.recordPermissionDenined("location_disabled");
                    break;
                case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                    EventRecorder.recordPermissionDenined("location_photos_disabled");
                    break;
                case PermissionConstants.PERMISSION_LOCATION_VITALS:
                    EventRecorder.recordPermissionDenined("location_disabled");
                    break;
                case PermissionConstants.PERMISSION_MIC_NITIFICATION:
                    EventRecorder.recordPermissionDenined("mic_notification_disabled");
                    break;
                case PermissionConstants.PERMISSION_NOTIFICATION:
                    EventRecorder.recordPermissionDenined("notification_disabled");
                    break;
                case PermissionConstants.PERMISSION_STORAGE:
                    EventRecorder.recordPermissionDenined("photos_disabled");
                    break;
                case PermissionConstants.PERMISSION_WRITE_STORAGE_VITALS:
                    EventRecorder.recordPermissionDenined("photos_disabled");
                    break;
            }
        }
    }

    public void checkOnGoingCall() {
        if (!UserType.isUserPatient()) {

            String upcomingList = appPreference.getString(PreferenceConstants.UPCOMING_SCHEDULES);

            if (upcomingList != null && !upcomingList.isEmpty()) {
                List<SchedulesApiResponseModel.ResultBean> upcomingSchedulesList = new Gson().fromJson(upcomingList, new TypeToken<List<SchedulesApiResponseModel.ResultBean>>() {
                }.getType());

                if (!upcomingSchedulesList.isEmpty()) {
                    for (int i = 0; i < upcomingSchedulesList.size(); i++) {
                        if (Utils.isDateTimeExpired(upcomingSchedulesList.get(i).getStart()) &&
                                !Utils.isDateTimeExpired(upcomingSchedulesList.get(i).getEnd())) {

                            if (!popUpSchedulesId.contains(upcomingSchedulesList.get(i).getSchedule_id())) {

                                popUpSchedulesId.add(upcomingSchedulesList.get(i).getSchedule_id());

                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ArgumentKeys.SCHEDULE_DETAIL, upcomingSchedulesList.get(i));

                                CallPopupDialogFragment callPopupDialogFragment = new CallPopupDialogFragment();
                                callPopupDialogFragment.setArguments(bundle);
                                callPopupDialogFragment.show(getChildFragmentManager(), callPopupDialogFragment.getClass().getSimpleName());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkOnGoingCall();
    }
}
