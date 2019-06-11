package com.thealer.telehealer.views.settings;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.userStatus.UpdateStatusApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.call.CallNetworkTestActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginUtil;
import com.thealer.telehealer.views.settings.cellView.ProfileCellView;
import com.thealer.telehealer.views.settings.cellView.SettingsCellView;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 11/20/18.
 */

public class GeneralSettingsFragment extends BaseFragment implements View.OnClickListener {

    private SettingsCellView checkCallQuality, presence, quickLogin;
    private ProfileCellView signature;
    private LinearLayout deleteView;

    private OnActionCompleteInterface actionCompleteInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;

    private SettingsCellView notification;
    private WhoAmIApiResponseModel whoAmIApiResponseModel;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private SettingsCellView privacy;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionCompleteInterface = (OnActionCompleteInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
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
    }

    private void initView(View view) {
        checkCallQuality = view.findViewById(R.id.check_call_quality);
        presence = view.findViewById(R.id.presence);
        quickLogin = view.findViewById(R.id.quick_login);
        signature = view.findViewById(R.id.signature);
        deleteView = view.findViewById(R.id.delete_view);
        notification = (SettingsCellView) view.findViewById(R.id.notification);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        privacy = (SettingsCellView) view.findViewById(R.id.privacy);

        toolbarTitle.setText(getString(R.string.settings));

        checkCallQuality.setOnClickListener(this);
        notification.setOnClickListener(this);
        presence.setOnClickListener(this);
        quickLogin.setOnClickListener(this);
        signature.setOnClickListener(this);
        deleteView.setOnClickListener(this);
        backIv.setOnClickListener(this);
        privacy.setOnClickListener(this);

        updateQuickLoginSwitch();

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

    private void updateQuickLoginSwitch() {
        quickLogin.updateSwitch(QuickLoginUtil.isQuickLoginEnable(getActivity()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
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
                intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

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
        }
    }

    private boolean isNotificationEnabled() {
        return NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();
    }
}
