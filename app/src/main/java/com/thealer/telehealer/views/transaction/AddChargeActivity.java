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

import java.util.Calendar;

public class AddChargeActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layoutChargeType;
    private LinearLayout layoutReason;
    private EditText etFees;

    private RecyclerView rvChargeType;
    private RecyclerView rvReason;
    private TextView tvChargeType;
    private TextView tvReason;
    private ImageView ivReason;

    private EditText etTextField;

    private MasterApiViewModel masterApiViewModel;
    private AddChargeViewModel addChargeViewModel;

    private ReasonOptionAdapter adapterReason;

    private DateRangeView viewBHI;
    private DateRangeView viewCCM;
    private DateRangeView viewConcierge;
    private DateRangeView viewRPM;


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
        checkForVisitAndSetUI();
    }


    private void initView() {
        layoutChargeType = findViewById(R.id.layoutChargeType);
        layoutReason = findViewById(R.id.layoutReason);
        rvChargeType = findViewById(R.id.rvChargeType);
        rvReason = findViewById(R.id.rvReasonType);
        tvChargeType = findViewById(R.id.tvChargeType);
        tvReason = findViewById(R.id.tvReason);
        ivReason = findViewById(R.id.ivReasonArrow);

        viewBHI = findViewById(R.id.bhi);
        viewCCM = findViewById(R.id.ccm);
        viewRPM = findViewById(R.id.rpm);
        viewConcierge = findViewById(R.id.concierge);


        etTextField = findViewById(R.id.etTextField);
        etFees = findViewById(R.id.etFees);

        layoutChargeType.setOnClickListener(this);

        rvReason.setNestedScrollingEnabled(false);


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
        for (ReasonOption reasonOption : addChargeViewModel.getReasonOptions()) {
            showUIByReason(reasonOption);
        }
    }


    private void checkForVisitAndSetUI() {
        if (addChargeViewModel.isReasonSelected(Constants.ChargeReason.VISIT)) {
            layoutReason.setVisibility(View.VISIBLE);
            etFees.setVisibility(View.VISIBLE);
        } else {
            adapterReason = new ReasonOptionAdapter(addChargeViewModel.getReasonOptions(), pos -> {
                addChargeViewModel.getReasonOptions().get(pos).setSelected(!addChargeViewModel.getReasonOptions().get(pos).isSelected());
                adapterReason.notifyItemChanged(pos);
                showUIByReason(addChargeViewModel.getReasonOptions().get(pos));
            });
            rvReason.setAdapter(adapterReason);
            ivReason.setVisibility(View.VISIBLE);

        }
    }

    private void showUIByReason(ReasonOption reasonOption) {
        switch (reasonOption.getValue()) {
            case Constants.ChargeReason.BHI: {
                viewBHI.show(reasonOption.isSelected());
                break;
            }
            case Constants.ChargeReason.CCM: {
                viewCCM.show(reasonOption.isSelected());
                break;
            }
            case Constants.ChargeReason.RPM: {
                viewRPM.show(reasonOption.isSelected());
                break;
            }
            case Constants.ChargeReason.CONCIERGE: {
                viewConcierge.show(reasonOption.isSelected());
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

        }
    }


}
