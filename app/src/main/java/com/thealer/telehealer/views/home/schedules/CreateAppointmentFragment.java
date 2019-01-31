package com.thealer.telehealer.views.home.schedules;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesCreateRequestModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.SelectAssociationFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.settings.ProfileSettingsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 19,December,2018
 */
public class CreateAppointmentFragment extends BaseFragment implements View.OnClickListener {
    private TextView infoTv;
    private OrdersCustomView doctorOcv;
    private OrdersCustomView patientOcv;
    private TextView appointmentTimeTv;
    private RecyclerView slotsRv;
    private TextView slotInfoTv;
    private TextView addSlotTv;
    private EditText reasonEt;
    private OrdersCustomView demographOcv;
    private CustomButton demographUpdateBtn;
    private View demographView;
    private OrdersCustomView insuranceOcv;
    private CustomButton insuranceUpdateBtn;
    private LinearLayout insuranceFrontLl;
    private ImageView frontIv;
    private TextView frontTv;
    private LinearLayout insuranceBackLl;
    private ImageView backIv;
    private TextView backTv;
    private Button actionBtn;

    private ShowSubFragmentInterface showSubFragmentInterface;
    private WhoAmIApiViewModel whoAmIApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private CreateScheduleViewModel createScheduleViewModel;
    private SchedulesApiViewModel schedulesApiViewModel;
    private ChangeTitleInterface changeTitleInterface;

    private CommonUserApiResponseModel patientDetailModel, selectedPatientDetailModel, doctorDetailCommonModel;
    private WhoAmIApiResponseModel whoAmIApiResponseModel;
    private List<String> doctorSchedulesTimeList = new ArrayList<>();
    private List<String> patientSchedulesTimeList = new ArrayList<>();
    private boolean isDoctorSchedules = false, isPatientSchedules = false, isDemographicUpdated = false, isInsuranceUpdated = false;
    private String requestee_name;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        changeTitleInterface = (ChangeTitleInterface) getActivity();

        createScheduleViewModel = ViewModelProviders.of(getActivity()).get(CreateScheduleViewModel.class);
        schedulesApiViewModel = ViewModelProviders.of(this).get(SchedulesApiViewModel.class);
        whoAmIApiViewModel = ViewModelProviders.of(this).get(WhoAmIApiViewModel.class);
        attachObserverInterface.attachObserver(whoAmIApiViewModel);

        createScheduleViewModel.getTimeSlots().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> list) {
                if (list != null) {
                    if (list.size() == 3) {
                        addSlotTv.setVisibility(View.GONE);
                    } else {
                        addSlotTv.setVisibility(View.VISIBLE);
                    }
                    enableOrDisableBtn();
                }
            }
        });

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                    UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);

                    if (UserType.isUserPatient()) {
                        if (createScheduleViewModel.getPatientCommonModel() != null &&
                                createScheduleViewModel.getPatientCommonModel().getUser_detail() != null &&
                                createScheduleViewModel.getPatientCommonModel().getUser_detail().getData() != null) {

                            if (!createScheduleViewModel.getPatientCommonModel().getFirst_name().equals(whoAmIApiResponseModel.getFirst_name()) ||
                                    !createScheduleViewModel.getPatientCommonModel().getLast_name().equals(whoAmIApiResponseModel.getLast_name()) ||
                                    !createScheduleViewModel.getPatientCommonModel().getDob().equals(whoAmIApiResponseModel.getDob()) ||
                                    !createScheduleViewModel.getPatientCommonModel().getGender().equals(whoAmIApiResponseModel.getGender())) {
                                isDemographicUpdated = true;
                            }

                            if (createScheduleViewModel.getPatientCommonModel().getUser_detail().getData().getInsurance_front() != null &&
                                    createScheduleViewModel.getPatientCommonModel().getUser_detail().getData().getInsurance_back() != null &&
                                    whoAmIApiResponseModel.getUser_detail().getData().getInsurance_front() != null &&
                                    whoAmIApiResponseModel.getUser_detail().getData().getInsurance_back() != null) {

                                if (!createScheduleViewModel.getPatientCommonModel().getUser_detail().getData().getInsurance_front()
                                        .equals(whoAmIApiResponseModel.getUser_detail().getData().getInsurance_front()) ||
                                        !createScheduleViewModel.getPatientCommonModel().getUser_detail().getData().getInsurance_back()
                                                .equals(whoAmIApiResponseModel.getUser_detail().getData().getInsurance_back())) {

                                    isInsuranceUpdated = true;
                                }
                            }
                        }
                        createScheduleViewModel.setPatientCommonModel(whoAmIApiResponseModel);
                    } else {
                        createScheduleViewModel.setDoctorCommonModel(whoAmIApiResponseModel);
                    }

                    updateView();
                }
            }
        });

        schedulesApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<SchedulesApiResponseModel.ResultBean> arrayList = (ArrayList<SchedulesApiResponseModel.ResultBean>) (Object) baseApiResponseModels;
                    for (SchedulesApiResponseModel.ResultBean resultBean : arrayList) {
                        if (isDoctorSchedules) {
                            doctorSchedulesTimeList.add(resultBean.getStart());
//                            doctorSchedulesTimeList.add(resultBean.getEnd());
                        } else {
                            patientSchedulesTimeList.add(resultBean.getStart());
//                            patientSchedulesTimeList.add(resultBean.getEnd());
                        }
                    }

                    isDoctorSchedules = false;
                    isPatientSchedules = false;

                }
            }
        });

        schedulesApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    sendSuccessViewBroadCast(getActivity(), baseApiResponseModel.isSuccess(), getString(R.string.success),
                            String.format("Your appointment with %s will appear on the schedules tab as soon as it is accepted", requestee_name));
                }
            }
        });

        schedulesApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendSuccessViewBroadCast(getActivity(), errorModel.isSuccess(), getString(R.string.failure),
                            String.format("Your appointment with %s is not requested successfully. Please try again", requestee_name));
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_appointment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        infoTv = (TextView) view.findViewById(R.id.info_tv);
        doctorOcv = (OrdersCustomView) view.findViewById(R.id.doctor_ocv);
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        appointmentTimeTv = (TextView) view.findViewById(R.id.appointment_time_tv);
        slotsRv = (RecyclerView) view.findViewById(R.id.slots_rv);
        slotInfoTv = (TextView) view.findViewById(R.id.slot_info_tv);
        addSlotTv = (TextView) view.findViewById(R.id.add_slot_tv);
        reasonEt = (EditText) view.findViewById(R.id.reason_et);
        demographOcv = (OrdersCustomView) view.findViewById(R.id.demograph_ocv);
        demographUpdateBtn = (CustomButton) view.findViewById(R.id.demograph_update_btn);
        demographView = (View) view.findViewById(R.id.demograph_view);
        insuranceOcv = (OrdersCustomView) view.findViewById(R.id.insurance_ocv);
        insuranceUpdateBtn = (CustomButton) view.findViewById(R.id.insurance_update_btn);
        insuranceFrontLl = (LinearLayout) view.findViewById(R.id.insurance_front_ll);
        frontIv = (ImageView) view.findViewById(R.id.front_iv);
        frontTv = (TextView) view.findViewById(R.id.front_tv);
        insuranceBackLl = (LinearLayout) view.findViewById(R.id.insurance_back_ll);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        backTv = (TextView) view.findViewById(R.id.back_tv);
        actionBtn = (Button) view.findViewById(R.id.action_btn);

        if (UserType.isUserPatient()) {
            infoTv.setVisibility(View.VISIBLE);
            patientOcv.setVisibility(View.GONE);
            demographOcv.setVisibility(View.VISIBLE);
            demographUpdateBtn.setVisibility(View.VISIBLE);
            demographView.setVisibility(View.VISIBLE);
            insuranceOcv.setVisibility(View.VISIBLE);
            insuranceUpdateBtn.setVisibility(View.VISIBLE);
            insuranceFrontLl.setVisibility(View.VISIBLE);
            insuranceBackLl.setVisibility(View.VISIBLE);

            actionBtn.setText(getString(R.string.next));

        } else if (UserType.isUserDoctor()) {
            doctorOcv.setVisibility(View.GONE);
            patientOcv.setVisibility(View.VISIBLE);
        } else if (UserType.isUserAssistant()) {
            patientOcv.setVisibility(View.VISIBLE);
        }

        doctorOcv.setOnClickListener(this);
        patientOcv.setOnClickListener(this);
        addSlotTv.setOnClickListener(this);
        demographUpdateBtn.setOnClickListener(this);
        insuranceUpdateBtn.setOnClickListener(this);
        actionBtn.setOnClickListener(this);

        reasonEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableOrDisableBtn();
            }
        });

        if (UserType.isUserPatient()) {
            createScheduleViewModel.setPatientCommonModel(UserDetailPreferenceManager.getWhoAmIResponse());
        } else if (UserType.isUserDoctor()) {
            createScheduleViewModel.setDoctorCommonModel(UserDetailPreferenceManager.getWhoAmIResponse());
        }

        if (getArguments() != null) {
            if (UserType.isUserPatient()) {
                doctorOcv.setOnClickListener(null);
                doctorOcv.setArrow_visible(false);
                doctorDetailCommonModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                createScheduleViewModel.setDoctorCommonModel(doctorDetailCommonModel);
            } else if (UserType.isUserDoctor()) {
                patientOcv.setOnClickListener(null);
                patientOcv.setArrow_visible(false);
                patientDetailModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                createScheduleViewModel.setPatientCommonModel(patientDetailModel);
            }
        }

        updateView();
    }

    private void updateView() {
        if (createScheduleViewModel.getDoctorCommonModel() != null) {
            doctorDetailCommonModel = createScheduleViewModel.getDoctorCommonModel();
        }
        if (createScheduleViewModel.getPatientCommonModel() != null) {
            patientDetailModel = createScheduleViewModel.getPatientCommonModel();
        }

        if (!UserType.isUserDoctor()) {
            if (doctorDetailCommonModel != null) {
                setDoctorOcv(doctorDetailCommonModel.getDoctorDisplayName(), doctorDetailCommonModel.getDoctorSpecialist());
                if (doctorSchedulesTimeList.isEmpty()) {
                    getDoctorScheduleList(doctorDetailCommonModel.getUser_guid());
                }
            }
        }


        if (!UserType.isUserPatient()) {
            if (patientDetailModel != null) {
                setPatientOcv(patientDetailModel.getUserDisplay_name(), patientDetailModel.getDob());
                if (patientSchedulesTimeList.isEmpty()) {
                    if (UserType.isUserDoctor()) {
                        getPatientScheduleList(patientDetailModel.getUser_guid(), null);
                    } else if (UserType.isUserAssistant()) {
                        getPatientScheduleList(patientDetailModel.getUser_guid(), doctorDetailCommonModel.getUser_guid());
                    }
                }
            }
        }

        if (UserType.isUserPatient()) {

            if (patientDetailModel == null) {
                whoAmIApiViewModel.checkWhoAmI();
            } else {

                setDemographOcv(patientDetailModel.getUserDisplay_name(), patientDetailModel.getGender(), patientDetailModel.getAge());

                if (patientDetailModel.getUser_detail() != null && patientDetailModel.getUser_detail().getData() != null) {
                    if (patientDetailModel.getUser_detail().getData().getInsurance_front() == null &&
                            patientDetailModel.getUser_detail().getData().getInsurance_back() == null) {
                        showAsCash();
                    } else {
                        insuranceOcv.setTitle_visible(false);
                        insuranceFrontLl.setVisibility(View.VISIBLE);
                        insuranceBackLl.setVisibility(View.VISIBLE);
                        Utils.setImageWithGlide(getActivity(), frontIv, patientDetailModel.getUser_detail().getData().getInsurance_front(), null, true);
                        Utils.setImageWithGlide(getActivity(), backIv, patientDetailModel.getUser_detail().getData().getInsurance_back(), null, true);
                    }
                } else {
                    showAsCash();
                }
            }
        } else if (UserType.isUserDoctor() && createScheduleViewModel.getDoctorCommonModel() == null) {
            whoAmIApiViewModel.checkWhoAmI();
        }

        if (createScheduleViewModel.getTimeSlots().getValue() == null) {
            createScheduleViewModel.getTimeSlots().setValue(new ArrayList<>());
        }
        slotsRv.setVisibility(View.VISIBLE);
        slotsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        slotsRv.setAdapter(new SlotsListAdapter(getActivity()));

        enableOrDisableBtn();
    }

    private void getPatientScheduleList(String user_guid, String doctorGuid) {
        isPatientSchedules = true;
        schedulesApiViewModel.getUserSchedules(user_guid, doctorGuid, true, true);
    }

    private void getDoctorScheduleList(String user_guid) {
        isDoctorSchedules = true;
        schedulesApiViewModel.getUserSchedules(user_guid, null, true, true);
    }

    private void enableOrDisableBtn() {
        boolean enable = false;
        if (!reasonEt.getText().toString().isEmpty() &&
                !createScheduleViewModel.getTimeSlots().getValue().isEmpty()) {

            if (UserType.isUserPatient()) {
                if (createScheduleViewModel.getDoctorCommonModel() != null) {
                    enable = true;
                }
            }

            if (UserType.isUserDoctor() && createScheduleViewModel.getPatientCommonModel() != null) {
                enable = true;
            }

            if (UserType.isUserAssistant()) {
                if (createScheduleViewModel.getPatientCommonModel() != null && createScheduleViewModel.getDoctorCommonModel() != null) {
                    enable = true;
                }
            }
        }

        actionBtn.setEnabled(enable);
    }

    private void showAsCash() {
        insuranceOcv.setTitleTv(getString(R.string.cash));
        insuranceOcv.setTitle_visible(true);
        insuranceFrontLl.setVisibility(View.GONE);
        insuranceBackLl.setVisibility(View.GONE);
    }

    private void setDemographOcv(String userDisplay_name, String gender, String age) {
        demographOcv.setTitleTv(userDisplay_name);
        demographOcv.setSubtitleTv(gender + ", " + age);
    }

    private void setPatientOcv(String userDisplay_name, String dob) {
        patientOcv.setTitleTv(userDisplay_name);
        patientOcv.setSubtitleTv(dob);
        patientOcv.setSub_title_visible(true);
    }

    private void setDoctorOcv(String doctorDisplayName, String doctorSpecialist) {
        doctorOcv.setTitleTv(doctorDisplayName);
        doctorOcv.setSubtitleTv(doctorSpecialist);
        doctorOcv.setSub_title_visible(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doctor_ocv:
                showAssociationSelection(RequestID.REQ_SELECT_ASSOCIATION_DOCTOR, ArgumentKeys.SEARCH_ASSOCIATION_DOCTOR, null);
                break;
            case R.id.patient_ocv:
                if (UserType.isUserDoctor()) {
                    showAssociationSelection(RequestID.REQ_SELECT_ASSOCIATION_PATIENT, ArgumentKeys.SEARCH_ASSOCIATION, null);
                } else if (UserType.isUserAssistant()) {
                    if (createScheduleViewModel.getDoctorCommonModel() == null) {
                        showSelectAssoctiationAlert(getString(R.string.please_select_a_doctor));
                    } else {
                        showAssociationSelection(RequestID.REQ_SELECT_ASSOCIATION_PATIENT, ArgumentKeys.SEARCH_ASSOCIATION, createScheduleViewModel.getDoctorCommonModel().getUser_guid());
                    }
                }
                break;
            case R.id.add_slot_tv:
                if (createScheduleViewModel.getTimeSlots().getValue().size() < 3) {
                    if (UserType.isUserPatient()) {
                        if (createScheduleViewModel.getDoctorCommonModel() == null) {
                            showSelectAssoctiationAlert(getString(R.string.select_doctor_to_add_slot));
                        } else {
                            showSlotSelectionDialogFragment();
                        }
                    } else {
                        if (createScheduleViewModel.getPatientCommonModel() == null) {
                            showSelectAssoctiationAlert(getString(R.string.select_patient_to_add_slot));
                        } else {
                            showSlotSelectionDialogFragment();
                        }
                    }
                }
                break;
            case R.id.demograph_update_btn:
            case R.id.insurance_update_btn:
                goToProfileUpdate();
                break;
            case R.id.action_btn:
                SchedulesCreateRequestModel schedulesCreateRequestModel = new SchedulesCreateRequestModel();
                String to_guid = null;

                if (UserType.isUserPatient()) {
                    schedulesCreateRequestModel.setRequestee_id(String.valueOf(createScheduleViewModel.getDoctorCommonModel().getUser_id()));
                    requestee_name = createScheduleViewModel.getDoctorCommonModel().getFirst_name();
                    to_guid = createScheduleViewModel.getDoctorCommonModel().getUser_guid();
                } else {
                    schedulesCreateRequestModel.setRequestee_id(String.valueOf(createScheduleViewModel.getPatientCommonModel().getUser_id()));
                    requestee_name = createScheduleViewModel.getPatientCommonModel().getFirst_name();
                    to_guid = createScheduleViewModel.getPatientCommonModel().getUser_guid();
                }

                schedulesCreateRequestModel.setMessage(reasonEt.getText().toString());

                SchedulesCreateRequestModel.Requestdetails requestdetails = new SchedulesCreateRequestModel.Requestdetails();

                List<SchedulesCreateRequestModel.Requestdetails.Dates> datesList = new ArrayList<>();

                for (int i = 0; i < createScheduleViewModel.getTimeSlots().getValue().size(); i++) {
                    SchedulesCreateRequestModel.Requestdetails.Dates dates = new SchedulesCreateRequestModel.Requestdetails.Dates();
                    dates.setStart(createScheduleViewModel.getTimeSlots().getValue().get(i));
                    dates.setEnd(Utils.getIncreasedTime(createScheduleViewModel.getDoctorCommonModel().getAppt_length(), createScheduleViewModel.getTimeSlots().getValue().get(i)));
                    datesList.add(dates);
                }
                requestdetails.setDates(datesList);
                requestdetails.setReason(reasonEt.getText().toString());
                requestdetails.setInsurance_to_date(isInsuranceUpdated);
                requestdetails.setChange_demographic(isDemographicUpdated);

                schedulesCreateRequestModel.setDetail(requestdetails);
                createScheduleViewModel.setSchedulesCreateRequestModel(schedulesCreateRequestModel);

                if (UserType.isUserPatient()) {
                    createScheduleViewModel.setPatientHistory(UserDetailPreferenceManager.getWhoAmIResponse().getHistory());
                    PatientHistoryFragment patientHistoryFragment = new PatientHistoryFragment();
                    showSubFragmentInterface.onShowFragment(patientHistoryFragment);
                } else {
                    String doctorGuid = null;
                    if (UserType.isUserAssistant()) {
                        doctorGuid = createScheduleViewModel.getDoctorCommonModel().getUser_guid();
                    }
                    showSuccessView(null, RequestID.REQ_SHOW_SUCCESS_VIEW);
                    schedulesApiViewModel.createSchedule(doctorGuid, to_guid, createScheduleViewModel.getSchedulesCreateRequestModel(), false);
                }
                break;
        }
    }

    private void goToProfileUpdate() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.VIEW_TYPE, Constants.SCHEDULE_CREATION_MODE);
        startActivityForResult(new Intent(getActivity(), ProfileSettingsActivity.class).putExtras(bundle), RequestID.REQ_PROFILE_UPDATE);
    }

    private void showSlotSelectionDialogFragment() {
        addSlotTv.setClickable(false);

        List<String> unavailableTimeSlots = new ArrayList<>();

        if (UserType.isUserPatient()) {
            unavailableTimeSlots.addAll(doctorSchedulesTimeList);
        } else if (UserType.isUserDoctor()) {
            unavailableTimeSlots.addAll(patientSchedulesTimeList);
        } else {
            unavailableTimeSlots.addAll(doctorSchedulesTimeList);
            unavailableTimeSlots.addAll(patientSchedulesTimeList);
        }

        if (createScheduleViewModel.getTimeSlots().getValue() != null) {
            unavailableTimeSlots.addAll(createScheduleViewModel.getTimeSlots().getValue());
        }
        createScheduleViewModel.getUnAvaliableTimeSlots().clear();
        createScheduleViewModel.setUnAvaliableTimeSlots(unavailableTimeSlots);

        SlotSelectionDialogFragment slotSelectionDialogFragment = new SlotSelectionDialogFragment();
        slotSelectionDialogFragment.setTargetFragment(this, RequestID.REQ_SLOT_SELECTION);
        slotSelectionDialogFragment.show(getActivity().getSupportFragmentManager(), slotSelectionDialogFragment.getClass().getSimpleName());
    }

    private void showSelectAssoctiationAlert(String title) {

        showAlertDialog(getActivity(), title, null, getString(R.string.ok), null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, null);
        addSlotTv.setEnabled(true);
    }

    private void showAssociationSelection(int requestCode, String searchType, String user_guid) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(ArgumentKeys.SEARCH_TYPE, searchType);
        bundle.putString(ArgumentKeys.DOCTOR_GUID, user_guid);

        SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();
        selectAssociationFragment.setArguments(bundle);
        selectAssociationFragment.setTargetFragment(this, requestCode);

        showSubFragmentInterface.onShowFragment(selectAssociationFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestID.REQ_SELECT_ASSOCIATION_PATIENT:
                    if (data != null && data.getExtras() != null) {
                        selectedPatientDetailModel = (CommonUserApiResponseModel) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                        createScheduleViewModel.setPatientCommonModel(selectedPatientDetailModel);
                        patientSchedulesTimeList.clear();
                        createScheduleViewModel.getTimeSlots().setValue(new ArrayList<>());
                        enableOrDisableBtn();
                    }
                    break;
                case RequestID.REQ_SELECT_ASSOCIATION_DOCTOR:
                    if (data != null && data.getExtras() != null) {
                        doctorDetailCommonModel = (CommonUserApiResponseModel) data.getExtras().getSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL);
                        doctorSchedulesTimeList.clear();
                        createScheduleViewModel.setDoctorCommonModel(doctorDetailCommonModel);
                        createScheduleViewModel.getTimeSlots().setValue(new ArrayList<>());
                        enableOrDisableBtn();
                    }
                    break;
                case RequestID.REQ_PROFILE_UPDATE:
                    whoAmIApiViewModel.checkWhoAmI();
                    break;
                case RequestID.REQ_SLOT_SELECTION:
                    addSlotTv.setClickable(true);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeTitleInterface.onTitleChange(getString(R.string.new_appointment));
    }
}
