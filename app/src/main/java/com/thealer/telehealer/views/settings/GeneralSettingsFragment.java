package com.thealer.telehealer.views.settings;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.ihealth.communication.utils.Log;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.settings.ProfileUpdate;
import com.thealer.telehealer.apilayer.models.userStatus.UpdateStatusApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.DateUtil;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.stripe.AppOAuthUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.call.CallNetworkTestActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginUtil;
import com.thealer.telehealer.views.settings.accessLogs.AccessLogActivity;
import com.thealer.telehealer.views.settings.cellView.ProfileCellView;
import com.thealer.telehealer.views.settings.cellView.SettingsCellView;

import java.util.Calendar;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.views.home.orders.forms.EditableFormFragment.getUtcFromDayMonthYear;

/**
 * Created by rsekar on 11/20/18.
 */

public class GeneralSettingsFragment extends BaseFragment implements View.OnClickListener {

    private SettingsCellView presence, quickLogin, secure_message, connection_request, appointment_request, order_request, integration_request, record_encounter, transcribe_encounter, enable_patient_card;
    private ProfileCellView signature, appointment_slots, available_time,rpmView;
    private LinearLayout deleteView, rpmLl, appointmentView, encounterView;

    private WhoAmIApiViewModel whoAmIApiViewModel;

    private OnActionCompleteInterface actionCompleteInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;

    private SettingsCellView notification;
    private WhoAmIApiResponseModel whoAmIApiResponseModel;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle, appointmentTv, encounterTv, rpmTv;
    private SettingsCellView privacy;
    private String selectedItem, startTime, endTime;
    private Boolean isSlotLoaded = false;

    private ProfileUpdate profileUpdate;
    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionCompleteInterface = (OnActionCompleteInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        profileUpdate = new ViewModelProvider(this).get(ProfileUpdate.class);
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        attachObserverInterface.attachObserver(whoAmIApiViewModel);
        profileUpdate.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                whoAmIApiViewModel.assignWhoAmI();
                manageSwitches();
            }
        });

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel responseModel) {
                if (responseModel instanceof WhoAmIApiResponseModel) {
                    whoAmIApiResponseModel = (WhoAmIApiResponseModel) responseModel;

                    if (!TextUtils.isEmpty(selectedItem)) {
                        appointment_slots.updateUI(false);
                        appointment_slots.updateValue(getAppointmentSlotValue(selectedItem));
                    }

                    if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
                        available_time.updateUI(false);
                        updateAvaibleTime(whoAmIApiResponseModel.getAppt_start_time(), whoAmIApiResponseModel.getAppt_end_time());
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_settings, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        notification.updateSwitch(isNotificationEnabled());

        updateQuickLoginSwitch();
        manageSwitches();

    }

    private void manageSwitches() {
        if (whoAmIApiResponseModel != null && whoAmIApiResponseModel.getStatus().equals(Constants.AVAILABLE))
            presence.updateSwitch(true);
        else
            presence.updateSwitch(false);

        if (whoAmIApiResponseModel != null) {
            Log.d("message ", "secure " + whoAmIApiResponseModel.isSecure_message());
            secure_message.updateSwitch(whoAmIApiResponseModel.isSecure_message());
        } else {
            secure_message.updateSwitch(false);
        }

        if (whoAmIApiResponseModel != null) {
            Log.d("message ", "order " + whoAmIApiResponseModel.getOrders_enabled());
            order_request.updateSwitch(whoAmIApiResponseModel.getOrders_enabled());
        } else {
            order_request.updateSwitch(false);
        }

        if (whoAmIApiResponseModel != null) {
            Log.d("message ", "record_encounter " + whoAmIApiResponseModel.getRecording_enabled());
            record_encounter.updateSwitch(whoAmIApiResponseModel.getRecording_enabled());
        } else {
            record_encounter.updateSwitch(false);
        }

        if (whoAmIApiResponseModel != null) {
            transcribe_encounter.updateSwitch(whoAmIApiResponseModel.getTranscription_enabled());
        } else {
            transcribe_encounter.updateSwitch(false);
        }

        if (whoAmIApiResponseModel != null) {
            integration_request.updateSwitch(false);
        } else {
            integration_request.updateSwitch(false);
        }

        if (whoAmIApiResponseModel != null) {
            Log.d("message ", "connection " + whoAmIApiResponseModel.getConnection_requests());
            connection_request.updateSwitch(whoAmIApiResponseModel.getConnection_requests());
        } else {
            connection_request.updateSwitch(false);
        }

        if (whoAmIApiResponseModel != null) {
            appointment_request.updateSwitch(whoAmIApiResponseModel.getAppt_requests());
        } else {
            appointment_request.updateSwitch(false);
        }

        if (whoAmIApiResponseModel != null) {
            enable_patient_card.updateSwitch(whoAmIApiResponseModel.isPatient_credit_card_required());
        } else {
            enable_patient_card.updateSwitch(false);
        }
    }

    private void initView(View view) {
        presence = view.findViewById(R.id.presence);
        quickLogin = view.findViewById(R.id.quick_login);
        signature = view.findViewById(R.id.signature);
        deleteView = view.findViewById(R.id.delete_view);
        rpmLl = view.findViewById(R.id.rpm_ll);
        rpmView = view.findViewById(R.id.rpm_view);
        appointmentView = view.findViewById(R.id.appointment_view);
        encounterView = view.findViewById(R.id.encounter_view);
        notification = (SettingsCellView) view.findViewById(R.id.notification);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        appointmentTv = (TextView) view.findViewById(R.id.appointment_text);
        encounterTv = (TextView) view.findViewById(R.id.encounter_text);
        rpmTv = (TextView) view.findViewById(R.id.rpm_hint);
        privacy = (SettingsCellView) view.findViewById(R.id.privacy);
        secure_message = view.findViewById(R.id.secure_message);
        connection_request = view.findViewById(R.id.connection_request);
        appointment_request = view.findViewById(R.id.appointment_request);
        order_request = view.findViewById(R.id.order_request);
        integration_request = view.findViewById(R.id.integration_request);
        appointment_slots = view.findViewById(R.id.appointment_slots);
        available_time = view.findViewById(R.id.available_time);
        record_encounter = view.findViewById(R.id.record_encounter);
        transcribe_encounter = view.findViewById(R.id.transcribe_encounter);
        enable_patient_card = view.findViewById(R.id.enable_patient_card);

        toolbarTitle.setText(getString(R.string.preference));
        setCurrentTimeOnView();
        addListenerOnButton();

       /* if (view.getId() == R.id.appointment_slots) {
            appointment_slots.openSpinner();
        }*/

        String[] titleList = getActivity().getResources().getStringArray(R.array.doctor_appointment_slots);
        ArrayAdapter titleAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, titleList);
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        isSlotLoaded = false;
        appointment_slots.updateAdapter(titleAdapter, new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isSlotLoaded) {
                    isSlotLoaded = true;
                } else {
                    selectedItem = parent.getItemAtPosition(position).toString();
                    profileUpdate.updateAppointmentSlot(getAppointmentSlotValue(selectedItem));
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        isSlotLoaded = false;
        notification.setOnClickListener(this);
        presence.setOnClickListener(this);
        quickLogin.setOnClickListener(this);
        signature.setOnClickListener(this);
        deleteView.setOnClickListener(this);
        backIv.setOnClickListener(this);
        privacy.setOnClickListener(this);
        secure_message.setOnClickListener(this);
        connection_request.setOnClickListener(this);
        appointment_request.setOnClickListener(this);
        appointment_slots.setOnClickListener(this);
        available_time.setOnClickListener(this);
        enable_patient_card.setOnClickListener(this);
        rpmView.setOnClickListener(this);

        order_request.setFocusableTitle();
        integration_request.setFocusableTitle();
        record_encounter.setFocusableTitle();
        transcribe_encounter.setFocusableTitle();

        switch (appPreference.getInt(Constants.USER_TYPE)) {
            case Constants.TYPE_PATIENT:
                signature.setVisibility(View.GONE);
                secure_message.setVisibility(View.GONE);
                connection_request.setVisibility(View.GONE);
                order_request.setVisibility(View.GONE);
                appointmentTv.setVisibility(View.GONE);
                encounterTv.setVisibility(View.GONE);
                rpmTv.setVisibility(View.GONE);
                integration_request.setVisibility(View.GONE);
                enable_patient_card.setVisibility(View.GONE);
                break;
            case Constants.TYPE_DOCTOR:
                rpmLl.setVisibility(View.VISIBLE);
                rpmTv.setVisibility(View.VISIBLE);
                appointmentView.setVisibility(View.VISIBLE);
                encounterView.setVisibility(View.VISIBLE);
                appointment_slots.updateValue(UserDetailPreferenceManager.getAppt_length() + "");
                if (UserDetailPreferenceManager.getAppt_start_time().equals("08:00 AM") && UserDetailPreferenceManager.getAppt_end_time().equals("09:30 PM")) {
                    startTime = DateUtil.getUTCfromLocal("08:00", "hh:mm", "hh:mm a");
                    endTime = DateUtil.getUTCfromLocal("21:30", "hh:mm", "hh:mm a");
                } else {
                    startTime = UserDetailPreferenceManager.getAppt_start_time();
                    endTime = UserDetailPreferenceManager.getAppt_end_time();
                }
                updateAvaibleTime(startTime, endTime);
                enable_patient_card.setVisibility(View.VISIBLE);
                break;
            case Constants.TYPE_MEDICAL_ASSISTANT:
                secure_message.setVisibility(View.GONE);
                signature.setVisibility(View.GONE);
                connection_request.setVisibility(View.GONE);
                order_request.setVisibility(View.GONE);
                appointmentTv.setVisibility(View.GONE);
                encounterTv.setVisibility(View.GONE);
                rpmTv.setVisibility(View.GONE);
                integration_request.setVisibility(View.GONE);
                enable_patient_card.setVisibility(View.GONE);
                break;
        }

        whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
    }

    private void addListenerOnButton() {
    }

    private void setCurrentTimeOnView() {
    }

    private void updateQuickLoginSwitch() {
        quickLogin.updateSwitch(QuickLoginUtil.isQuickLoginEnable(getActivity()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.notification:
                Utils.showAlertDialog(getActivity(), getString(R.string.settings), getString(R.string.notification_setting_alert_message),
                        getString(R.string.yes), getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
                                } else {
                                    intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                }
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
            case R.id.presence:
                UpdateStatusApiViewModel updateStatusApiViewModel = new ViewModelProvider(this).get(UpdateStatusApiViewModel.class);
                attachObserverInterface.attachObserver(updateStatusApiViewModel);

                updateStatusApiViewModel.updateStatus(!presence.getSwitchStatus(), true);

                updateStatusApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            presence.toggleSwitch();
                            if (presence.getSwitchStatus()) {
                                whoAmIApiResponseModel.setStatus(Constants.AVAILABLE);
                            } else {
                                whoAmIApiResponseModel.setStatus(Constants.OFFLINE);
                            }
                            UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                            updateStatusApiViewModel.baseApiResponseModelMutableLiveData.removeObservers(GeneralSettingsFragment.this);
                        }
                    }
                });

                break;
            case R.id.quick_login:
                if (quickLogin.getSwitchStatus()) {
                    appPreference.setInt(Constants.QUICK_LOGIN_TYPE, 0);
                    appPreference.setString(Constants.QUICK_LOGIN_PIN, null);
                    quickLogin.toggleSwitch();
                } else {
                    appPreference.setInt(Constants.QUICK_LOGIN_TYPE, QuickLoginUtil.getAvailableQuickLoginType(getActivity()));
                    appPreference.setString(Constants.QUICK_LOGIN_PIN, null);
                    startActivityForResult(new Intent(getActivity(), QuickLoginActivity.class).putExtra(ArgumentKeys.IS_CREATE_PIN, true), RequestID.REQ_CREATE_QUICK_LOGIN);
                }
                break;
            case R.id.signature:
                if (PermissionChecker.with(getActivity()).checkPermission(PermissionConstants.PERMISSION_STORAGE)) {
                    showSignatureView();
                }
                break;
            case R.id.appointment_slots:
                appointment_slots.openSpinner();
                break;
            case R.id.available_time:
                getAvailableStartTime();
                break;
            case R.id.connection_request:
                connection_request.toggleSwitch();
                profileUpdate.updateConnectionRequest(connection_request.getSwitchStatus());
                break;
            case R.id.appointment_request:
                appointment_request.toggleSwitch();
                profileUpdate.updateAppointmentRequest(appointment_request.getSwitchStatus());
                break;
            case R.id.delete_view:

                Utils.showAlertDialog(getActivity(), getString(R.string.delete_account),
                        getString(R.string.delete_account_description),
                        getString(R.string.Delete),
                        getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                EventRecorder.recordUserSession("account_deletion_initiated");

                                Intent intent = new Intent(getActivity(), DeleteAccountActivity.class);
                                getActivity().startActivity(intent);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                break;
            case R.id.privacy:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                break;
            case R.id.secure_message:
                String msg=String.format(getString(R.string.disclaimer_for_secure_messaging), getString(R.string.organization_name));
                secure_message.toggleSwitch();
                profileUpdate.updateSecureMessage(secure_message.getSwitchStatus(), true);
                if(secure_message.getSwitchStatus())
                    customToast(msg);
                break;
            case R.id.enable_patient_card:
                enable_patient_card.toggleSwitch();
                if (enable_patient_card.getSwitchStatus()) {
                    if (AppOAuthUtils.checkForOAuth(this, UserDetailPreferenceManager.getWhoAmIResponse().getPayment_account_info()))
                        profileUpdate.updatePatientCreditCard(enable_patient_card.getSwitchStatus(), true);
                } else {
                    profileUpdate.updatePatientCreditCard(enable_patient_card.getSwitchStatus(), true);
                }
                break;
            case R.id.rpm_view:
                showRemotePatientMonitoring();
            break;
        }
    }

    private void getAvailableStartTime() {
        Utils.showTimePickerDialog("Start time", getActivity(), startTime, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar1.set(Calendar.MINUTE, minute);
                startTime = DateUtil.getUTCfromLocal(hourOfDay + ":" + minute, "hh:mm", "hh:mm a");
                getAvailableEndTime();
            }
        });
    }

    private void getAvailableEndTime() {
        Utils.showTimePickerDialog("End time", getActivity(), endTime, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar2.set(Calendar.MINUTE, minute);
                endTime = DateUtil.getUTCfromLocal(hourOfDay + ":" + minute, "hh:mm", "hh:mm a");
                if (calendar2.getTimeInMillis() >= calendar1.getTimeInMillis()) {
                    postAvaibleTime();
                } else {
                    Snackbar snackbar = Snackbar.make(getView(), getString(R.string.appt_end_time_note), 3000).setBackgroundTint(getResources().getColor(R.color.app_gradient_start));
                    Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                    View snackbarView = getLayoutInflater().inflate(R.layout.view_snackbar, null);
                    TextView textView = snackbarView.findViewById(R.id.snackbar_tv);
                    textView.setText(getString(R.string.appt_end_time_note));
                    snackbarLayout.addView(snackbarView);
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            getAvailableEndTime();
                        }
                    }).show();
                }
            }
        });
    }

    private void postAvaibleTime() {
        updateAvaibleTime(startTime, endTime);
        profileUpdate.updateAvailableTime(startTime, endTime);
    }

    private void showRemotePatientMonitoring() {
        RemotePatientMonitoringFragment remotePatientMonitoringFragment = new RemotePatientMonitoringFragment();
        showSubFragmentInterface.onShowFragment(remotePatientMonitoringFragment);
    }
    private void showSignatureView() {
        SignatureViewFragment signatureViewFragment = new SignatureViewFragment();
        showSubFragmentInterface.onShowFragment(signatureViewFragment);
    }

    private void updateAvaibleTime(String startTime, String endTime) {
        String start = DateUtil.getLocalfromUTC(startTime, "hh:mm a", "hh:mm a");
        String end = DateUtil.getLocalfromUTC(endTime, "hh:mm a", "hh:mm a");
        available_time.updateValue(start + "  -  " + end);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PermissionConstants.PERMISSION_STORAGE:
                if (resultCode == Activity.RESULT_OK) {
                    showSignatureView();
                }
                break;
            case RequestID.REQ_CREATE_QUICK_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    updateQuickLoginSwitch();
                }
                break;
            case RequestID.REQ_OAUTH: {
                if (resultCode == Activity.RESULT_OK) {
                    profileUpdate.updatePatientCreditCard(true, true);
                } else {
                    enable_patient_card.toggleSwitch();
                    profileUpdate.updatePatientCreditCard(enable_patient_card.getSwitchStatus(), true);
                }
            }
        }
    }

    private boolean isNotificationEnabled() {
        return NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();
    }

    private String getAppointmentSlotValue(String selectedItem) {
        String[] value = selectedItem.split("\\s", 2);
        return value[0];
    }

}
