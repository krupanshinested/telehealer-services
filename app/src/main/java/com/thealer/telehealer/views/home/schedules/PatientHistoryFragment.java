package com.thealer.telehealer.views.home.schedules;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.apilayer.models.settings.ProfileUpdate;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.ContentActivity;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 19,December,2018
 */
public class PatientHistoryFragment extends BaseFragment {
    private RecyclerView patientHistoryRv;
    private Button nextBtn;

    private ChangeTitleInterface changeTitleInterface;
    private CreateScheduleViewModel createScheduleViewModel;
    private SchedulesApiViewModel schedulesApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private WhoAmIApiResponseModel whoAmIApiResponseModel;
    private WhoAmIApiViewModel whoAmIApiViewModel;
    private ProfileUpdate profileUpdate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        changeTitleInterface = (ChangeTitleInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        profileUpdate = new ViewModelProvider(getActivity()).get(ProfileUpdate.class);
        createScheduleViewModel = new ViewModelProvider(getActivity()).get(CreateScheduleViewModel.class);
        schedulesApiViewModel = new ViewModelProvider(this).get(SchedulesApiViewModel.class);
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        attachObserverInterface.attachObserver(schedulesApiViewModel);
        attachObserverInterface.attachObserver(profileUpdate);
        attachObserverInterface.attachObserver(whoAmIApiViewModel);

        schedulesApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    sendSuccessViewBroadCast(getActivity(), baseApiResponseModel.isSuccess(), getString(R.string.success),
                            String.format(getString(R.string.appointment_request_success), createScheduleViewModel.getDoctorCommonModel().getDoctorDisplayName()));
                }
            }
        });

        schedulesApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendSuccessViewBroadCast(getActivity(), errorModel.isSuccess(), getString(R.string.failure),
                            String.format(getString(R.string.appointment_request_failure), createScheduleViewModel.getDoctorCommonModel().getDoctorDisplayName()));
                }
            }
        });

        profileUpdate.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null && baseApiResponseModel.isSuccess()) {
                    UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                }
            }
        });


        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null && baseApiResponseModel.isSuccess()) {
                    if (baseApiResponseModel instanceof WhoAmIApiResponseModel)
                        whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                }
            }
        });
        whoAmIApiViewModel.assignWhoAmI();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_history, container, false);
        changeTitleInterface.onTitleChange(getString(R.string.patient_history));
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientHistoryRv = (RecyclerView) view.findViewById(R.id.patient_history_rv);
        nextBtn = (Button) view.findViewById(R.id.next_btn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appPreference.getBoolean(PreferenceConstants.IS_PAYMENT_PRE_AUTH_SHOWN)) {
                    if (AppPaymentCardUtils.hasValidPaymentCard(whoAmIApiResponseModel.getPayment_account_info()))
                        createSchedule();
                    else {
                        AppPaymentCardUtils.handleCardCasesFromPaymentInfo(PatientHistoryFragment.this, whoAmIApiResponseModel.getPayment_account_info(), null);
                    }
                } else {
                    appPreference.setBoolean(PreferenceConstants.IS_PAYMENT_PRE_AUTH_SHOWN, true);
                    Bundle bundle = new Bundle();
                    bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.ic_payment_preauthorization);
                    bundle.putString(ArgumentKeys.TITLE, getString(R.string.payment_pre_authorization));
                    bundle.putString(ArgumentKeys.DESCRIPTION, getString(R.string.payment_pre_authorization_info));
                    bundle.putString(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.request_appointment));
                    startActivityForResult(new Intent(getActivity(), ContentActivity.class).putExtras(bundle), RequestID.REQ_CONTENT_VIEW);
                }
            }
        });
        whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();

        patientHistoryRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        patientHistoryRv.setAdapter(new PatientHistoryAdapter(getActivity()));
    }

    private void createSchedule() {
        showSuccessView(null, RequestID.REQ_SHOW_SUCCESS_VIEW, null);
        sendSuccessViewBroadCast(getActivity(), true, getString(R.string.please_wait), getString(R.string.posting_your_appointment));
        checkForHistoryUpdate();
        schedulesApiViewModel.createSchedule(null, createScheduleViewModel.getDoctorCommonModel().getUser_guid(), createScheduleViewModel.getSchedulesCreateRequestModel(), false);
    }

    private void checkForHistoryUpdate() {
        if (createScheduleViewModel != null && createScheduleViewModel.getPatientHistory() != null) {
            createScheduleViewModel.getSchedulesCreateRequestModel().getDetail().setChange_medical_info(false);
            boolean updateHistory = false;

            if (whoAmIApiResponseModel.getHistory() != null) {
                for (int i = 0; i < createScheduleViewModel.getPatientHistory().size(); i++) {

                    if ((createScheduleViewModel.getPatientHistory().get(i).isIsYes() && !whoAmIApiResponseModel.getHistory().get(i).isIsYes()) ||
                            (!createScheduleViewModel.getPatientHistory().get(i).isIsYes() && whoAmIApiResponseModel.getHistory().get(i).isIsYes()) ||
                            (createScheduleViewModel.getPatientHistory().get(i).getReason() == null && whoAmIApiResponseModel.getHistory().get(i).getReason() != null) ||
                            (createScheduleViewModel.getPatientHistory().get(i).getReason() != null && whoAmIApiResponseModel.getHistory().get(i).getReason() == null) ||
                            (createScheduleViewModel.getPatientHistory().get(i).getReason() != null && whoAmIApiResponseModel.getHistory().get(i).getReason() != null &&
                                    !createScheduleViewModel.getPatientHistory().get(i).getReason().trim().equals(whoAmIApiResponseModel.getHistory().get(i).getReason().trim())) &&
                                    (!createScheduleViewModel.getPatientHistory().get(i).getReason().isEmpty() && !whoAmIApiResponseModel.getHistory().get(i).getReason().isEmpty())) {

                        updateHistory = true;

                        break;
                    }
                }
            } else {
                updateHistory = true;
            }

            if (updateHistory) {
                profileUpdate.updateUserHistory(createScheduleViewModel.getPatientHistory(), false);

                createScheduleViewModel.getSchedulesCreateRequestModel().getDetail().setChange_medical_info(true);

                whoAmIApiResponseModel.setHistory(createScheduleViewModel.getPatientHistory());

                profileUpdate.updateUserDetail(whoAmIApiResponseModel, false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestID.REQ_CONTENT_VIEW && resultCode == Activity.RESULT_OK) {
            if (AppPaymentCardUtils.hasValidPaymentCard(whoAmIApiResponseModel.getPayment_account_info()))
                createSchedule();
            else {
                AppPaymentCardUtils.handleCardCasesFromPaymentInfo(PatientHistoryFragment.this, whoAmIApiResponseModel.getPayment_account_info(), null);
            }
        }
        if (requestCode == RequestID.REQ_CARD_EXPIRE || requestCode == RequestID.REQ_CARD_INFO) {
            createSchedule();
        }
    }
}
