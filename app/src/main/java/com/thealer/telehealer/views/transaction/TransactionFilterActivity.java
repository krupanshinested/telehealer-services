package com.thealer.telehealer.views.transaction;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.master.MasterApiViewModel;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.AddChargeViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.fragmentcontainer.FragmentContainerActivity;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

import java.util.ArrayList;

public class TransactionFilterActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rvChargeType;
    private RecyclerView rvReason;

    private ReasonOptionAdapter adapterReason;
    private MasterSelectionAdapter adapterType;
    private MasterApiViewModel masterApiViewModel;
    private AddChargeViewModel addChargeViewModel;

    private OrdersCustomView doctorOcv;
    private OrdersCustomView patientOcv;

    private CommonUserApiResponseModel doctorModel;
    private CommonUserApiResponseModel patientModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_filter);
        doctorOcv = (OrdersCustomView) findViewById(R.id.doctor_ocv);
        patientOcv = (OrdersCustomView) findViewById(R.id.patient_ocv);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.filter));

        rvChargeType = findViewById(R.id.rvChargeType);
        rvReason = findViewById(R.id.rvReasonType);


        initViewModels();

        adapterReason = new ReasonOptionAdapter(addChargeViewModel.getReasonOptions(), false, pos -> {
            addChargeViewModel.getReasonOptions().get(pos).setSelected(!addChargeViewModel.getReasonOptions().get(pos).isSelected());
            adapterReason.notifyItemChanged(pos);
        });
        rvReason.setAdapter(adapterReason);
        masterApiViewModel.fetchMasters();

        if (UserType.isUserPatient()) {
            patientOcv.setVisibility(View.GONE);
        } else if (UserType.isUserDoctor()) {
            doctorOcv.setVisibility(View.GONE);
        }

        doctorOcv.setOnClickListener(this);
        patientOcv.setOnClickListener(this);
    }

    private void initViewModels() {
        masterApiViewModel = new ViewModelProvider(this).get(MasterApiViewModel.class);
        addChargeViewModel = new ViewModelProvider(this).get(AddChargeViewModel.class);
        attachObserver(masterApiViewModel);
        attachObserver(addChargeViewModel);

        masterApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof MasterResp) {
                    MasterResp resp = (MasterResp) baseApiResponseModel;
                    addChargeViewModel.setUpChargeTypeFromMasters(resp);
                    adapterType = new MasterSelectionAdapter(addChargeViewModel.getListChargeTypes(), pos -> {
                        addChargeViewModel.getListChargeTypes().get(pos).setSelected(!addChargeViewModel.getListChargeTypes().get(pos).isSelected());
                        adapterType.notifyItemChanged(pos);
                    });
                    rvChargeType.setAdapter(adapterType);
                }
            }
        });
    }

    private void showAssociationSelection(int requestCode, String searchType, String user_guid) {
        Bundle bundle = new Bundle();

        bundle.putString(ArgumentKeys.SEARCH_TYPE, searchType);
        bundle.putString(ArgumentKeys.DOCTOR_GUID, user_guid);
        startActivityForResult(new Intent(this, FragmentContainerActivity.class)
                .putExtra(FragmentContainerActivity.EXTRA_FRAGMENT, SelectAssociationFragment.class.getName())
                .putExtra(FragmentContainerActivity.EXTRA_BUNDLE, bundle), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestID.REQ_SELECT_ASSOCIATION_PATIENT:
                    if (data != null && data.getExtras() != null) {
                        patientModel = (CommonUserApiResponseModel) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                        setPatientOcv(patientModel.getUserDisplay_name(), patientModel.getDob());
                        /*selectedPatientDetailModel = (CommonUserApiResponseModel) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                        createScheduleViewModel.setPatientCommonModel(selectedPatientDetailModel);
                        patientSchedulesTimeList.clear();
                        createScheduleViewModel.getTimeSlots().setValue(new ArrayList<>());
                        enableOrDisableBtn();*/
                    }
                    break;
                case RequestID.REQ_SELECT_ASSOCIATION_DOCTOR:
                    if (data != null && data.getExtras() != null) {

                        doctorModel = (CommonUserApiResponseModel) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                        setDoctorOcv(doctorModel.getUserDisplay_name(), doctorModel.getDoctorSpecialist());
                      /*  if (UserDetailPreferenceManager.getRole().equals(Constants.ROLE_PATIENT) && !doctor.getAppt_requests()) {
                            Utils.showAlertDialog(getActivity(), getString(R.string.no_new_appointment), String.format(getString(R.string.appointment_not_allowed_create)), doctor.getOfficePhoneNo(), getString(R.string.ok)
                                    , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Uri uri = Uri.parse("tel:" + doctor.getOfficePhoneNo());
                                            startActivity(new Intent(Intent.ACTION_DIAL, uri));
                                        }
                                    }, null);
                        } else {
                   *//*         doctorDetailCommonModel = doctor;
                            doctorSchedulesTimeList.clear();
                            createScheduleViewModel.setDoctorCommonModel(doctorDetailCommonModel);
                            createScheduleViewModel.getTimeSlots().setValue(new ArrayList<>());
                            enableOrDisableBtn();*//*
                        }*/

                    }
                    break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doctor_ocv: {
                showAssociationSelection(RequestID.REQ_SELECT_ASSOCIATION_DOCTOR, ArgumentKeys.SEARCH_ASSOCIATION_DOCTOR, null);
                break;
            }
            case R.id.patient_ocv: {
                if (UserType.isUserDoctor()) {
                    showAssociationSelection(RequestID.REQ_SELECT_ASSOCIATION_PATIENT, ArgumentKeys.SEARCH_ASSOCIATION, null);
                } else if (UserType.isUserAssistant()) {
                    if (doctorModel == null) {
                        Utils.showAlertDialog(this, getString(R.string.please_select_a_doctor), null, getString(R.string.ok), null,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, null);
                    } else {
                        showAssociationSelection(RequestID.REQ_SELECT_ASSOCIATION_PATIENT, ArgumentKeys.SEARCH_ASSOCIATION, doctorModel.getUser_guid());
                    }
                }
                break;
            }
        }
    }


    private void setPatientOcv(String userDisplay_name, String dob) {
        patientOcv.setTitleTv(userDisplay_name);
        patientOcv.setSubtitleTv(dob);
        patientOcv.setSub_title_visible(true);
    }

    private void setDoctorOcv(String doctorDisplayName, String doctorSpecialist) {
        doctorOcv.setTitleTv(doctorDisplayName);
        doctorOcv.setSubtitleTv(doctorSpecialist);
        doctorOcv.setSub_title_visible(true);
    }
}
