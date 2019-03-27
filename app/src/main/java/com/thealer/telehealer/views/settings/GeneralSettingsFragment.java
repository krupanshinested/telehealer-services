package com.thealer.telehealer.views.settings;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.userStatus.UpdateStatusApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.call.CallNetworkTestActivity;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.quickLogin.QuickLoginUtil;
import com.thealer.telehealer.views.settings.cellView.ProfileCellView;
import com.thealer.telehealer.views.settings.cellView.SettingsCellView;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 11/20/18.
 */

public class GeneralSettingsFragment extends BaseFragment implements View.OnClickListener {

    private SettingsCellView checkCallQuality, presence, quickLogin;
    private ProfileCellView signature;
    private LinearLayout deleteView;

    private OnActionCompleteInterface actionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AttachObserverInterface attachObserverInterface;

    private SettingsCellView notification;
    private WhoAmIApiResponseModel whoAmIApiResponseModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
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
        onViewChangeInterface.hideOrShowNext(false);

        onViewChangeInterface.updateTitle(getString(R.string.settings));

        notification.updateSwitch(isNotificationEnabled());
    }
    private void initView(View view) {
        checkCallQuality = view.findViewById(R.id.check_call_quality);
        presence = view.findViewById(R.id.presence);
        quickLogin = view.findViewById(R.id.quick_login);
        signature = view.findViewById(R.id.signature);
        deleteView = view.findViewById(R.id.delete_view);
        notification = (SettingsCellView) view.findViewById(R.id.notification);

        checkCallQuality.setOnClickListener(this);
        notification.setOnClickListener(this);
        presence.setOnClickListener(this);
        quickLogin.setOnClickListener(this);
        signature.setOnClickListener(this);
        deleteView.setOnClickListener(this);

        quickLogin.updateSwitch(QuickLoginUtil.isQuickLoginEnable(getActivity()));

        switch (appPreference.getInt(Constants.USER_TYPE)) {
            case Constants.TYPE_PATIENT:
                signature.setVisibility(View.GONE);
                break;
            case Constants.TYPE_DOCTOR:
                break;
            case Constants.TYPE_MEDICAL_ASSISTANT:
                signature.setVisibility(View.GONE);
                break;
        }

        whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();

        if (whoAmIApiResponseModel.getStatus().equals(Constants.AVAILABLE))
            presence.updateSwitch(true);
        else
            presence.updateSwitch(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_call_quality:
                Intent intent = new Intent(getActivity(), CallNetworkTestActivity.class);
                startActivity(intent);
                break;
            case R.id.notification:
                Utils.showAlertDialog(getActivity(), getString(R.string.settings), getString(R.string.notification_setting_alert_message),
                        getString(R.string.yes), getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
                UpdateStatusApiViewModel updateStatusApiViewModel = ViewModelProviders.of(this).get(UpdateStatusApiViewModel.class);
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
                    appPreference.setInt(Constants.QUICK_LOGIN_TYPE, Constants.QUICK_LOGIN_TYPE_NONE);
                    quickLogin.toggleSwitch();
                } else {
                    if (QuickLoginUtil.getAvailableQuickLoginType(getActivity()) != Constants.QUICK_LOGIN_TYPE_PIN) {
                        quickLogin.toggleSwitch();
                        appPreference.setInt(Constants.QUICK_LOGIN_TYPE, Constants.QUICK_LOGIN_TYPE_PIN);
                    } else {
                        appPreference.setString(Constants.QUICK_LOGIN_PIN, "");
                        actionCompleteInterface.onCompletionResult(RequestID.REQ_QUICK_LOGIN_PIN, true, null);
                    }
                }
                break;
            case R.id.signature:
                if (PermissionChecker.with(getActivity()).checkPermission(PermissionConstants.PERMISSION_STORAGE)) {
                    showSignatureView();
                }
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
        }
    }

    private void showSignatureView() {
        SignatureViewFragment signatureViewFragment = new SignatureViewFragment();
        showSubFragmentInterface.onShowFragment(signatureViewFragment);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionConstants.PERMISSION_STORAGE && resultCode == Activity.RESULT_OK) {
            showSignatureView();
        }
    }

    private boolean isNotificationEnabled() {
        return NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();
    }
}
