package com.thealer.telehealer.views.home.schedules;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.apilayer.models.settings.AppointmentSlotUpdate;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;

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
    private AppointmentSlotUpdate appointmentSlotUpdate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        changeTitleInterface = (ChangeTitleInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        appointmentSlotUpdate = ViewModelProviders.of(getActivity()).get(AppointmentSlotUpdate.class);
        createScheduleViewModel = ViewModelProviders.of(getActivity()).get(CreateScheduleViewModel.class);
        schedulesApiViewModel = ViewModelProviders.of(this).get(SchedulesApiViewModel.class);
        attachObserverInterface.attachObserver(schedulesApiViewModel);
        attachObserverInterface.attachObserver(appointmentSlotUpdate);

        schedulesApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    sendSuccessViewBroadCast(getActivity(), baseApiResponseModel.isSuccess(), getString(R.string.success),
                            String.format("Your appointment with %s will appear on the schedules tab as soon as it is accepted", createScheduleViewModel.getDoctorCommonModel().getDoctorDisplayName()));
                }
            }
        });

        schedulesApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendSuccessViewBroadCast(getActivity(), errorModel.isSuccess(), getString(R.string.failure),
                            String.format("Your appointment with %s is not requested successfully. Please try again", createScheduleViewModel.getDoctorCommonModel().getDoctorDisplayName()));
                }
            }
        });
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
                showSuccessView(null, RequestID.REQ_SHOW_SUCCESS_VIEW);
                sendSuccessViewBroadCast(getActivity(), true, getString(R.string.please_wait), getString(R.string.posting_your_appointment));
                checkForHistoryUpdate();
                schedulesApiViewModel.createSchedule(null, createScheduleViewModel.getSchedulesCreateRequestModel(), false);
            }
        });
        whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();

        patientHistoryRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        patientHistoryRv.setAdapter(new PatientHistoryAdapter(getActivity()));
    }

    private void checkForHistoryUpdate() {
        for (int i = 0; i < createScheduleViewModel.getPatientHistory().size(); i++) {
            if (createScheduleViewModel.getPatientHistory().get(i).isIsYes() != whoAmIApiResponseModel.getHistory().get(i).isIsYes() ||
                    !createScheduleViewModel.getPatientHistory().get(i).getReason().equals(whoAmIApiResponseModel.getHistory().get(i).getReason())) {
                createScheduleViewModel.getSchedulesCreateRequestModel().getDetail().setChange_medical_info(true);

                WhoAmIApiResponseModel whoAmIApiResponseModel = new WhoAmIApiResponseModel();
                whoAmIApiResponseModel.setHistory(createScheduleViewModel.getPatientHistory());
                appointmentSlotUpdate.updateUserDetail(whoAmIApiResponseModel, false);

                break;
            }
        }
    }
}
