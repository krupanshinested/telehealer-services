package com.thealer.telehealer.views.transaction;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.master.MasterApiViewModel;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.TransactionOption;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AddChargeActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layoutChargeType;
    private LinearLayout layoutReason;
    private EditText etFees;

    private RecyclerView rvChargeType;
    private RecyclerView rvReason;
    private TextView tvChargeType;
    private TextView tvReason;

    private MasterApiViewModel masterApiViewModel;

    private ArrayList<MasterResp.MasterItem> listChargeTypes = new ArrayList<>();

    private ArrayList<MasterResp.MasterItem> listReason = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_charge);
        layoutChargeType = findViewById(R.id.layoutChargeType);
        layoutReason = findViewById(R.id.layoutReason);
        rvChargeType = findViewById(R.id.rvChargeType);
        rvReason = findViewById(R.id.rvReasonType);
        tvChargeType = findViewById(R.id.tvChargeType);
        tvReason = findViewById(R.id.tvReason);

        layoutChargeType.setOnClickListener(this);
        layoutReason.setOnClickListener(this);


        masterApiViewModel = new ViewModelProvider(this).get(MasterApiViewModel.class);
        attachObserver(masterApiViewModel);
        masterApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof MasterResp) {
                    MasterResp resp = (MasterResp) baseApiResponseModel;
                    HashMap<Integer, ArrayList<MasterResp.MasterItem>> filteredMaster = new HashMap<>();
                    MasterResp.MasterItem chargeTypeParentMaster = null;
                    MasterResp.MasterItem chargeReasonParentMaster = null;
                    for (MasterResp.MasterItem masterItem : resp.getData()) {
                        if (masterItem.getParentId() == 0) {
                            filteredMaster.put(masterItem.getId(), new ArrayList<>());
                            if (Constants.MasterCodes.TYPE_OF_CHARGE.equals(masterItem.getCode())) {
                                chargeTypeParentMaster = masterItem;
                            }
                            if (Constants.MasterCodes.REASON.equals(masterItem.getCode())) {
                                chargeReasonParentMaster = masterItem;
                            }

                        } else {
                            if (filteredMaster.get(masterItem.getParentId()) == null)
                                filteredMaster.put(masterItem.getParentId(), new ArrayList<>());
                            filteredMaster.get(masterItem.getParentId()).add(masterItem);
                        }
                    }
                    if (chargeTypeParentMaster != null) {
                        listChargeTypes.addAll(filteredMaster.get(chargeTypeParentMaster.getId()));
                    }
                    if (chargeReasonParentMaster != null) {
                        listReason.addAll(filteredMaster.get(chargeTypeParentMaster.getId()));
                    }

                    initAdapter();
                }


            }
        });
        masterApiViewModel.fetchMasters();
    }

    private void initAdapter() {
        TransactionOptionAdapter adapterChargeType = new TransactionOptionAdapter(listChargeTypes, new TransactionOptionAdapter.OnOptionSelected() {
            @Override
            public void onSelected(int pos) {
                rvChargeType.setVisibility(View.GONE);
                tvChargeType.setText(listChargeTypes.get(pos).getName());
            }
        });
        rvChargeType.setAdapter(adapterChargeType);

        TransactionOptionAdapter adapterReason = new TransactionOptionAdapter(listReason, new TransactionOptionAdapter.OnOptionSelected() {
            @Override
            public void onSelected(int pos) {
                rvReason.setVisibility(View.GONE);
                tvReason.setText(listReason.get(pos).getName());
            }
        });
        rvReason.setAdapter(adapterReason);
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
