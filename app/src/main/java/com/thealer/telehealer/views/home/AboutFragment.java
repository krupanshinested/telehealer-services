package com.thealer.telehealer.views.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.OnAdapterListener;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.addConnection.ConnectionListApiViewModel;
import com.thealer.telehealer.apilayer.models.addConnection.DesignationResponseModel;
import com.thealer.telehealer.apilayer.models.addConnection.ValueBaseCareViewModel;
import com.thealer.telehealer.apilayer.models.associationDetail.DisconnectAssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.commonResponseModel.PermissionBean;
import com.thealer.telehealer.apilayer.models.commonResponseModel.PermissionRequestModel;
import com.thealer.telehealer.apilayer.models.userPermission.UserPermissionApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OnItemEndListener;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.imagePreview.ImagePreviewDialogFragment;
import com.thealer.telehealer.views.common.imagePreview.ImagePreviewViewModel;
import com.thealer.telehealer.views.home.userPermission.UserPermissionAdapter;
import com.thealer.telehealer.views.settings.Adapters.AboutHistoryAdapter;
import com.thealer.telehealer.views.settings.RemotePatientMonitoringFragment;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryList;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryViewFragment;
import com.thealer.telehealer.views.signup.patient.InsuranceViewPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import config.AppConfig;

/**
 * Created by Aswin on 14,November,2018
 */
public class AboutFragment extends BaseFragment implements OnAdapterListener {
    private LinearLayout patientDetailView;
    private CardView medicalHistoryBtn;
    private CardView insuranceCv;
    private TextView insuranceDetailTv;
    private ViewPager insuranceViewPager;
    private LinearLayout insurancePagerIndicator;
    private CardView emailCv;
    private TextView patientEmailTv;
    private LinearLayout doctorDetailView;
    private TextView licenseHintTv;
    private TextView npiHintTv;
    private TextView licenseTv;
    private TextView npiTv;
    private TextView doctorBioTv;
    private TextView moreLessTv;
    private CardView clinicCv;
    private TextView clinicAddressTv;
    private CardView phoneCv;
    private TextView userPhoneTv;
    private RecyclerView rvRootPermission;
    private ConstraintLayout clPermission;
    private UserPermissionAdapter userPermissionAdapter;
    private TextView disconnectTv;
    private ImageView[] indicators;
    private TextView insuranceCashTv;
    private LinearLayout insuranceImageLl;
    private ConstraintLayout clVitalHistory, clHistory;
    private RecyclerView rvVitalHistory, rvHistory;
    private TextView tvRpmStatus, tvVitalEdit;
    private int userType;
    private String view_type, doctorGuid = null;
    private CommonUserApiResponseModel userDetail, doctorDetail;
    private DisconnectAssociationApiViewModel disconnectAssociationApiViewModel;
    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private UserPermissionApiViewModel userPermissionApiViewModel;
    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra(ArgumentKeys.CONNECTION_STATUS);
            onStatusUpdate(status);
        }
    };
    private ConstraintLayout doctorDetailCl;
    private ConstraintLayout indianDocDetailCl;
    private TextView registrationNumberTv;
    private TextView yearOfRegistrationTv;
    private TextView mciTv;
    private CardView websiteCv;
    private TextView websiteTv;
    private List<PermissionBean> permissionList = new ArrayList<>();
    private AboutHistoryAdapter historyAdapter;
    private AboutHistoryAdapter vitalHistoryAdapter;
    private CardView designationCv;
    private TextView designationTv;
    private ConnectionListApiViewModel connectionListApiViewModel;
    private AddConnectionApiViewModel addConnectionApiViewModel;
    private ValueBaseCareViewModel valueBaseCareViewModel;
    private DesignationResponseModel designationResponseModel;
    private List<String> designationList = new ArrayList<>();
    private int selectedId;
    private String finaldesignation;
    private CompoundButton rpmbhiccm;
    private Boolean rpmbhiccmValue;
    private CardView valuebasesummarypermission;
    private Switch permissionrpm,permissionccm,permissionbhi;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        userPermissionApiViewModel = new ViewModelProvider(this).get(UserPermissionApiViewModel.class);
        attachObserverInterface.attachObserver(userPermissionApiViewModel);
        connectionListApiViewModel = new ViewModelProvider(this).get(ConnectionListApiViewModel.class);
        attachObserverInterface.attachObserver(connectionListApiViewModel);
        valueBaseCareViewModel = new ViewModelProvider(this).get(ValueBaseCareViewModel.class);
        attachObserverInterface.attachObserver(valueBaseCareViewModel);
        addConnectionApiViewModel = new ViewModelProvider(this).get(AddConnectionApiViewModel.class);
        attachObserverInterface.attachObserver(addConnectionApiViewModel);

        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                    intent.putExtra(Constants.SUCCESS_VIEW_STATUS, baseApiResponseModel.isSuccess());

                    if (baseApiResponseModel.isSuccess()) {
                        designationTv.setText(finaldesignation);
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, baseApiResponseModel.getMessage());

                    } else {
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, baseApiResponseModel.getMessage());
                    }

                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                }
            }
        });

        addConnectionApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (errorModel.geterrorCode() == null){
                        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    }
                }
            }
        });

        userPermissionApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                try {
                    Log.e("neem", "onChanged: " + baseApiResponseModel);
                } catch (Exception e) {
                    Log.e("neem", "Success: ");
                }
            }
        });

        valueBaseCareViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()){
                        switch (rpmbhiccm.getId()){
                            case R.id.permission_bhi:
                                userDetail.setShow_bhi(rpmbhiccmValue);
                                rpmbhiccm = null;
                                break;
                            case R.id.permission_ccm:
                                userDetail.setShow_ccm(rpmbhiccmValue);
                                rpmbhiccm = null;
                                break;
                            case R.id.permission_rpm:
                                userDetail.setShow_rpm(rpmbhiccmValue);
                                rpmbhiccm = null;
                                break;
                        }
                    }
                }
            }
        });

        valueBaseCareViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                Log.d("TAG", "onChanged: "+errorModel.getMessage());
            }
        });


        disconnectAssociationApiViewModel = new ViewModelProvider(this).get(DisconnectAssociationApiViewModel.class);
        attachObserverInterface.attachObserver(disconnectAssociationApiViewModel);
        disconnectAssociationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null && baseApiResponseModel.isSuccess()) {
                    Utils.showAlertDialog(getActivity(), getString(R.string.success), getString(R.string.association_deleted), getString(R.string.yes),
                            null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    onCloseActionInterface.onClose(true);
                                }
                            }, null);
                }
            }
        });

        connectionListApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof DesignationResponseModel) {
                        designationResponseModel = (DesignationResponseModel) baseApiResponseModel;
                        if (designationResponseModel.isSuccess() && designationResponseModel.getResult() != null) {
                            designationList = designationResponseModel.getResult();
                        }
                    }
                }
            }
        });

    }

    private void setUpPermissionUI() {
        userPermissionAdapter = new UserPermissionAdapter(getActivity(), this);
        rvRootPermission.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRootPermission.setAdapter(userPermissionAdapter);
        if (userDetail.getPermissions() != null && userDetail.getPermissions().size() > 0) {
            permissionList = userDetail.getPermissions();
            userPermissionAdapter.setAdapterData(permissionList);
            clPermission.setVisibility(View.VISIBLE);
        } else {
            clPermission.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientDetailView = (LinearLayout) view.findViewById(R.id.patient_detail_view);
        medicalHistoryBtn = (CardView) view.findViewById(R.id.medical_history_btn);
        insuranceCv = (CardView) view.findViewById(R.id.insurance_cv);
        insuranceDetailTv = (TextView) view.findViewById(R.id.insurance_detail_tv);
        insuranceViewPager = (ViewPager) view.findViewById(R.id.insurance_viewPager);
        insurancePagerIndicator = (LinearLayout) view.findViewById(R.id.insurance_pager_indicator);
        emailCv = (CardView) view.findViewById(R.id.email_cv);
        patientEmailTv = (TextView) view.findViewById(R.id.patient_email_tv);
        doctorDetailView = (LinearLayout) view.findViewById(R.id.doctor_detail_view);
        licenseHintTv = (TextView) view.findViewById(R.id.license_hint_tv);
        npiHintTv = (TextView) view.findViewById(R.id.npi_hint_tv);
        licenseTv = (TextView) view.findViewById(R.id.license_tv);
        npiTv = (TextView) view.findViewById(R.id.npi_tv);
        doctorBioTv = (TextView) view.findViewById(R.id.doctor_bio_tv);
        moreLessTv = (TextView) view.findViewById(R.id.more_less_tv);
        clinicCv = (CardView) view.findViewById(R.id.clinic_cv);
        clinicAddressTv = (TextView) view.findViewById(R.id.clinic_address_tv);
        phoneCv = (CardView) view.findViewById(R.id.phone_cv);
        designationCv = (CardView) view.findViewById(R.id.designation_cv);
        designationTv = (TextView) view.findViewById(R.id.designation_tv);
        userPhoneTv = (TextView) view.findViewById(R.id.user_phone_tv);
        rvRootPermission = (RecyclerView) view.findViewById(R.id.rv_root_permission);
        clPermission = (ConstraintLayout) view.findViewById(R.id.cl_permission);
        disconnectTv = (TextView) view.findViewById(R.id.disconnect_tv);
        insuranceCashTv = (TextView) view.findViewById(R.id.insurance_cash_tv);
        insuranceImageLl = (LinearLayout) view.findViewById(R.id.insurance_image_ll);
        clVitalHistory = (ConstraintLayout) view.findViewById(R.id.cl_vital_history);
        rvVitalHistory = (RecyclerView) view.findViewById(R.id.rv_vital_history);
        tvRpmStatus = (TextView) view.findViewById(R.id.tv_rpm_status);
        tvVitalEdit = (TextView) view.findViewById(R.id.tv_vital_edit);
        clHistory = (ConstraintLayout) view.findViewById(R.id.cl_history);
        rvHistory = (RecyclerView) view.findViewById(R.id.rv_history);

        doctorDetailCl = (ConstraintLayout) view.findViewById(R.id.doctor_detail_cl);
        indianDocDetailCl = (ConstraintLayout) view.findViewById(R.id.indian_doc_detail_cl);
        registrationNumberTv = (TextView) view.findViewById(R.id.registration_number_tv);
        yearOfRegistrationTv = (TextView) view.findViewById(R.id.year_of_registration_tv);
        mciTv = (TextView) view.findViewById(R.id.mci_tv);
        websiteCv = (CardView) view.findViewById(R.id.website_cv);
        websiteTv = (TextView) view.findViewById(R.id.website_tv);

        permissionrpm = (Switch) view.findViewById(R.id.permission_rpm);
        permissionccm = (Switch) view.findViewById(R.id.permission_ccm);
        permissionbhi = (Switch) view.findViewById(R.id.permission_bhi);

        valuebasesummarypermission = (CardView) view.findViewById(R.id.ll_valuebasesummarypermission);

        if (getArguments() != null) {
            userDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);

            if (doctorDetail != null) {
                doctorGuid = doctorDetail.getUser_guid();
            }

            view_type = getArguments().getString(Constants.VIEW_TYPE);

            if (userDetail == null) {
                userDetail = doctorDetail;
            }

            if (view_type.equals(Constants.VIEW_CONNECTION)) {
                disconnectTv.setVisibility(View.GONE);
            } else if (view_type.equals(Constants.VIEW_ASSOCIATION_DETAIL)) {
                disconnectTv.setVisibility(View.VISIBLE);
            }

            rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
            historyAdapter = new AboutHistoryAdapter(getActivity());
            rvHistory.setAdapter(historyAdapter);

            rvVitalHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
            vitalHistoryAdapter = new AboutHistoryAdapter(getActivity());
            rvVitalHistory.setAdapter(vitalHistoryAdapter);

            if (UserDetailPreferenceManager.getWhoAmIResponse().getRole().equals(Constants.ROLE_DOCTOR)){
                if (userDetail.getRole().equals(Constants.ROLE_PATIENT)){
                    valuebasesummarypermission.setVisibility(View.VISIBLE);
                    permissionrpm.setChecked(userDetail.getShow_rpm());
                    permissionccm.setChecked(userDetail.getShow_ccm());
                    permissionbhi.setChecked(userDetail.getShow_bhi());

                    permissionrpm.setOnCheckedChangeListener(multiListener);
                    permissionccm.setOnCheckedChangeListener(multiListener);
                    permissionbhi.setOnCheckedChangeListener(multiListener);

                }else {
                    valuebasesummarypermission.setVisibility(View.GONE);
                }
            }else {
                valuebasesummarypermission.setVisibility(View.GONE);
            }

            if (userDetail != null) {
                switch (userDetail.getRole()) {
                    case Constants.ROLE_DOCTOR:
                        doctorDetailView.setVisibility(View.VISIBLE);
                        patientDetailView.setVisibility(View.GONE);
                        phoneCv.setVisibility(View.GONE);
                        clPermission.setVisibility(View.GONE);
                        clVitalHistory.setVisibility(View.GONE);
                        clHistory.setVisibility(View.GONE);

                        if (userDetail.getUser_detail() != null &&
                                userDetail.getUser_detail().getData() != null) {

                            AppConfig appConfig = new AppConfig(getActivity());
                            if (!appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_NPI)) {

                                npiTv.setText(userDetail.getUser_detail().getData().getNpi());
                                doctorBioTv.setText(userDetail.getUser_detail().getData().getBio());

                                if (userDetail.getUser_detail().getData().getLicenses() != null) {

                                    StringBuilder license = new StringBuilder();
                                    for (int i = 0; i < userDetail.getUser_detail().getData().getLicenses().size(); i++) {
                                        license.append(userDetail.getUser_detail().getData().getLicenses().get(i).getState())
                                                .append(" ")
                                                .append(userDetail.getUser_detail().getData().getLicenses().get(i).getNumber())
                                                .append("\n");
                                    }
                                    licenseTv.setText(license.toString());
                                }

                            } else if (userDetail.getUser_detail().getData().getOtherInformation() != null) {
                                doctorDetailCl.setVisibility(View.GONE);
                                indianDocDetailCl.setVisibility(View.VISIBLE);

                                registrationNumberTv.setText(userDetail.getUser_detail().getData().getOtherInformation().getRegistrationNumber());
                                yearOfRegistrationTv.setText(Utils.getDayMonthYear(userDetail.getUser_detail().getData().getOtherInformation().getYearOfRegistration()));
                                mciTv.setText(userDetail.getUser_detail().getData().getOtherInformation().getMci());
                            }

                            if (userDetail.getUser_detail().getData().getPractices().size() > 0) {

                                String clinicAddress = userDetail.getUser_detail().getData().getPracticeAddress(userDetail.getUser_detail().getData().getPractices().get(0));

                                clinicCv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String lat = String.valueOf(userDetail.getUser_detail().getData().getPractices().get(0).getVisit_address().getLat());
                                        String lon = String.valueOf(userDetail.getUser_detail().getData().getPractices().get(0).getVisit_address().getLon());
                                        String uriString = String.format("geo:%s,%s?q=%s", lat, lon, clinicAddress);
                                        Uri uri = Uri.parse(uriString);
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                            startActivity(mapIntent);
                                        }
                                    }
                                });


                                clinicAddressTv.setText(clinicAddress);
                            }

                            if (userDetail.getUser_detail().getData().getWebsite() != null && !userDetail.getUser_detail().getData().getWebsite().isEmpty()) {
                                websiteCv.setVisibility(View.VISIBLE);
                                websiteTv.setText(userDetail.getUser_detail().getData().getWebsite());
                            }
                        }

                        moreLessTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (moreLessTv.getText().toString().equals(getString(R.string.more))) {

                                    moreLessTv.setText(getString(R.string.less));
                                    doctorBioTv.setEllipsize(null);
                                    doctorBioTv.setMaxLines(Integer.MAX_VALUE);
                                } else {
                                    moreLessTv.setText(getString(R.string.more));
                                    doctorBioTv.setEllipsize(TextUtils.TruncateAt.END);
                                    doctorBioTv.setMaxLines(4);
                                }
                            }
                        });

                        view_type = getArguments().getString(Constants.VIEW_TYPE);
                        setDisconnectTv(view_type);

                        break;
                    case Constants.ROLE_PATIENT:
                    case Constants.ROLE_ASSISTANT:
                        manageVitalHistory();
                        doctorDetailView.setVisibility(View.GONE);
                        patientDetailView.setVisibility(View.VISIBLE);
                        if (userDetail.getRole().equals(Constants.ROLE_ASSISTANT)) {
                            clPermission.setVisibility(View.VISIBLE); // Physician Can Assign permission to Patient as well as assistant
                            if (userDetail.getConnection_status() != null){
                                designationCv.setVisibility(View.VISIBLE);
                            }else {
                                designationCv.setVisibility(View.GONE);
                            }
                            setUpPermissionUI();
                        } else {
                            clPermission.setVisibility(View.GONE);
                            designationCv.setVisibility(View.GONE);
                        }
                        if (view_type.equals(Constants.VIEW_CONNECTION)) {
                            medicalHistoryBtn.setVisibility(View.GONE);
                        } else if (view_type.equals(Constants.VIEW_ASSOCIATION_DETAIL)) {
                            medicalHistoryBtn.setVisibility(View.VISIBLE);
                        }

                        patientEmailTv.setText(userDetail.getEmail());
                        connectionListApiViewModel.getDesignationList();
                        if (userDetail.getUser_detail().getData().getTitle() != null) {
                            designationTv.setText(userDetail.getDesignation());
                            designationTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    selectDesignation(view, userDetail);
                                }
                            });
                        }

                        List<String> insuranceImageList = new ArrayList<>();
                        List<String> insuranceLabelList = new ArrayList<>();

                        if (userDetail.getUser_detail() != null &&
                                userDetail.getUser_detail().getData() != null) {
                            if (userDetail.getRole().equals(Constants.ROLE_ASSISTANT)) {
                                if (userDetail.getUser_detail().getData().getCertification() != null) {
                                    insuranceLabelList.add("");
                                    insuranceImageList.add(userDetail.getUser_detail().getData().getCertification());
                                }
                            } else {
                                if (userDetail.getUser_detail().getData().isInsurancePresent()) {
                                    insuranceLabelList.add(getString(R.string.primary_insurance_front));
                                    insuranceLabelList.add(getString(R.string.primary_insurance_back));

                                    insuranceImageList.add(userDetail.getUser_detail().getData().getInsurance_front());
                                    insuranceImageList.add(userDetail.getUser_detail().getData().getInsurance_back());

                                    if (userDetail.getUser_detail().getData().isSecondaryInsurancePresent()) {
                                        insuranceLabelList.add(getString(R.string.secondary_insurance_front));
                                        insuranceLabelList.add(getString(R.string.secondary_insurance_back));

                                        insuranceImageList.add(userDetail.getUser_detail().getData().getSecondary_insurance_front());
                                        insuranceImageList.add(userDetail.getUser_detail().getData().getSecondary_insurance_back());
                                    }
                                }
                            }
                        }

                        if (insuranceImageList.isEmpty()) {
                            insuranceDetailTv.setText(getString(R.string.lbl_payment_method));
                            insuranceImageLl.setVisibility(View.GONE);
                            insuranceCashTv.setVisibility(View.VISIBLE);
                        } else {
                            insuranceDetailTv.setText(getString(R.string.insurance_details));
                            ImagePreviewViewModel imagePreviewViewModel = new ViewModelProvider(getActivity()).get(ImagePreviewViewModel.class);
                            imagePreviewViewModel.setImageList(insuranceImageList);

                            InsuranceViewPagerAdapter insuranceViewPagerAdapter = new InsuranceViewPagerAdapter(getActivity(), insuranceLabelList, new PickerListener() {
                                @Override
                                public void didSelectedItem(int position) {
                                    ImagePreviewDialogFragment imagePreviewDialogFragment = new ImagePreviewDialogFragment();
                                    imagePreviewDialogFragment.show(getActivity().getSupportFragmentManager(), ImagePreviewDialogFragment.class.getSimpleName());
                                }

                                @Override
                                public void didCancelled() {

                                }
                            });

                            insuranceViewPagerAdapter.setImageList(insuranceImageList);
                            insuranceViewPager.setAdapter(insuranceViewPagerAdapter);
                            insuranceViewPager.setCurrentItem(0, true);
                            insuranceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int i, float v, int i1) {

                                }

                                @Override
                                public void onPageSelected(int i) {
                                    for (int j = 0; j < insuranceImageList.size(); j++) {
                                        indicators[j].setImageDrawable(getActivity().getDrawable(R.drawable.circular_unselected_indicator));
                                    }
                                    indicators[i].setImageDrawable(getActivity().getDrawable(R.drawable.circular_selected_indicator));
                                }

                                @Override
                                public void onPageScrollStateChanged(int i) {

                                }
                            });

                            if (insuranceImageList.size() > 1)
                                createIndicator(insuranceImageList.size());

                            insuranceImageLl.setVisibility(View.VISIBLE);
                            insuranceCashTv.setVisibility(View.GONE);
                        }

                        if (userDetail.getRole().equals(Constants.ROLE_ASSISTANT)) {
                            insuranceDetailTv.setText(getString(R.string.certificate));
                            medicalHistoryBtn.setVisibility(View.GONE);
                            onStatusUpdate(userDetail.getConnection_status());
                        }
                        break;
                }
            }

            medicalHistoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment;

                    if (UserType.isUserAssistant() || (userDetail.getQuestionnaire() != null && userDetail.getQuestionnaire().isQuestionariesEmpty())) {
                        fragment = new MedicalHistoryViewFragment();
                    } else {
                        fragment = new MedicalHistoryList();
                    }
                    fragment.setArguments(getArguments());
                    showSubFragmentInterface.onShowFragment(fragment);
                }
            });
            tvVitalEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRemotePatientMonitoring(userDetail.getUser_guid());
                }
            });

            disconnectTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showAlertDialog(getActivity(), getString(R.string.delete_connection),
                            getString(R.string.disassoctiate_this_connection), getString(R.string.yes),
                            getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    disconnectAssociationApiViewModel.disconnectUser(userDetail.getUser_guid(), doctorGuid);
                                    dialog.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
            });

            if (getPhoneNumber() != null) {
                phoneCv.setVisibility(View.VISIBLE);
                userPhoneTv.setText(getPhoneNumber());
                phoneCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserType.isUserPatient()) {
                            Uri uri = Uri.parse("tel:" + getPhoneNumber());
                            startActivity(new Intent(Intent.ACTION_DIAL, uri));
                        } else {
                            String phone_number = getPhoneNumber().replaceAll("\\+1", "*67");
                            Uri uri = Uri.parse("tel:" + phone_number);
                            startActivity(new Intent(Intent.ACTION_DIAL, uri));
                        }
                    }
                });
            } else {
                phoneCv.setVisibility(View.GONE);
            }
        }
    }

    CompoundButton.OnCheckedChangeListener multiListener = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton v, boolean isChecked) {
            HashMap<String, Object> param = new HashMap<>();
            param.put(ArgumentKeys.USER_ID, String.valueOf(userDetail.getUser_id()));
            param.put(ArgumentKeys.buser_id, String.valueOf(UserDetailPreferenceManager.getWhoAmIResponse().getUser_id()));
            rpmbhiccmValue = isChecked;
            switch (v.getId()){
                case R.id.permission_bhi:
                    param.put(ArgumentKeys.show_bhi, isChecked);
                    param.put(ArgumentKeys.show_rpm, userDetail.getShow_rpm());
                    param.put(ArgumentKeys.show_ccm, userDetail.getShow_ccm());
                    break;
                case R.id.permission_ccm:
                    param.put(ArgumentKeys.show_bhi, userDetail.getShow_bhi());
                    param.put(ArgumentKeys.show_rpm, userDetail.getShow_rpm());
                    param.put(ArgumentKeys.show_ccm, isChecked);
                    break;
                case R.id.permission_rpm:
                    param.put(ArgumentKeys.show_bhi, userDetail.getShow_bhi());
                    param.put(ArgumentKeys.show_rpm, isChecked);
                    param.put(ArgumentKeys.show_ccm, userDetail.getShow_ccm());
                    break;
            }
            rpmbhiccm = v;
            valueBaseCareViewModel.changevaluebase(param);
        }
    };

    private void showRemotePatientMonitoring(String user_guid) {
        RemotePatientMonitoringFragment remotePatientMonitoringFragment = new RemotePatientMonitoringFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.USER_GUID, user_guid);
        remotePatientMonitoringFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(remotePatientMonitoringFragment);
    }

    private void manageVitalHistory() {
        if (userDetail.getRole().equals(Constants.ROLE_PATIENT)) {
            if (userDetail.getIs_rpm_enabled()) {
                tvRpmStatus.setText(getString(R.string.str_rpm_status, getString(R.string.str_enable)));
                tvVitalEdit.setVisibility(View.VISIBLE);
            } else {
                tvRpmStatus.setText(getString(R.string.str_rpm_status, getString(R.string.str_disable)));
                tvVitalEdit.setVisibility(View.GONE);
            }

            if (userDetail.getVitals() != null && userDetail.getVitals().size() > 0) {
                vitalHistoryAdapter.setDataAdapter(userDetail.getVitals());
                if (UserType.isUserAssistant()){
                 if (Constants.isVitalsViewEnable){
                     clVitalHistory.setVisibility(View.VISIBLE);
                 }else {
                     clVitalHistory.setVisibility(View.GONE);
                 }
                }else {
                    clVitalHistory.setVisibility(View.VISIBLE);
                }
                rvVitalHistory.setVisibility(View.VISIBLE);
            } else {
                clVitalHistory.setVisibility(View.GONE);
                rvVitalHistory.setVisibility(View.GONE);
            }

            if (userDetail.getHistory() != null && userDetail.getHistory().size() > 0) {
                historyAdapter.setDataAdapter(userDetail.getHistory());
                clHistory.setVisibility(View.VISIBLE);
            } else {
                clHistory.setVisibility(View.GONE);
            }

        } else {
            rvVitalHistory.setVisibility(View.VISIBLE);
            clVitalHistory.setVisibility(View.GONE);
            clHistory.setVisibility(View.GONE);
        }
    }

    private void selectDesignation(View v, CommonUserApiResponseModel apiResponseModelList) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View layoutInflateView = layoutInflater.inflate
                (R.layout.designation_alert, (ViewGroup) v.findViewById(R.id.cl_root));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(layoutInflateView);
        alertDialog.setCancelable(false);
        AlertDialog dialog = alertDialog.create();
        TextView headerTitle = layoutInflateView.findViewById(R.id.header_title);
        RecyclerView rvDesignation = layoutInflateView.findViewById(R.id.rv_designation);
        rvDesignation.setLayoutManager(new LinearLayoutManager(getActivity()));
        Button btnYes = layoutInflateView.findViewById(R.id.btn_yes);
        TextView noRecordFound = layoutInflateView.findViewById(R.id.no_record_found);
        Button btnCancel = layoutInflateView.findViewById(R.id.btn_cancel);
        View viewDevider = layoutInflateView.findViewById(R.id.view_devider);


        headerTitle.setText(String.format(getActivity().getString(R.string.str_select_designation_for), apiResponseModelList.getUserDisplay_name()));

        if (designationList.size() == 0) {
            rvDesignation.setVisibility(View.GONE);
            noRecordFound.setVisibility(View.VISIBLE);
            btnYes.setVisibility(View.GONE);
            viewDevider.setVisibility(View.GONE);
        } else {
            rvDesignation.setVisibility(View.VISIBLE);
            noRecordFound.setVisibility(View.GONE);
            btnYes.setVisibility(View.VISIBLE);
            viewDevider.setVisibility(View.VISIBLE);
        }
        DesignationListAdapter designationListAdapter = new DesignationListAdapter(getActivity(), designationList, new OnItemEndListener() {
            @Override
            public void itemEnd(int position) {

            }
        });
        rvDesignation.setAdapter(designationListAdapter);

        designationListAdapter.setdesignation(apiResponseModelList.getDesignation());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (designationListAdapter != null) {
                    Utils.vibrate(getActivity());
                    String designation = designationListAdapter.getSpecialistInfo();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.ADD_CONNECTION_ID, apiResponseModelList.getUser_id());

                    if (designation != null)
                        bundle.putString(Constants.DESIGNATION, designation);

                    bundle.putString(ArgumentKeys.USER_GUID, apiResponseModelList.getUser_guid());
                    bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                    bundle.putSerializable(Constants.USER_DETAIL, apiResponseModelList);
                    bundle.putBoolean(ArgumentKeys.CHECK_CONNECTION_STATUS, true);
                    bundle.putBoolean(ArgumentKeys.CONNECT_USER, true);

                    onCompletionResult(RequestID.REQ_ADD_CONNECTION, true, bundle);

                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                dialog.dismiss();

            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Nullable
    private String getPhoneNumber() {
        if (userDetail == null) {
            return null;
        }
        try {
            switch (userDetail.getRole()) {
                case Constants.ROLE_DOCTOR:
                    if ((userDetail.getUser_detail() != null) && (userDetail.getUser_detail().getData() != null) && (userDetail.getUser_detail().getData().getPractices() != null) && !((userDetail.getUser_detail().getData().getPractices().isEmpty()))) {
                        if (userDetail.getUser_detail().getData().getPractices().get(0).getOfficePhone() != null &&
                                userDetail.getUser_detail().getData().getPractices().get(0).getOfficePhone().length() > 0) {
                            return userDetail.getUser_detail().getData().getPractices().get(0).getOfficePhone();
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                case Constants.ROLE_PATIENT:
                    return userDetail.getPhone();
                case Constants.ROLE_ASSISTANT:
                    if (UserType.isUserDoctor())
                        return userDetail.getPhone();
                    else
                        return null;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void setDisconnectTv(String view_type) {
        if (view_type != null) {
            if (view_type.equals(Constants.VIEW_CONNECTION)) {
                disconnectTv.setVisibility(View.GONE);
            } else if (view_type.equals(Constants.VIEW_ASSOCIATION_DETAIL)) {
                disconnectTv.setVisibility(View.VISIBLE);
            }
        } else {
            disconnectTv.setVisibility(View.GONE);
        }
    }

    private void createIndicator(int size) {
        indicators = new ImageView[size];

        for (int i = 0; i < size; i++) {
            indicators[i] = new ImageView(getActivity());
            indicators[i].setImageDrawable(getActivity().getDrawable(R.drawable.circular_unselected_indicator));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);

            insurancePagerIndicator.addView(indicators[i], params);
        }

        indicators[0].setImageDrawable(getResources().getDrawable(R.drawable.circular_selected_indicator));

    }

    public void onStatusUpdate(String connection_status) {
        if (connection_status == null || !connection_status.equals(Constants.CONNECTION_STATUS_ACCEPTED)) {
            disconnectTv.setVisibility(View.GONE);
        } else {
            disconnectTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(statusReceiver, new IntentFilter(Constants.CONNECTION_STATUS_RECEIVER));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(statusReceiver);
    }

    @Override
    public void onEventTrigger(Bundle bundle) {
        boolean isFromParent = bundle.getBoolean(ArgumentKeys.IS_FROM_PARENT);
        int parentPos = bundle.getInt(ArgumentKeys.ITEM_CLICK_PARENT_POS);

        if (isFromParent) {
            Boolean isChecked = permissionList.get(parentPos).getValue();
            permissionList.get(parentPos).setValue(!isChecked);
            callUpdatePermissionAPI(permissionList.get(parentPos).getId(), !isChecked);
            if (!isChecked) {
                List<PermissionBean> subPermissionList = permissionList.get(parentPos).getChildren();
                for (int i = 0; i < subPermissionList.size(); i++) {
                    permissionList.get(parentPos).getChildren().get(i).setValue(true);
                }
            }
        } else {
            int childPos = bundle.getInt(ArgumentKeys.ITEM_CLICK_CHILD_POS);
            Boolean isChecked = permissionList.get(parentPos).getChildren().get(childPos).getValue();
            permissionList.get(parentPos).getChildren().get(childPos).setValue(!isChecked);
            callUpdatePermissionAPI(permissionList.get(parentPos).getChildren().get(childPos).getId(), !isChecked);
            if (isChecked) {
                List<PermissionBean> subPermissionList = permissionList.get(parentPos).getChildren();
                boolean isAnyOneEnable = false;
                for (int i = 0; i < subPermissionList.size(); i++) {
                    if (subPermissionList.get(i).getValue()) {
                        isAnyOneEnable = true;
                        i = subPermissionList.size();
                    }
                }
                if (!isAnyOneEnable) {
                    permissionList.get(parentPos).setValue(false);
                    callUpdatePermissionAPI(permissionList.get(parentPos).getId(), false);
                }
            }
        }
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userPermissionAdapter.notifyItemChanged(parentPos);
            }
        });

    }

    //TODO - Update User permission Status
    private void callUpdatePermissionAPI(int permissionId, boolean isEnable) {
        PermissionRequestModel permissionRequestModel = new PermissionRequestModel();
        permissionRequestModel.setGuid(userDetail.getUser_guid());
        permissionRequestModel.setId(permissionId);
        permissionRequestModel.setValue(isEnable);
        userPermissionApiViewModel.updateUserPermission(permissionRequestModel);
    }

    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        if (success) {
            selectedId = bundle.getInt(Constants.ADD_CONNECTION_ID);
            finaldesignation = bundle.getString(Constants.DESIGNATION);
            CommonUserApiResponseModel userModel = (CommonUserApiResponseModel) bundle.getSerializable(Constants.USER_DETAIL);
            String userGuid = null;

            if (userModel != null) {
                userGuid = userModel.getUser_guid();
            }

            bundle = new Bundle();
            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_requesting));
            bundle.putString("Designation","true");

            SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
            successViewDialogFragment.setArguments(bundle);

            successViewDialogFragment.show(getParentFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

            String currentUserGuid=userGuid;
            if(!UserType.isUserAssistant())
                currentUserGuid="";

            addConnectionApiViewModel.updateDesignation(currentUserGuid,userGuid, null, String.valueOf(selectedId), finaldesignation);

        }
    }
}
