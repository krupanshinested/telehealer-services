package com.thealer.telehealer.views.signup.patient;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aswin on 14,October,2018
 */
public class PatientUploadInsuranceFragment extends BaseFragment implements DoCurrentTransactionInterface, CameraInterface, View.OnClickListener {

    private TextView titleTv;
    private LinearLayout firstInsuranceLl;
    private ViewPager firstInsuranceViewPager;
    private LinearLayout firstInsurancePagerIndicator;
    private LinearLayout secondInsuranceLl;
    private ViewPager secondInsuranceViewPager;
    private LinearLayout secondInsurancePagerIndicator;
    private CustomButton addInsuranceBtn;

    private List<String> firstInsuranceLabelList;
    private List<String> secondInsuranceLabelList;
    private String primaryFrontImgPath, primaryBackImgPath, secondaryFrontImgPath, secondaryBackImgPath;
    private int currentScreenType = Constants.forRegistration;
    private boolean isPrimaryImage = true, isFrontImage = true;
    private InsuranceViewPagerAdapter secondaryInsuranceAdapter, primaryInsuranceAdapter;

    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private boolean isPrimaryDeleted, isSecondaryDeleted;
    private CreateUserRequestModel createUserRequestModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_upload_insurance, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        titleTv = view.findViewById(R.id.textView5);
        firstInsuranceLl = (LinearLayout) view.findViewById(R.id.first_insurance_ll);
        firstInsuranceViewPager = (ViewPager) view.findViewById(R.id.first_insurance_viewPager);
        firstInsurancePagerIndicator = (LinearLayout) view.findViewById(R.id.first_insurance_pager_indicator);
        secondInsuranceLl = (LinearLayout) view.findViewById(R.id.second_insurance_ll);
        secondInsuranceViewPager = (ViewPager) view.findViewById(R.id.second_insurance_viewPager);
        secondInsurancePagerIndicator = (LinearLayout) view.findViewById(R.id.second_insurance_pager_indicator);
        addInsuranceBtn = (CustomButton) view.findViewById(R.id.add_insurance_btn);

        addInsuranceBtn.setOnClickListener(this);

        firstInsuranceLabelList = Arrays.asList(getString(R.string.primary_insurance_front), getString(R.string.primary_insurance_back));
        secondInsuranceLabelList = Arrays.asList(getString(R.string.secondary_insurance_front), getString(R.string.secondary_insurance_back));

        setUpPrimaryInsuranceViewPager();
        setUpSecondaryInsuranceViewPager();

        if (getArguments() != null) {
            currentScreenType = getArguments().getInt(ArgumentKeys.SCREEN_TYPE, Constants.forRegistration);


            primaryFrontImgPath = getArguments().getString(ArgumentKeys.INSURANCE_FRONT);
            primaryBackImgPath = getArguments().getString(ArgumentKeys.INSURANCE_BACK);
            secondaryFrontImgPath = getArguments().getString(ArgumentKeys.SECONDARY_INSURANCE_FRONT);
            secondaryBackImgPath = getArguments().getString(ArgumentKeys.SECONDARY_INSURANCE_BACK);

        }

        if (currentScreenType == Constants.forRegistration) {
            createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);

            primaryFrontImgPath = createUserRequestModel.getInsurance_front_path();
            primaryBackImgPath = createUserRequestModel.getInsurance_back_path();
            secondaryFrontImgPath = createUserRequestModel.getSecondary_insurance_front_path();
            secondaryBackImgPath = createUserRequestModel.getSecondary_insurance_back_path();

        }

        if (primaryBackImgPath != null) {
            primaryInsuranceAdapter.setData(new ArrayList<>(Arrays.asList(primaryFrontImgPath, primaryBackImgPath)), firstInsuranceLabelList, true);
        }

        if (secondaryFrontImgPath != null) {
            secondaryInsuranceAdapter.setData(new ArrayList<>(Arrays.asList(secondaryFrontImgPath, secondaryBackImgPath)), secondInsuranceLabelList, true);
            addInsuranceBtn.setVisibility(View.GONE);
            secondInsuranceLl.setVisibility(View.VISIBLE);

        }

        if (currentScreenType == Constants.forProfileUpdate) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setVisibility(View.VISIBLE);
        }

    }

    private void setUpSecondaryInsuranceViewPager() {
        secondaryInsuranceAdapter = new InsuranceViewPagerAdapter(getActivity(), secondInsuranceLabelList, new PickerListener() {
            @Override
            public void didSelectedItem(int position) {
                isPrimaryImage = false;
                isFrontImage = position == 0;
                showOptionSelection();
            }

            @Override
            public void didCancelled() {

            }
        });
        secondInsuranceViewPager.setAdapter(secondaryInsuranceAdapter);
        secondInsuranceViewPager.setCurrentItem(0);
        secondInsuranceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                createSecondaryIndicators(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        createSecondaryIndicators(0);
    }

    private void setUpPrimaryInsuranceViewPager() {
        primaryInsuranceAdapter = new InsuranceViewPagerAdapter(getActivity(), firstInsuranceLabelList, new PickerListener() {
            @Override
            public void didSelectedItem(int position) {
                isPrimaryImage = true;
                isFrontImage = position == 0;
                showOptionSelection();
            }

            @Override
            public void didCancelled() {
            }
        });
        firstInsuranceViewPager.setAdapter(primaryInsuranceAdapter);
        firstInsuranceViewPager.setCurrentItem(0);
        firstInsuranceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                createPrimaryIndicators(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        createPrimaryIndicators(0);
    }

    private void createSecondaryIndicators(int position) {
        ImageView[] indicators = new ImageView[secondInsuranceLabelList.size()];

        secondInsurancePagerIndicator.removeAllViewsInLayout();

        for (int i = 0; i < secondInsuranceLabelList.size(); i++) {
            indicators[i] = new ImageView(getActivity());
            indicators[i].setImageDrawable(getActivity().getDrawable(R.drawable.circular_unselected_indicator));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);

            secondInsurancePagerIndicator.addView(indicators[i], params);
        }
        indicators[position].setImageDrawable(getActivity().getDrawable(R.drawable.circular_selected_indicator));

    }

    private void createPrimaryIndicators(int position) {
        ImageView[] indicators = new ImageView[firstInsuranceLabelList.size()];

        firstInsurancePagerIndicator.removeAllViewsInLayout();

        for (int i = 0; i < firstInsuranceLabelList.size(); i++) {
            indicators[i] = new ImageView(getActivity());
            indicators[i].setImageDrawable(getActivity().getDrawable(R.drawable.circular_unselected_indicator));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);

            firstInsurancePagerIndicator.addView(indicators[i], params);
        }
        indicators[position].setImageDrawable(getActivity().getDrawable(R.drawable.circular_selected_indicator));
    }

    private void checkImagesAdded() {
        if (primaryFrontImgPath != null && primaryBackImgPath != null) {
            onViewChangeInterface.enableNext(true);

            if ((secondaryFrontImgPath != null && secondaryBackImgPath == null) ||
                    (secondaryFrontImgPath == null && secondaryBackImgPath != null)) {
                onViewChangeInterface.enableNext(false);
            }

        } else {
            onViewChangeInterface.enableNext(false);
        }
    }

    @Override
    public void doCurrentTransaction() {
        switch (currentScreenType) {
            case Constants.forRegistration:
                createUserRequestModel.setInsurance_front_path(primaryFrontImgPath);
                createUserRequestModel.setInsurance_back_path(primaryBackImgPath);
                createUserRequestModel.setSecondary_insurance_front_path(secondaryFrontImgPath);
                createUserRequestModel.setSecondary_insurance_back_path(secondaryBackImgPath);

                onActionCompleteInterface.onCompletionResult(null, true, null);
                break;
            case Constants.forProfileUpdate:
                Bundle bundle = new Bundle();
                bundle.putBoolean(ArgumentKeys.CASH_SELECTED, false);
                bundle.putString(ArgumentKeys.INSURANCE_FRONT, primaryFrontImgPath);
                bundle.putString(ArgumentKeys.INSURANCE_BACK, primaryBackImgPath);
                bundle.putString(ArgumentKeys.SECONDARY_INSURANCE_FRONT, secondaryFrontImgPath);
                bundle.putString(ArgumentKeys.SECONDARY_INSURANCE_BACK, secondaryBackImgPath);
                bundle.putBoolean(ArgumentKeys.IS_PRIMARY_DELETED, isPrimaryDeleted);
                bundle.putBoolean(ArgumentKeys.IS_SECONDARY_DELETED, isSecondaryDeleted);

                onActionCompleteInterface.onCompletionResult(RequestID.INSURANCE_CHANGE_RESULT, true, bundle);
                break;
        }
    }

    @Override
    public void onImageReceived(String imagePath) {
        if (isPrimaryImage) {
            isPrimaryDeleted = false;
            if (isFrontImage) {
                primaryFrontImgPath = imagePath;
                primaryInsuranceAdapter.setFrontImgPath(primaryFrontImgPath);
                if (primaryBackImgPath == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firstInsuranceViewPager.setCurrentItem(1, true);

                        }
                    }, 400);
                }
            } else {
                primaryBackImgPath = imagePath;
                primaryInsuranceAdapter.setBackImgPath(primaryBackImgPath);
                if (primaryFrontImgPath == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firstInsuranceViewPager.setCurrentItem(0, true);
                        }
                    }, 400);
                }
            }
        } else {
            addInsuranceBtn.setVisibility(View.GONE);
            secondInsuranceLl.setVisibility(View.VISIBLE);
            isSecondaryDeleted = false;
            if (isFrontImage) {
                secondaryFrontImgPath = imagePath;
                secondaryInsuranceAdapter.setFrontImgPath(secondaryFrontImgPath);

                if (secondaryBackImgPath == null) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            secondInsuranceViewPager.setCurrentItem(1, true);

                        }
                    }, 400);
                }
            } else {
                secondaryBackImgPath = imagePath;
                secondaryInsuranceAdapter.setBackImgPath(secondaryBackImgPath);
                if (secondaryFrontImgPath == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            secondInsuranceViewPager.setCurrentItem(0, true);
                        }
                    }, 400);
                }
            }
        }
        checkImagesAdded();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_insurance_btn:
                if (primaryFrontImgPath == null || primaryBackImgPath == null) {
                    Utils.showAlertDialog(getActivity(), getString(R.string.error),
                            getString(R.string.insurance_alert_error),
                            getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, null);
                } else {
                    isPrimaryImage = false;
                    isFrontImage = true;
                    showOptionSelection();
                }
                break;
        }
    }

    private void showOptionSelection() {
        List<String> optionsList = new ArrayList<>(Arrays.asList(getString(R.string.camera), getString(R.string.gallery)));

        if (isPrimaryImage) {
            if (primaryFrontImgPath != null || primaryBackImgPath != null) {
                optionsList.add(getString(R.string.Delete));
            }
        } else {
            if (secondaryFrontImgPath != null || secondaryBackImgPath != null) {
                optionsList.add(getString(R.string.Delete));
            }
        }

        Utils.showOptionsSelectionAlert(getActivity(), optionsList, new PickerListener() {
            @Override
            public void didSelectedItem(int position) {

                if (optionsList.get(position).equals(getString(R.string.camera))) {
                    CameraUtil.openCamera(getActivity());
                } else if (optionsList.get(position).equals(getString(R.string.gallery))) {
                    CameraUtil.openGallery(getActivity());
                } else if (optionsList.get(position).equals(getString(R.string.Delete))) {

                    if (isPrimaryImage) {
                        deletePrimaryImages();
                        deleteSecondaryImages();
                    } else {
                        deleteSecondaryImages();
                    }

                    checkImagesAdded();
                }
            }

            @Override
            public void didCancelled() {

            }
        });
    }

    private void deleteSecondaryImages() {
        secondaryFrontImgPath = null;
        secondaryBackImgPath = null;
        secondaryInsuranceAdapter.deleteImages();
        isSecondaryDeleted = true;
    }

    private void deletePrimaryImages() {
        primaryFrontImgPath = null;
        primaryBackImgPath = null;
        primaryInsuranceAdapter.deleteImages();
        isPrimaryDeleted = true;

        secondInsuranceLl.setVisibility(View.GONE);
        addInsuranceBtn.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();

        onViewChangeInterface.hideOrShowNext(true);

        onViewChangeInterface.updateNextTitle(getString(R.string.next));
        if (currentScreenType == Constants.forProfileUpdate) {
            onViewChangeInterface.updateTitle(getString(R.string.insurance_details));
        }

        checkImagesAdded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentScreenType == Constants.forRegistration && createUserRequestModel != null) {
            createUserRequestModel.setInsurance_front_path(null);
            createUserRequestModel.setInsurance_back_path(null);
            createUserRequestModel.setSecondary_insurance_front_path(null);
            createUserRequestModel.setSecondary_insurance_back_path(null);
        }
    }
}
