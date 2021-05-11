package com.thealer.telehealer.views.inviteUser;

import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByDemographicRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.DatePickerDialogFragment;
import com.thealer.telehealer.views.common.DateBroadcastReceiver;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteByDemographicFragment extends InviteUserBaseFragment {
    private TextInputLayout firstnameTil;
    private EditText firstnameEt;
    private TextInputLayout lastnameTil;
    private EditText lastnameEt;
    private TextInputLayout dobTil;
    private EditText dobEt;
    private Spinner genderSp;
    private Button inviteBtn;
    private Bundle bundle = null;

    private CommonUserApiResponseModel commonUserApiResponseModel = null;
    private String doctor_guid = null;

    private DateBroadcastReceiver dateBroadcastReceiver = new DateBroadcastReceiver() {
        @Override
        public void onDateReceived(String formatedDate) {
            dobEt.setText(formatedDate);
            enableOrDisableInvite();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_by_demographic, container, false);
        initView(view);
        changeTitleInterface.onTitleChange(getString(R.string.invite_by_demographics));
        return view;
    }

    private void initView(View view) {
        bundle=this.getArguments();
        if(bundle!=null){
            String role=bundle.getString(ArgumentKeys.ROLE,"");
            Log.e(TAG, "initView: "+role );
        }

        firstnameTil = (TextInputLayout) view.findViewById(R.id.firstname_til);
        firstnameEt = (EditText) view.findViewById(R.id.firstname_et);
        lastnameTil = (TextInputLayout) view.findViewById(R.id.lastname_til);
        lastnameEt = (EditText) view.findViewById(R.id.lastname_et);
        dobTil = (TextInputLayout) view.findViewById(R.id.dob_til);
        dobEt = (EditText) view.findViewById(R.id.dob_et);
        genderSp = (Spinner) view.findViewById(R.id.gender_sp);
        inviteBtn = (Button) view.findViewById(R.id.invite_btn);

        addTextWatcher(firstnameEt);
        addTextWatcher(lastnameEt);

        String[] genderList = getActivity().getResources().getStringArray(R.array.gender_list);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, genderList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSp.setAdapter(arrayAdapter);

        if (getArguments() != null && getArguments().getSerializable(Constants.USER_DETAIL) != null) {
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            if (commonUserApiResponseModel != null) {
                doctor_guid = commonUserApiResponseModel.getUser_guid();
            }
        }

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSuccessFragment();
                InviteByDemographicRequestModel demographicRequestMode = new InviteByDemographicRequestModel(bundle.getString(ArgumentKeys.ROLE,""),firstnameEt.getText().toString(),
                        lastnameEt.getText().toString(),
                        dobEt.getText().toString(),
                        genderSp.getSelectedItem().toString().toLowerCase());
                inviteUserApiViewModel.inviteUserByDemographic(demographicRequestMode, doctor_guid, false);
            }
        });

        dobEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.DATE_PICKER_TYPE, Constants.TILL_CURRENT_DAY);
                datePickerDialogFragment.setArguments(bundle);
                datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), datePickerDialogFragment.getClass().getSimpleName());
            }
        });
    }

    private void addTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableOrDisableInvite();
            }
        });
    }

    private void enableOrDisableInvite() {
        boolean enable = false;

        if (!firstnameEt.getText().toString().isEmpty() &&
                !lastnameEt.getText().toString().isEmpty() &&
                !dobEt.getText().toString().isEmpty()) {
            enable = true;
        }

        inviteBtn.setEnabled(enable);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.DATE_PICKER_INTENT));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dateBroadcastReceiver);
    }
}
