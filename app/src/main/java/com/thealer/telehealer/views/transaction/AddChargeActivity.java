package com.thealer.telehealer.views.transaction;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.TransactionOption;
import com.thealer.telehealer.views.base.BaseActivity;

import java.util.Arrays;
import java.util.List;

public class AddChargeActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layoutChargeType;
    private LinearLayout layoutReason;
    private EditText etFees;

    private RecyclerView rvChargeType;
    private RecyclerView rvReason;
    private TextView tvChargeType;
    private TextView tvReason;

    private List<TransactionOption> listChargeTypes = Arrays.asList(
            new TransactionOption("Co-pay"),
            new TransactionOption("Deductible"),
            new TransactionOption("Co-insurance"),
            new TransactionOption("Fee")
    );

    private List<TransactionOption> listReason = Arrays.asList(
            new TransactionOption("Visit"),
            new TransactionOption("Medicine"),
            new TransactionOption("Supplies"),
            new TransactionOption("CCM"),
            new TransactionOption("RPM"),
            new TransactionOption("BHI"),
            new TransactionOption("Concierge")
    );


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

        initAdapter();
    }

    private void initAdapter() {
        TransactionOptionAdapter adapterChargeType = new TransactionOptionAdapter(listChargeTypes, new TransactionOptionAdapter.OnOptionSelected() {
            @Override
            public void onSelected(int pos) {
                rvChargeType.setVisibility(View.GONE);
                tvChargeType.setText(listChargeTypes.get(pos).getTitle());
            }
        });
        rvChargeType.setAdapter(adapterChargeType);

        TransactionOptionAdapter adapterReason = new TransactionOptionAdapter(listReason, new TransactionOptionAdapter.OnOptionSelected() {
            @Override
            public void onSelected(int pos) {
                rvReason.setVisibility(View.GONE);
                tvReason.setText(listReason.get(pos).getTitle());
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
