package com.thealer.telehealer.views.signup.patient;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;
import com.thealer.telehealer.views.signup.SignUpActivity;
import com.thealer.telehealer.views.signup.SignupBaseFragment;

/**
 * Created by Aswin on 12,October,2018
 */
public class PatientChoosePaymentFragment extends SignupBaseFragment implements View.OnClickListener {

    private CustomButton choosePaymentInsuranceBtn;
    private CustomButton choosePaymentCashBtn;
    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private Dialog dialog;

    private int currentScreenType = Constants.forRegistration;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView closeIv;

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
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        choosePaymentInsuranceBtn = (CustomButton) view.findViewById(R.id.choose_payment_insurance_btn);
        choosePaymentCashBtn = (CustomButton) view.findViewById(R.id.choose_payment_cash_btn);

        choosePaymentInsuranceBtn.setOnClickListener(this);
        choosePaymentCashBtn.setOnClickListener(this);

        if (getArguments() != null) {
            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR, false)) {
                appbarLayout.setVisibility(View.VISIBLE);
                backIv.setVisibility(View.GONE);
                nextTv.setVisibility(View.GONE);
                closeIv.setVisibility(View.VISIBLE);
                closeIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OnCloseActionInterface) getActivity()).onClose(false);
                    }
                });
                toolbarTitle.setText(getString(R.string.payment_methods));
            }
        }
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
                        Bundle bundle = getArguments();
                        onActionCompleteInterface.onCompletionResult(RequestID.INSURANCE_REQUEST_IMAGE, true, bundle);
                        break;
                }
                break;
            case R.id.choose_payment_cash_btn:
                showConformationDialog();
                break;
        }
    }

    private void showConformationDialog() {
        Utils.showAlertDialog(getActivity(), getString(R.string.alert), getString(R.string.payment_alert_info),
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
                                CreateUserRequestModel createUserRequestModel = new ViewModelProvider(getActivity()).get(CreateUserRequestModel.class);
                                postPatientDetail(createUserRequestModel);
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
