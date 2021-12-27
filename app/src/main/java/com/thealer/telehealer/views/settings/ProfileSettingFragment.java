package com.thealer.telehealer.views.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.settings.ProfileUpdate;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Config;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.settings.Interface.SettingClickListener;
import com.thealer.telehealer.views.settings.cellView.ProfileCellView;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 11/15/18.
 */

public class ProfileSettingFragment extends BaseFragment implements View.OnClickListener {

    private SettingClickListener settingClickListener;
    private OnViewChangeInterface onViewChangeInterface;

    private ProfileCellView profile, medical_history, settings, email_id,
            phone_number, change_password, checkCallQuality, logs,
            feedback, terms_and_condition, privacy_policy, add_card,telehealer_billings, educational_video, patient_payments,subscription,newDeviceSetup;

    private View signOut;

    private ProfileUpdate profileUpdate;
    private LinearLayout medicalAssistantLl, billLl;
    private ProfileCellView medicalAssistant;
    private ProfileCellView documents;
    private TextView versionTv;
    private TextView lastLoginTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        settingClickListener = (SettingClickListener) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        onViewChangeInterface.hideOrShowNext(false);
        onViewChangeInterface.hideOrShowOtherOption(false);

        if (getActivity() instanceof ProfileSettingsActivity) {
            ((ProfileSettingsActivity) getActivity()).updateProfile();
        }

        onViewChangeInterface.updateTitle(UserDetailPreferenceManager.getUserDisplayName());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signOut && CallManager.shared.isActiveCallPresent()) {
            Toast.makeText(getActivity(), getString(R.string.live_call_going_error), Toast.LENGTH_LONG).show();
            return;
        }
        settingClickListener.didSelecteItem(view.getId());
    }

    private void initView(View baseView) {
        checkCallQuality = baseView.findViewById(R.id.check_call_quality);
        logs = baseView.findViewById(R.id.logs);
        profile = baseView.findViewById(R.id.profile);
        documents = (ProfileCellView) baseView.findViewById(R.id.documents);
        medical_history = baseView.findViewById(R.id.medical_history);
        settings = baseView.findViewById(R.id.settings);
        email_id = baseView.findViewById(R.id.email_id);
        phone_number = baseView.findViewById(R.id.phone_number);
        change_password = baseView.findViewById(R.id.change_password);
        feedback = baseView.findViewById(R.id.feedback);
        terms_and_condition = baseView.findViewById(R.id.terms_and_condition);
        privacy_policy = baseView.findViewById(R.id.privacy_policy);
        signOut = baseView.findViewById(R.id.signOut);
        add_card = baseView.findViewById(R.id.add_card);
        telehealer_billings = baseView.findViewById(R.id.telehealer_billings);
        subscription = baseView.findViewById(R.id.subscription);
        patient_payments = baseView.findViewById(R.id.patient_payments);
        medicalAssistantLl = (LinearLayout) baseView.findViewById(R.id.medical_assistant_ll);
        billLl = (LinearLayout) baseView.findViewById(R.id.bill_view);
        medicalAssistant = (ProfileCellView) baseView.findViewById(R.id.medical_assistant);
        educational_video = baseView.findViewById(R.id.educational_video);
        versionTv = (TextView) baseView.findViewById(R.id.version_tv);
        newDeviceSetup = (ProfileCellView) baseView.findViewById(R.id.new_device_setup);
        lastLoginTv = (TextView) baseView.findViewById(R.id.last_login_tv);

        lastLoginTv.setText(getString(R.string.last_login, appPreference.getString(PreferenceConstants.LAST_LOGIN)));

        email_id.hideOrShowRightArrow(false);
        phone_number.hideOrShowRightArrow(false);

        try {
            String versionString = getString(R.string.version) + " " + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            if (Config.isDev()) {
                versionString += " DEV";
            }

            versionTv.setText(versionString);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        profileUpdate = new ViewModelProvider(getActivity()).get(ProfileUpdate.class);

        onViewChangeInterface.attachObserver(profileUpdate);

        profileUpdate.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
                    UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                }
            }
        });

        educational_video.setOnClickListener(this);
        checkCallQuality.setOnClickListener(this);
        logs.setOnClickListener(this);
        profile.setOnClickListener(this);
        documents.setOnClickListener(this);
        medical_history.setOnClickListener(this);
        settings.setOnClickListener(this);
        email_id.setOnClickListener(this);
        phone_number.setOnClickListener(this);
        change_password.setOnClickListener(this);
        feedback.setOnClickListener(this);
        terms_and_condition.setOnClickListener(this);
        privacy_policy.setOnClickListener(this);
        signOut.setOnClickListener(this);
        add_card.setOnClickListener(this);
        telehealer_billings.setOnClickListener(this);
        subscription.setOnClickListener(this);
        medicalAssistantLl.setOnClickListener(this);
        patient_payments.setOnClickListener(this);
        newDeviceSetup.setOnClickListener(this);

        email_id.updateValue(UserDetailPreferenceManager.getEmail());
        phone_number.updateValue(UserDetailPreferenceManager.getPhone());

        switch (appPreference.getInt(Constants.USER_TYPE)) {
            case Constants.TYPE_PATIENT:
                if (!appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_PAYMENT)) {
                    billLl.setVisibility(View.VISIBLE);
                    telehealer_billings.setVisibility(View.GONE);
                    patient_payments.setVisibility(View.VISIBLE);
                    patient_payments.updateTitle(getString(R.string.lbl_charges));
                    add_card.setVisibility(View.GONE);
                    subscription.setVisibility(View.GONE);
                    patient_payments.hideSplitter(true);
                } else {
                    subscription.setVisibility(View.GONE);
                    add_card.hideSplitter(true);
                    patient_payments.setVisibility(View.GONE);
                }
                documents.setVisibility(View.VISIBLE);
                medicalAssistant.setVisibility(View.GONE);
                educational_video.setVisibility(View.GONE);
                break;
            case Constants.TYPE_DOCTOR:
                medical_history.setVisibility(View.GONE);
                if (!appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_PAYMENT)) {
                    billLl.setVisibility(View.VISIBLE);
                    telehealer_billings.setVisibility(View.VISIBLE);
                    telehealer_billings.hideSplitter(false);
                    add_card.setVisibility(View.VISIBLE);
                    add_card.hideSplitter(false);
                    subscription.hideSplitter(false);
                    subscription.setVisibility(View.VISIBLE);
                    patient_payments.hideSplitter(true);
                    patient_payments.setVisibility(View.VISIBLE);
                } else {
                    billLl.setVisibility(View.GONE);
                }
                medicalAssistantLl.setVisibility(View.VISIBLE);
                educational_video.setVisibility(View.VISIBLE);
                break;
            case Constants.TYPE_MEDICAL_ASSISTANT:
                medicalAssistantLl.setVisibility(View.GONE);
                if (!appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_PAYMENT)) {
                    billLl.setVisibility(View.VISIBLE);
                    telehealer_billings.setVisibility(View.GONE);
                    add_card.hideSplitter(false);
                    add_card.setVisibility(View.GONE);
                    patient_payments.hideSplitter(true);
                    subscription.setVisibility(View.GONE);
                    subscription.hideSplitter(true);
                    patient_payments.setVisibility(View.VISIBLE);
                } else {
                    billLl.setVisibility(View.GONE);
                }
                break;
        }
        if(Constants.isRedirectProfileSetting){
            Constants.isRedirectProfileSetting=false;
            settings.performClick();
        }
    }
}
