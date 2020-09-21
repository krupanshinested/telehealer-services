package com.thealer.telehealer.views.signup.doctor;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.createuser.PracticesBean;
import com.thealer.telehealer.apilayer.models.createuser.VisitAddressBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseBottomSheetDialogFragment;

import java.util.ArrayList;

/**
 * Created by Aswin on 25,October,2018
 */
public class DoctorPracticesBottomSheetFragment extends BaseBottomSheetDialogFragment {
    private TextView deleteTv;
    private TextView doneTv;
    private TextInputLayout practiceTil;
    private EditText practiceEt;
    private TextInputLayout streetTil;
    private EditText streetEt;
    private TextInputLayout street2Til;
    private EditText street2Et;
    private TextInputLayout cityTil;
    private EditText cityEt;
    private TextInputLayout stateTil;
    private EditText stateEt;
    private TextInputLayout zipTil;
    private EditText zipEt;
    private int practiceId;
    private CreateUserRequestModel createUserRequestModel;
    private PracticesBean practicesBean;
    private boolean isNewPractice;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);

        if (createUserRequestModel.getUser_detail() != null &&
                createUserRequestModel.getUser_detail().getData().getPractices().size() > practiceId) {

            practicesBean = createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId);

            setData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.bottomsheet_doctor_practices, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        deleteTv = (TextView) view.findViewById(R.id.delete_tv);
        doneTv = (TextView) view.findViewById(R.id.done_tv);
        practiceTil = (TextInputLayout) view.findViewById(R.id.practice_til);
        practiceEt = (EditText) view.findViewById(R.id.practice_et);
        streetTil = (TextInputLayout) view.findViewById(R.id.street_til);
        streetEt = (EditText) view.findViewById(R.id.street_et);
        street2Til = (TextInputLayout) view.findViewById(R.id.street2_til);
        street2Et = (EditText) view.findViewById(R.id.street2_et);
        cityTil = (TextInputLayout) view.findViewById(R.id.city_til);
        cityEt = (EditText) view.findViewById(R.id.city_et);
        stateTil = (TextInputLayout) view.findViewById(R.id.state_til);
        stateEt = (EditText) view.findViewById(R.id.state_et);
        zipTil = (TextInputLayout) view.findViewById(R.id.zip_til);
        zipEt = (EditText) view.findViewById(R.id.zip_et);

        setBottomSheetHeight(view.findViewById(R.id.parent_view), 80);

        if (getArguments() != null) {
            isNewPractice = getArguments().getBoolean(Constants.IS_NEW_PRACTICE);

            if (!isNewPractice)
                practiceId = getArguments().getInt(Constants.PRACTICE_ID);
        }

        doneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (practiceEt.getText().toString().isEmpty()) {
                    practiceEt.requestFocus();
                    practiceTil.setError(getString(R.string.enter_practice_name));
                } else if (streetEt.getText().toString().isEmpty()) {
                    streetEt.requestFocus();
                    streetTil.setError(getString(R.string.enter_street));
                } else if (cityEt.getText().toString().isEmpty()) {
                    cityEt.requestFocus();
                    cityTil.setError(getString(R.string.enter_city));
                } else if (stateEt.getText().toString().isEmpty()) {
                    stateEt.requestFocus();
                    stateTil.setError(getString(R.string.enter_state));
                } else if (!Utils.isValidState(stateEt.getText().toString())){
                    stateEt.requestFocus();
                }else if (zipEt.getText().toString().isEmpty()) {
                    zipEt.requestFocus();
                    zipTil.setError(getString(R.string.enter_zip_code));
                } else {

                    if (isNewPractice) {

                        createUserRequestModel.getUser_detail().getData().getPractices().add(new PracticesBean(practiceEt.getText().toString(),
                                null,
                                new VisitAddressBean(streetEt.getText().toString(),
                                        street2Et.getText().toString(),
                                        cityEt.getText().toString(),
                                        0,
                                        zipEt.getText().toString(),
                                        0,
                                        null,
                                        stateEt.getText().toString().toUpperCase()),
                                new ArrayList<>()));

                    } else {
                        PracticesBean practicesBean = createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId);
                        practicesBean.setName(practiceEt.getText().toString());
                        practicesBean.setVisit_address(new VisitAddressBean(streetEt.getText().toString(),
                                street2Et.getText().toString(),
                                cityEt.getText().toString(),
                                practicesBean.getVisit_address().getLon(),
                                zipEt.getText().toString(),
                                practicesBean.getVisit_address().getLat(),
                                practicesBean.getVisit_address().getState_long(),
                                stateEt.getText().toString().toUpperCase()));

                        createUserRequestModel.getUser_detail().getData().getPractices().set(practiceId, practicesBean);

                    }

                    notifyParent();
                    getDialog().dismiss();
                }
            }
        });

        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserRequestModel.getUser_detail().getData().getPractices().remove(practiceId);
                notifyParent();
                getDialog().dismiss();
            }
        });

        stateEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Utils.isValidState(s.toString())){
                    stateTil.setErrorEnabled(false);
                }else {
                    stateTil.setError(getString(R.string.enter_valid_state));
                }
            }
        });
    }

    private void notifyParent() {
        getTargetFragment().onActivityResult(getTargetRequestCode(), RequestID.REQ_PRACTICE, null);
    }

    private void setData() {

        deleteTv.setVisibility(View.VISIBLE);

        practiceEt.setText(practicesBean.getName());

        if (practicesBean.getVisit_address() != null) {

            streetEt.setText(practicesBean.getVisit_address().getStreet());
            street2Et.setText(practicesBean.getVisit_address().getStreet2());
            cityEt.setText(practicesBean.getVisit_address().getCity());
            stateEt.setText(practicesBean.getVisit_address().getState());
            zipEt.setText(practicesBean.getVisit_address().getZip());
        }
    }

}
