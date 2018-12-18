package com.thealer.telehealer.views.home.schedules;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

/**
 * Created by Aswin on 18,December,2018
 */
public class ScheduleDetailViewFragment extends BaseFragment implements View.OnClickListener {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private OrdersCustomView doctorOcv;
    private ImageView doctorChatIv;
    private View doctorView;
    private OrdersCustomView appointmentTimeOcv;
    private OrdersCustomView patientOcv;
    private ImageView patientChatIv;
    private ImageView patientCallIv;
    private View patientView;
    private LinearLayout cancelLl;
    private View topView;
    private TextView cancelTv;
    private OrdersCustomView reasonOcv;

    private SchedulesApiViewModel schedulesApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;

    private SchedulesApiResponseModel.ResultBean resultBean;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        schedulesApiViewModel = ViewModelProviders.of(this).get(SchedulesApiViewModel.class);
        attachObserverInterface.attachObserver(schedulesApiViewModel);

        schedulesApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel.isSuccess()) {
                                showAlertDialog(getActivity(), getString(R.string.success).toUpperCase(), getString(R.string.schedule_deleted), getString(R.string.ok).toUpperCase(), null,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                onCloseActionInterface.onClose(false);
                                            }
                                        }, null);
                            }
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_detail_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        doctorOcv = (OrdersCustomView) view.findViewById(R.id.doctor_ocv);
        doctorChatIv = (ImageView) view.findViewById(R.id.doctor_chat_iv);
        doctorView = (View) view.findViewById(R.id.doctor_view);
        appointmentTimeOcv = (OrdersCustomView) view.findViewById(R.id.appointment_time_ocv);
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        patientChatIv = (ImageView) view.findViewById(R.id.patient_chat_iv);
        patientCallIv = (ImageView) view.findViewById(R.id.patient_call_iv);
        patientView = (View) view.findViewById(R.id.patient_view);
        cancelLl = (LinearLayout) view.findViewById(R.id.cancel_ll);
        topView = (View) view.findViewById(R.id.top_view);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        reasonOcv = (OrdersCustomView) view.findViewById(R.id.reason_ocv);

        cancelTv.setText(getString(R.string.cancel_appointment));
        toolbarTitle.setText(getString(R.string.appointment_detail));

        backIv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
        doctorChatIv.setOnClickListener(this);
        patientCallIv.setOnClickListener(this);
        patientChatIv.setOnClickListener(this);

        if (UserType.isUserDoctor() || UserType.isUserAssistant()) {
            patientChatIv.setVisibility(View.VISIBLE);
            patientCallIv.setVisibility(View.VISIBLE);
        }

        if (UserType.isUserPatient()) {
            doctorChatIv.setVisibility(View.VISIBLE);
        }

        if (getArguments() != null) {
            resultBean = (SchedulesApiResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.SCHEDULE_DETAIL);
            if (resultBean != null) {
                reasonOcv.setTitleTv(resultBean.getDetail().getReason());
                appointmentTimeOcv.setTitleTv(Utils.getDayMonth(resultBean.getStart()) + " - " + Utils.getFormatedTime(resultBean.getStart()));

                String doctorName = null, doctorSpecialist = null, patientName = null, patientDob = null;

                CommonUserApiResponseModel scheduleWith = resultBean.getScheduled_with_user();
                CommonUserApiResponseModel scheduleBy = resultBean.getScheduled_by_user();

                if (scheduleWith.getRole().equals(Constants.ROLE_PATIENT)) {
                    patientName = scheduleWith.getUserDisplay_name();
                    patientDob = scheduleWith.getDob();
                } else {
                    doctorName = scheduleWith.getDoctorDisplayName();
                    doctorSpecialist = scheduleWith.getDoctorSpecialist();
                }
                if (scheduleBy.getRole().equals(Constants.ROLE_PATIENT)) {
                    patientName = scheduleBy.getUserDisplay_name();
                    patientDob = scheduleBy.getDob();
                } else {
                    doctorName = scheduleBy.getDoctorDisplayName();
                    doctorSpecialist = scheduleBy.getDoctorSpecialist();
                }

                doctorOcv.setTitleTv(doctorName);
                doctorOcv.setSubtitleTv(doctorSpecialist);
                patientOcv.setTitleTv(patientName);
                patientOcv.setSubtitleTv(patientDob);

                if (Utils.isDateTimeExpired(resultBean.getEnd())) {
                    cancelLl.setVisibility(View.GONE);
                    doctorChatIv.setVisibility(View.GONE);
                    patientCallIv.setVisibility(View.GONE);
                    patientChatIv.setVisibility(View.GONE);
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.cancel_tv:
                showAlertDialog(getActivity(), getString(R.string.cancel_schedule), getString(R.string.cancel_schedule_conformation), getString(R.string.yes), getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                schedulesApiViewModel.deleteSchedule(resultBean.getSchedule_id(), true);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
            case R.id.doctor_chat_iv:
                break;
            case R.id.patient_call_iv:
                break;
            case R.id.patient_chat_iv:
                break;
        }
    }
}
