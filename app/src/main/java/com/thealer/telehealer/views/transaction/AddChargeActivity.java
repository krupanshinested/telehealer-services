package com.thealer.telehealer.views.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.master.MasterApiViewModel;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.AddChargeViewModel;
import com.thealer.telehealer.apilayer.models.transaction.ReasonOption;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddChargeActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layoutChargeType;
    private LinearLayout layoutReason;
    private EditText etFees;

    private RecyclerView rvChargeType;
    private RecyclerView rvReason;
    private TextView tvChargeType;
    private TextView tvReason;
    private ImageView ivReason;

    private LinearLayout layoutFromDate;
    private LinearLayout layoutToDate;
    private TextView tvFromDate;
    private TextView tvToDate;
    private EditText etTextField;

    private MasterApiViewModel masterApiViewModel;
    private AddChargeViewModel addChargeViewModel;

    public static String EXTRA_REASON = "reason";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_charge);
        initView();
        initViewModels();
        addChargeViewModel.setSelectedReason(getIntent().getIntExtra(EXTRA_REASON, -1));
        masterApiViewModel.fetchMasters();
        updateUI();
    }


    private void initView() {
        layoutChargeType = findViewById(R.id.layoutChargeType);
        layoutReason = findViewById(R.id.layoutReason);
        rvChargeType = findViewById(R.id.rvChargeType);
        rvReason = findViewById(R.id.rvReasonType);
        tvChargeType = findViewById(R.id.tvChargeType);
        tvReason = findViewById(R.id.tvReason);
        ivReason = findViewById(R.id.ivReasonArrow);

        layoutFromDate = findViewById(R.id.layoutFromDate);
        layoutToDate = findViewById(R.id.layoutTomDate);
        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        etTextField = findViewById(R.id.etTextField);

        layoutChargeType.setOnClickListener(this);
        layoutFromDate.setOnClickListener(this);
        layoutToDate.setOnClickListener(this);

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
                    MasterOptionAdapter adapterType = new MasterOptionAdapter(addChargeViewModel.getListChargeTypes(), pos -> {
                        rvChargeType.setVisibility(View.GONE);
                        tvChargeType.setText(addChargeViewModel.getListChargeTypes().get(pos).getName());
                        addChargeViewModel.setSelectedChargeTypeId(addChargeViewModel.getListChargeTypes().get(pos).getId());
                    });
                    rvChargeType.setAdapter(adapterType);
                }
            }
        });
    }

    private void updateUI() {
        if (addChargeViewModel.getSelectedReason() == -1) {
            ReasonOptionAdapter adapterReason = new ReasonOptionAdapter(addChargeViewModel.getReasonOptions(), pos -> {
                rvReason.setVisibility(View.GONE);
                tvReason.setText(addChargeViewModel.getReasonOptions().get(pos).getTitle());
                addChargeViewModel.setSelectedReason(addChargeViewModel.getReasonOptions().get(pos).getValue());
                showUIByReason();
            });
            rvReason.setAdapter(adapterReason);
            layoutReason.setOnClickListener(this);
            ivReason.setVisibility(View.VISIBLE);
            return;
        }
        ReasonOption selectedOption = addChargeViewModel.getReasonByValue(addChargeViewModel.getSelectedReason());
        if (selectedOption != null) {
            tvReason.setText(selectedOption.getTitle());
            layoutReason.setOnClickListener(null);
            ivReason.setVisibility(View.GONE);

            showUIByReason();
        }
    }

    private void showUIByReason() {
        layoutFromDate.setVisibility(View.GONE);
        layoutToDate.setVisibility(View.GONE);
        etTextField.setVisibility(View.GONE);

        etTextField.setText(null);
        tvFromDate.setText(null);
        tvToDate.setText(null);
        addChargeViewModel.setSelectedFromDate(null);
        addChargeViewModel.setSelectedToDate(null);

        switch (addChargeViewModel.getSelectedReason()) {
            case Constants.ChargeReason.BHI:
            case Constants.ChargeReason.CCM:
            case Constants.ChargeReason.RPM:
            case Constants.ChargeReason.CONCIERGE: {
                tvFromDate.setHint(R.string.lbl_from_date);
                layoutFromDate.setVisibility(View.VISIBLE);
                layoutToDate.setVisibility(View.VISIBLE);
                break;
            }
            case Constants.ChargeReason.MEDICINE: {
                etTextField.setHint(R.string.drug_name);
                etTextField.setVisibility(View.VISIBLE);
                break;
            }
            case Constants.ChargeReason.SUPPLIES: {
                etTextField.setHint(R.string.lbl_supplier);
                etTextField.setVisibility(View.VISIBLE);
                break;
            }
            case Constants.ChargeReason.VISIT: {
                tvFromDate.setHint(R.string.lbl_service_date);
                layoutFromDate.setVisibility(View.VISIBLE);
                break;
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutChargeType:
                if (rvChargeType.getVisibility() == View.VISIBLE)
                    rvChargeType.setVisibility(View.GONE);
                else
                    rvChargeType.setVisibility(View.VISIBLE);
                break;
            case R.id.layoutReason:
                if (rvReason.getVisibility() == View.VISIBLE)
                    rvReason.setVisibility(View.GONE);
                else
                    rvReason.setVisibility(View.VISIBLE);
                break;
            case R.id.layoutFromDate:
                Utils.showDatePickerDialog(this, Calendar.getInstance(), Constants.TYPE_EXPIRATION, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        addChargeViewModel.setSelectedFromDate(calendar);
                        updateDate(tvFromDate, year, month, dayOfMonth);
                    }
                });
                break;
            case R.id.layoutTomDate:
                Utils.showDatePickerDialog(this, addChargeViewModel.getSelectedFromDate() != null ? addChargeViewModel.getSelectedFromDate() : Calendar.getInstance(), Constants.TYPE_EXPIRATION, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        addChargeViewModel.setSelectedToDate(calendar);
                        updateDate(tvToDate, year, month, dayOfMonth);
                    }
                });
                break;
        }
    }

    private void updateDate(TextView dateTextView, int year, int month, int dayOfMonth) {
        dateTextView.setText(Utils.getFormatedDate(year, month, dayOfMonth));
    }
}
