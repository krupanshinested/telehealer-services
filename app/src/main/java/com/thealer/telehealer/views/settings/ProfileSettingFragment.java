package com.thealer.telehealer.views.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.settings.AppointmentSlotUpdate;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.TokBox;
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
            phone_number, change_password,
            feedback, terms_and_condition, privacy_policy, appointment_slots, payments_billings;
    private View signOut;

    private AppointmentSlotUpdate appointmentSlotUpdate;
    private Boolean isSlotLoaded = false;
    private LinearLayout medicalAssistantLl;
    private ProfileCellView medicalAssistant;
    private ProfileCellView documents;
    private TextView versionTv;
    private String selectedItem;
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
        if (view.getId() == R.id.signOut && TokBox.shared.isActiveCallPreset()) {
            Toast.makeText(getActivity(), getString(R.string.live_call_going_error), Toast.LENGTH_LONG).show();
            return;
        }

        if (view.getId() == R.id.appointment_slots) {
            appointment_slots.openSpinner();
        } else {
            settingClickListener.didSelecteItem(view.getId());
        }
    }

    private void initView(View baseView) {
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
        payments_billings = baseView.findViewById(R.id.payments_billings);
        medicalAssistantLl = (LinearLayout) baseView.findViewById(R.id.medical_assistant_ll);
        medicalAssistant = (ProfileCellView) baseView.findViewById(R.id.medical_assistant);
        versionTv = (TextView) baseView.findViewById(R.id.version_tv);
        lastLoginTv = (TextView) baseView.findViewById(R.id.last_login_tv);

        lastLoginTv.setText(getString(R.string.last_login, appPreference.getString(PreferenceConstants.LAST_LOGIN)));


        try {
            versionTv.setText(getString(R.string.version) + " " + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appointment_slots = baseView.findViewById(R.id.appointment_slots);
        String[] titleList = getActivity().getResources().getStringArray(R.array.doctor_appointment_slots);
        ArrayAdapter titleAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, titleList);
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        appointmentSlotUpdate = ViewModelProviders.of(getActivity()).get(AppointmentSlotUpdate.class);

        onViewChangeInterface.attachObserver(appointmentSlotUpdate);

        appointmentSlotUpdate.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();
                    whoAmIApiResponseModel.setAppt_length(Integer.parseInt(selectedItem));
                    UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);

                    appointment_slots.updateUI(false);
                    appointment_slots.updateValue(selectedItem);

                }
            }
        });

        isSlotLoaded = false;
        appointment_slots.updateAdapter(titleAdapter, new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isSlotLoaded) {
                    isSlotLoaded = true;
                } else {
                    selectedItem = parent.getItemAtPosition(position).toString();
                    appointmentSlotUpdate.updateAppointmentSlot(selectedItem);
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        appointment_slots.setOnClickListener(this);
        payments_billings.setOnClickListener(this);
        medicalAssistantLl.setOnClickListener(this);

        email_id.updateValue(UserDetailPreferenceManager.getEmail());
        phone_number.updateValue(UserDetailPreferenceManager.getPhone());

        switch (appPreference.getInt(Constants.USER_TYPE)) {
            case Constants.TYPE_PATIENT:
                appointment_slots.setVisibility(View.GONE);
                change_password.hideSplitter(true);
                payments_billings.setVisibility(View.GONE);
                documents.setVisibility(View.VISIBLE);
                break;
            case Constants.TYPE_DOCTOR:
                appointment_slots.updateValue(UserDetailPreferenceManager.getAppt_length() + "");
                medical_history.setVisibility(View.GONE);
                if (!appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_PAYMENT))
                    payments_billings.setVisibility(View.VISIBLE);
                else {
                    payments_billings.setVisibility(View.GONE);
                }
                medicalAssistantLl.setVisibility(View.VISIBLE);
                break;
            case Constants.TYPE_MEDICAL_ASSISTANT:
                medical_history.setVisibility(View.GONE);
                appointment_slots.setVisibility(View.GONE);
                payments_billings.setVisibility(View.GONE);
                break;
        }
    }
}
