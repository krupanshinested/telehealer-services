package com.thealer.telehealer.views.signup.patient;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;
import com.thealer.telehealer.views.signup.SignUpActivity;

/**
 * Created by Aswin on 12,October,2018
 */
public class PatientChoosePaymentFragment extends BaseFragment implements View.OnClickListener {

    private CustomButton choosePaymentInsuranceBtn;
    private CustomButton choosePaymentCashBtn;
    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private Dialog dialog;

    private int currentScreenType = Constants.forRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_choose_payment, container, false);

        if (getArguments() != null) {
            currentScreenType = getArguments().getInt(ArgumentKeys.SCREEN_TYPE, Constants.forRegistration);
        }

        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        onViewChangeInterface.hideOrShowNext(false);

        if (currentScreenType == Constants.forProfileUpdate) {
            onViewChangeInterface.updateTitle(getString(R.string.payment_methods));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
    }

    private void initView(View view) {
        choosePaymentInsuranceBtn = (CustomButton) view.findViewById(R.id.choose_payment_insurance_btn);
        choosePaymentCashBtn = (CustomButton) view.findViewById(R.id.choose_payment_cash_btn);

        choosePaymentInsuranceBtn.setOnClickListener(this);
        choosePaymentCashBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_payment_insurance_btn:
                switch (currentScreenType) {
                    case Constants.forRegistration:
                        ((SignUpActivity) getActivity()).addChildFragment(new PatientUploadInsuranceFragment());
                        break;
                    case Constants.forProfileUpdate:
                        onActionCompleteInterface.onCompletionResult(RequestID.INSURANCE_REQUEST_IMAGE, true, null);
                        break;
                }
                break;
            case R.id.choose_payment_cash_btn:
                showConformationDialog();
                break;
        }
    }

    private void showConformationDialog() {
        showAlertDialog(getActivity(), getString(R.string.alert), getString(R.string.payment_alert_info),
                getString(R.string.cancel), getString(R.string.Continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (currentScreenType) {
                            case Constants.forRegistration:
                                onActionCompleteInterface.onCompletionResult(null, true, null);
                                break;
                            case Constants.forProfileUpdate:
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ArgumentKeys.CASH_SELECTED, true);
                                onActionCompleteInterface.onCompletionResult(RequestID.INSURANCE_CHANGE_RESULT, true, bundle);
                                break;
                        }

                    }
                });
    }

    @Override
    public void onDestroy() {

        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

        super.onDestroy();
    }
}
