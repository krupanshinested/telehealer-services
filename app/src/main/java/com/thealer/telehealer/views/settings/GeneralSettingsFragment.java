package com.thealer.telehealer.views.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginUtil;
import com.thealer.telehealer.views.settings.cellView.ProfileCellView;
import com.thealer.telehealer.views.settings.cellView.SettingsCellView;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.common.Constants.QUICK_LOGIN_TYPE_NONE;

/**
 * Created by rsekar on 11/20/18.
 */

public class GeneralSettingsFragment extends BaseFragment implements View.OnClickListener {

    private SettingsCellView checkCallQuality,presence,quickLogin;
    private ProfileCellView signature;
    private LinearLayout deleteView;

    private OnActionCompleteInterface actionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_settings, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        onViewChangeInterface.hideOrShowNext(false);

        onViewChangeInterface.updateTitle(getString(R.string.settings));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_call_quality:
                break;
            case R.id.presence:
                presence.toggleSwitch();
                break;
            case R.id.quick_login:
               if (quickLogin.getSwitchStatus()) {
                   appPreference.setInt(Constants.QUICK_LOGIN_TYPE,Constants.QUICK_LOGIN_TYPE_NONE);
                   quickLogin.toggleSwitch();
               } else {
                   if (QuickLoginUtil.getAvailableQuickLoginType(getActivity()) != Constants.QUICK_LOGIN_TYPE_PIN) {
                       quickLogin.toggleSwitch();
                       appPreference.setInt(Constants.QUICK_LOGIN_TYPE,Constants.QUICK_LOGIN_TYPE_PIN);
                   } else {
                       appPreference.setString(Constants.QUICK_LOGIN_PIN,"");
                       actionCompleteInterface.onCompletionResult(RequestID.REQ_QUICK_LOGIN_PIN,true,null);
                   }
               }
                break;
            case R.id.signature:
                break;
            case R.id.delete_view:

                showAlertDialog(getActivity(), getString(R.string.delete_account),
                        getString(R.string.delete_account_description),
                        getString(R.string.delete),
                        getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Intent intent = new Intent(getActivity(),DeleteAccountActivity.class);
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

    private void initView(View view) {
        checkCallQuality = view.findViewById(R.id.check_call_quality);
        presence = view.findViewById(R.id.presence);
        quickLogin = view.findViewById(R.id.quick_login);
        signature = view.findViewById(R.id.signature);
        deleteView = view.findViewById(R.id.delete_view);

        checkCallQuality.setOnClickListener(this);
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
    }

}
