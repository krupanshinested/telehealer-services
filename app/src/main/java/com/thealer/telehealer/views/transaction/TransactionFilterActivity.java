package com.thealer.telehealer.views.transaction;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.master.MasterApiViewModel;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.AddChargeViewModel;
import com.thealer.telehealer.apilayer.models.transaction.ReasonOption;
import com.thealer.telehealer.apilayer.models.transaction.req.TransactionListReq;
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
import java.util.Calendar;

public class TransactionFilterActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_FILTER = "filter";
    public static final String EXTRA_IS_RESET = "is_reset";

    private RecyclerView rvChargeType;
    private RecyclerView rvReason;
    private Button btnSubmit;
    private Button btnReset;

    private ReasonOptionAdapter adapterReason;
    private MasterSelectionAdapter adapterType;
    private MasterApiViewModel masterApiViewModel;
    private AddChargeViewModel addChargeViewModel;

    private OrdersCustomView doctorOcv;
    private OrdersCustomView patientOcv;

    private CommonUserApiResponseModel doctorModel;
    private CommonUserApiResponseModel patientModel;

    private DateRangeView dateFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_filter);
        doctorOcv = (OrdersCustomView) findViewById(R.id.doctor_ocv);
        patientOcv = (OrdersCustomView) findViewById(R.id.patient_ocv);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnReset = findViewById(R.id.btnReset);
        dateFilter = findViewById(R.id.dateFilter);

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
        btnSubmit.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                    String filterJson = getIntent().getStringExtra(EXTRA_FILTER);
                    if (filterJson != null) {
                        setDataFromRequest(new Gson().fromJson(filterJson, TransactionListReq.class));
                    }
                    rvReason.getAdapter().notifyDataSetChanged();
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
                        setPatientOcv(patientModel.getUserDisplay_name());
                    }
                    break;
                case RequestID.REQ_SELECT_ASSOCIATION_DOCTOR:
                    if (data != null && data.getExtras() != null) {

                        doctorModel = (CommonUserApiResponseModel) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                        setDoctorOcv(doctorModel.getUserDisplay_name());
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
            case R.id.btnSubmit: {
                if (dateFilter.getSelectedToDate() != null) {
                    if (dateFilter.getSelectedFromDate().getTimeInMillis() > dateFilter.getSelectedToDate().getTimeInMillis()) {
                        Utils.showAlertDialog(this, getString(R.string.error), getString(R.string.msg_please_select_valid_date_range_for_any, getString(R.string.filter)), getString(R.string.ok), null, null, null);
                        return;
                    }
                }
                setResult(RESULT_OK, new Intent().putExtra(EXTRA_FILTER, new Gson().toJson(getReq())));
                finish();
                break;
            }
            case R.id.btnReset: {
                /*patientModel = null;
                doctorModel = null;
                setDoctorOcv(null);
                setPatientOcv(null);
                for (MasterResp.MasterItem masterItem : addChargeViewModel.getListChargeTypes()) {
                    masterItem.setSelected(false);
                }
                for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions()) {
                    reasonOption.setSelected(false);
                }
                */

                dateFilter.setSelectedToDate(Calendar.getInstance());
                Calendar fromDate = Calendar.getInstance();
                fromDate.add(Calendar.MONTH, -1);
                dateFilter.setSelectedFromDate(fromDate);

                TransactionListReq req = new TransactionListReq();
                req.setFilter(new TransactionListReq.Filter());
                req.getFilter().setFromDate(Utils.getUTCDateFromCalendar(dateFilter.getSelectedFromDate()));
                req.getFilter().setToDate(Utils.getUTCDateFromCalendar(dateFilter.getSelectedToDate()));

                setResult(RESULT_OK, new Intent().putExtra(EXTRA_IS_RESET, true).putExtra(EXTRA_FILTER, new Gson().toJson(req)));
                finish();
            }
        }
    }

    private TransactionListReq getReq() {
        TransactionListReq req = new TransactionListReq();
        TransactionListReq.Filter filter = new TransactionListReq.Filter();
        if (patientModel != null) {
            filter.setPatientId(patientModel.getUser_id());
            filter.setPatientName(patientOcv.getTitleText());
        }
        if (doctorModel != null) {
            filter.setDoctorId(doctorModel.getUser_id());
            filter.setDoctorName(doctorOcv.getTitleText());
        }
        if (dateFilter.getSelectedFromDate() != null) {
            filter.setFromDate(Utils.getUTCDateFromCalendar(dateFilter.getSelectedFromDate()));
        }
        if (dateFilter.getSelectedToDate() != null) {
            filter.setToDate(Utils.getUTCDateFromCalendar(dateFilter.getSelectedToDate()));
        }
        ArrayList<Integer> selectedTypes = new ArrayList<>();
        for (MasterResp.MasterItem masterItem : addChargeViewModel.getListChargeTypes())
            if (masterItem.isSelected())
                selectedTypes.add(masterItem.getId());
        if (selectedTypes.size() > 0)
            filter.setTypeOfCharge(selectedTypes);

        ArrayList<Integer> selectedReasons = new ArrayList<>();
        for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions())
            if (reasonOption.isSelected())
                selectedReasons.add(reasonOption.getValue());
        if (selectedReasons.size() > 0)
            filter.setReasons(selectedReasons);

        req.setFilter(filter);
        return req;
    }

    private void setDataFromRequest(TransactionListReq req) {
        if (req != null) {
            TransactionListReq.Filter filter = req.getFilter();
            if (filter != null) {
                if (filter.getPatientId() != 0) {
                    patientModel = new CommonUserApiResponseModel();
                    patientModel.setUser_id(filter.getPatientId());
                    setPatientOcv(filter.getPatientName());
                }

                if (filter.getDoctorId() != 0) {
                    doctorModel = new CommonUserApiResponseModel();
                    doctorModel.setUser_id(filter.getDoctorId());
                    setDoctorOcv(filter.getDoctorName());
                }

                if (filter.getFromDate() != null)
                    dateFilter.setSelectedFromDate(Utils.getCalendar(filter.getFromDate()));

                if (filter.getToDate() != null)
                    dateFilter.setSelectedToDate(Utils.getCalendar(filter.getToDate()));


                if (filter.getReasons() != null) {
                    for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions()) {
                        reasonOption.setSelected(filter.getReasons().contains(reasonOption.getValue()));
                    }
                }
                if (filter.getTypeOfCharge() != null) {
                    for (MasterResp.MasterItem masterItem : addChargeViewModel.getListChargeTypes()) {
                        masterItem.setSelected(filter.getTypeOfCharge().contains(masterItem.getId()));
                    }
                }
            }
        }

    }


    private void setPatientOcv(String userDisplay_name) {
        patientOcv.setTitleTv(userDisplay_name);
    }

    private void setDoctorOcv(String doctorDisplayName) {
        doctorOcv.setTitleTv(doctorDisplayName);
    }
}
