package com.thealer.telehealer.views.settings;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.DeleteAccount.DeleteAccountViewModel;
import com.thealer.telehealer.apilayer.models.settings.AppointmentSlotUpdate;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.settings.Interface.SettingClickListener;
import com.thealer.telehealer.views.settings.cellView.ProfileCellView;
import com.thealer.telehealer.views.signin.SigninActivity;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 11/15/18.
 */

public class ProfileSettingFragment extends BaseFragment implements View.OnClickListener {

    private SettingClickListener settingClickListener;
    private OnViewChangeInterface onViewChangeInterface;

    private ProfileCellView profile,medical_history,settings,email_id,
            phone_number,change_password,
            feedback,terms_and_condition, privacy_policy,appointment_slots,payments_billings;
    private View signOut;

    private AppointmentSlotUpdate appointmentSlotUpdate;
    private Boolean isSlotLoaded = false;

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

        if (view.getId() == R.id.appointment_slots) {
            appointment_slots.openSpinner();
        } else {
            settingClickListener.didSelecteItem(view.getId());
        }
    }

    private void initView(View baseView) {
        profile = baseView.findViewById(R.id.profile);
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

                }
            }
        });

        isSlotLoaded = false;
        appointment_slots.updateAdapter(titleAdapter,new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)  {
                if (!isSlotLoaded) {
                    isSlotLoaded = true;
                } else {
                    String selectedItem = parent.getItemAtPosition(position).toString(); //this is your selected item
                    appointmentSlotUpdate.updateAppointmentSlot(selectedItem);
                    UserDetailPreferenceManager.setAppt_length(Integer.parseInt(selectedItem));
                    appointment_slots.updateUI(false);
                    appointment_slots.updateValue(selectedItem);
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        profile.setOnClickListener(this);
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

        email_id.updateValue(UserDetailPreferenceManager.getEmail());
        phone_number.updateValue(UserDetailPreferenceManager.getPhone());

        switch (appPreference.getInt(Constants.USER_TYPE)) {
            case Constants.TYPE_PATIENT:
                appointment_slots.setVisibility(View.GONE);
                change_password.hideSplitter(true);
                payments_billings.setVisibility(View.GONE);
                break;
            case Constants.TYPE_DOCTOR:
                appointment_slots.updateValue(UserDetailPreferenceManager.getAppt_length()+"");
                medical_history.setVisibility(View.GONE);
                payments_billings.setVisibility(View.VISIBLE);
                break;
            case Constants.TYPE_MEDICAL_ASSISTANT:
                medical_history.setVisibility(View.GONE);
                appointment_slots.setVisibility(View.GONE);
                payments_billings.setVisibility(View.GONE);
                break;
        }
    }
}
