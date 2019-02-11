package com.thealer.telehealer.views.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationDetail.DisconnectAssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.onboarding.OnBoardingViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 14,November,2018
 */
public class AboutFragment extends BaseFragment {
    private LinearLayout patientDetailView;
    private CustomButton medicalHistoryBtn;
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
    private TextView disconnectTv;

    private CommonUserApiResponseModel userDetail;
    private int userType;
    private ImageView[] indicators;
    private TextView insuranceCashTv;
    private LinearLayout insuranceImageLl;
    private DisconnectAssociationApiViewModel disconnectAssociationApiViewModel;
    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();

        disconnectAssociationApiViewModel = ViewModelProviders.of(this).get(DisconnectAssociationApiViewModel.class);
        attachObserverInterface.attachObserver(disconnectAssociationApiViewModel);

        disconnectAssociationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null && baseApiResponseModel.isSuccess()) {
                    dialog = Utils.showAlertDialog(getActivity(), getString(R.string.success), getString(R.string.association_deleted))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    onCloseActionInterface.onClose(true);
                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        });
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
        medicalHistoryBtn = (CustomButton) view.findViewById(R.id.medical_history_btn);
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
        userPhoneTv = (TextView) view.findViewById(R.id.user_phone_tv);
        disconnectTv = (TextView) view.findViewById(R.id.disconnect_tv);
        insuranceCashTv = (TextView) view.findViewById(R.id.insurance_cash_tv);
        insuranceImageLl = (LinearLayout) view.findViewById(R.id.insurance_image_ll);

        if (getArguments() != null) {
            userDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);

            userType = appPreference.getInt(Constants.USER_TYPE);

            if (userType == Constants.TYPE_PATIENT) {
                doctorDetailView.setVisibility(View.VISIBLE);
                patientDetailView.setVisibility(View.GONE);

                if (userDetail.getUser_detail() != null &&
                        userDetail.getUser_detail().getData() != null) {

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

                    StringBuilder clinicAddress = new StringBuilder();
                    if (userDetail.getUser_detail().getData().getPractices().size() > 0) {
                        clinicAddress.append(userDetail.getUser_detail().getData().getPractices().get(0).getVisit_address().getStreet())
                                .append(",")
                                .append(userDetail.getUser_detail().getData().getPractices().get(0).getVisit_address().getStreet2())
                                .append(",")
                                .append(userDetail.getUser_detail().getData().getPractices().get(0).getVisit_address().getCity())
                                .append(",")
                                .append(userDetail.getUser_detail().getData().getPractices().get(0).getVisit_address().getState())
                                .append(",")
                                .append(userDetail.getUser_detail().getData().getPractices().get(0).getVisit_address().getZip());
                    }
                    clinicAddressTv.setText(clinicAddress);

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

                String view_type = getArguments().getString(Constants.VIEW_TYPE);
                setDisconnectTv(view_type);

            } else if (userType == Constants.TYPE_DOCTOR) {
                patientDetailView.setVisibility(View.VISIBLE);
                doctorDetailView.setVisibility(View.GONE);

                patientEmailTv.setText(userDetail.getEmail());

                List<String> insuranceImageList = new ArrayList<>();

                if (userDetail.getUser_detail() != null &&
                        userDetail.getUser_detail().getData() != null) {


                    if (userDetail.getRole().equals(Constants.ROLE_ASSISTANT)) {
                        insuranceImageList.add(userDetail.getUser_detail().getData().getCertification());
                    } else {
                        insuranceImageList.add(userDetail.getUser_detail().getData().getInsurance_front());
                        insuranceImageList.add(userDetail.getUser_detail().getData().getInsurance_back());
                    }

                    OnBoardingViewPagerAdapter onBoardingViewPagerAdapter = new OnBoardingViewPagerAdapter(getActivity(), insuranceImageList, true);

                    insuranceViewPager.setAdapter(onBoardingViewPagerAdapter);
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

                    createIndicator(insuranceImageList.size());

                    insuranceImageLl.setVisibility(View.VISIBLE);
                    insuranceCashTv.setVisibility(View.GONE);

                } else {
                    insuranceImageLl.setVisibility(View.GONE);
                    insuranceCashTv.setVisibility(View.VISIBLE);
                }

                if (userDetail.getRole().equals(Constants.ROLE_ASSISTANT)) {
                    medicalHistoryBtn.setVisibility(View.GONE);
                    if (userDetail.getConnection_status() == null || !userDetail.getConnection_status().equals(Constants.CONNECTION_STATUS_ACCEPTED)) {
                        disconnectTv.setVisibility(View.GONE);
                    } else {
                        disconnectTv.setVisibility(View.VISIBLE);
                    }
                }
            }

            disconnectTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = Utils.showAlertDialog(getActivity(), getString(R.string.delete_connection),
                            getString(R.string.disassoctiate_this_connection))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    disconnectAssociationApiViewModel.disconnectUser(userDetail.getUser_guid());
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            });

            userPhoneTv.setText(userDetail.getPhone());
        }
    }

    private void setDisconnectTv(String view_type) {
        if (view_type != null) {
            if (view_type.equals(Constants.VIEW_CONNECTION)) {
                disconnectTv.setVisibility(View.GONE);
            } else if (view_type.equals(Constants.VIEW_ASSOCIATION_DETAIL)) {
                disconnectTv.setVisibility(View.VISIBLE);
            }
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

}
