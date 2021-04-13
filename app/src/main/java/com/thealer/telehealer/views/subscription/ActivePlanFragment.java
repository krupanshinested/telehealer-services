package com.thealer.telehealer.views.subscription;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.subscription.PlanInfo;
import com.thealer.telehealer.common.CustomSpinnerView;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.List;

import static com.thealer.telehealer.common.Constants.activatedPlan;
import static com.thealer.telehealer.common.Constants.isFromSubscriptionPlan;
import static com.thealer.telehealer.common.Constants.subscriptionPlanList;


public class ActivePlanFragment extends BaseFragment implements View.OnClickListener {
    private OnCloseActionInterface onCloseActionInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private TextView toolbarTitle, tvPlanName, tvTotalRpm;
    private ImageView backIv;
    private Button btnCancel, btnChange;

    public ActivePlanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active_plan, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        tvPlanName = (TextView) view.findViewById(R.id.tv_plan_name);
        tvTotalRpm = (TextView) view.findViewById(R.id.tv_total_rpm);
        btnChange = (Button) view.findViewById(R.id.btn_change);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        toolbarTitle.setText(getString(R.string.lbl_my_subscriptions));

        backIv.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnChange.setOnClickListener(this);

        prePareData();
    }

    private void prePareData() {
        if (activatedPlan == -1 && !isFromSubscriptionPlan) {
            btnChange.performClick();
        }else if (activatedPlan == -1 && isFromSubscriptionPlan) {
            isFromSubscriptionPlan=false;
            onCloseActionInterface.onClose(false);
        } else {
            PlanInfo currentPlanInfo = subscriptionPlanList.get(activatedPlan);
            tvPlanName.setText(currentPlanInfo.getPlanName());
            tvTotalRpm.setText("15");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.btn_cancel:
                selectReason(v);
                break;
            case R.id.btn_change:
                SubscriptionPlanFragment subscriptionPlanFragment = new SubscriptionPlanFragment();
                showSubFragmentInterface.onShowFragment(subscriptionPlanFragment);
                break;
        }
    }


    //Allow physician to view list of support staff. Also physician can request to add them.
    private void selectReason(View v) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View layoutInflateView = layoutInflater.inflate
                (R.layout.cancel_reason_alert, (ViewGroup) v.findViewById(R.id.cl_root));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(layoutInflateView);
        alertDialog.setCancelable(false);
        AlertDialog dialog = alertDialog.create();
        TextView headerTitle = layoutInflateView.findViewById(R.id.header_title);

        Button btnConfirm = layoutInflateView.findViewById(R.id.btn_confirm);
        Button btnCancel = layoutInflateView.findViewById(R.id.btn_cancel);
        Spinner spinner = (Spinner) layoutInflateView.findViewById(R.id.spinner);
        EditText commentsEt = (EditText) layoutInflateView.findViewById(R.id.comments_et);

        headerTitle.setText("This plan will continue till the end of the month. Thank you for your business.");
        List<String> tempList = new ArrayList<>();
        tempList = new ArrayList<>();

        tempList.add("Choose a reason for cancelling subscriptions");
        tempList.add("Reason 1");
        tempList.add("Reason 2");
        tempList.add("Reason 3");
        tempList.add("Reason 4");
        tempList.add("Reason 5");
        tempList.add("Reason 6");
        tempList.add("Reason 7");
        tempList.add("Reason 8");
        tempList.add("Other");
        commentsEt.setVisibility(View.GONE);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, tempList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        List<String> finalTempList = tempList;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==(finalTempList.size()-1)){
                    commentsEt.setVisibility(View.VISIBLE);
                }else{
                    commentsEt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("neem", "onClick: " + spinner.getSelectedItem());
                dialog.dismiss();
                activatedPlan=-1;
                isFromSubscriptionPlan=false;
                onCloseActionInterface.onClose(false);
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}