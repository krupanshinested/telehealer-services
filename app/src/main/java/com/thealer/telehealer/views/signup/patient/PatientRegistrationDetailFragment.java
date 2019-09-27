package com.thealer.telehealer.views.signup.patient;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.UpdateProfile.UpdateProfileModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserDetailBean;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.DatePickerDialogFragment;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.DateBroadcastReceiver;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.settings.Interface.BundleReceiver;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thealer.telehealer.common.Constants.TYPE_DOB;

/**
 * Created by Aswin on 11,October,2018
 */

public class PatientRegistrationDetailFragment extends BaseFragment implements
        View.OnFocusChangeListener, View.OnClickListener,
        DoCurrentTransactionInterface, CameraInterface, BundleReceiver, OnViewChangeInterface {

    private CircleImageView profileCiv;
    private EditText firstnameEt;
    private EditText lastnameEt;
    private EditText dobEt;
    private Spinner genderSp;
    private TextView title_tv;

    //not for Registration
    private LinearLayout insurance_lay;
    private TextView cash_tv, gender_value;

    private String[] genderList;
    private Bitmap profileImg = null;
    private String profileImgPath;
    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnCloseActionInterface onCloseActionInterface;

    private DateBroadcastReceiver dateBroadcastReceiver = new DateBroadcastReceiver() {
        @Override
        public void onDateReceived(String formatedDate) {
            dobEt.setText(formatedDate);
        }
    };

    private UpdateProfileModel updateProfileModel;
    private WhoAmIApiViewModel whoAmIApiViewModel;
    private WhoAmIApiResponseModel whoAmi;
    private CreateUserRequestModel createUserRequestModel;

    private int isCashSelected = -1;
    private int currentDisplayType = Constants.CREATE_MODE;
    private ViewPager insuranceViewPager;
    private List<String> insuranceLabelList = new ArrayList<>();
    private InsuranceViewPagerAdapter insuranceViewPagerAdapter;

    private AttachObserverInterface attachObserverInterface;
    private boolean isPrimaryDeleted = false, isSecondaryDeleted = false, updateProfile = false;
    private LinearLayout pagerIndicator;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();

        createUserRequestModel = new ViewModelProvider(this).get(CreateUserRequestModel.class);
        updateProfileModel = new ViewModelProvider(this).get(UpdateProfileModel.class);
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);

        attachObserverInterface.attachObserver(updateProfileModel);
        attachObserverInterface.attachObserver(whoAmIApiViewModel);

        updateProfileModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null) {

                    if (currentDisplayType == Constants.SCHEDULE_CREATION_MODE) {
                        if (updateProfile) {
                            updateProfile = false;
                            updateProfileModel.updatePatient(createUserRequestModel, null);
                        } else {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }
                    } else {
                        whoAmIApiViewModel.checkWhoAmI();

                        onViewChangeInterface.enableNext(true);
                        enableNext(true);
                        currentDisplayType = Constants.VIEW_MODE;
                        reloadUI();

                        updatedProfile();
                        isPrimaryDeleted = false;
                        isSecondaryDeleted = false;
                    }
                }

            }
        });

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                            whoAmi = whoAmIApiResponseModel;
                            UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);
                            updateUI(whoAmIApiResponseModel);

                            if (profileImgPath != null) {
                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(getString(R.string.profile_picture_updated)));
                            }

                            onViewChangeInterface.updateTitle(UserDetailPreferenceManager.getUserDisplayName());
                            updateTitle(UserDetailPreferenceManager.getUserDisplayName());
                        }
                    }
                });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_detail, container, false);

        initView(view);

        onViewChangeInterface.hideOrShowNext(true);
        onViewChangeInterface.enableNext(false);

        hideOrShowNext(true);
        enableNext(false);

        checkAllFields();

        switch (currentDisplayType) {
            case Constants.VIEW_MODE:
            case Constants.SCHEDULE_CREATION_MODE:
                whoAmi = UserDetailPreferenceManager.getWhoAmIResponse();
                if (whoAmi != null) {
                    updateUI(whoAmi);
                } else {
                    whoAmIApiViewModel.checkWhoAmI();
                }
                break;
            case Constants.EDIT_MODE:
                updateUI(createUserRequestModel);
                break;
        }

        if (savedInstanceState != null) {
            isCashSelected = savedInstanceState.getInt("isCashSelected", -1);
        }

        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        profileCiv = (CircleImageView) view.findViewById(R.id.profile_civ);
        firstnameEt = (EditText) view.findViewById(R.id.firstname_et);
        lastnameEt = (EditText) view.findViewById(R.id.lastname_et);
        dobEt = (EditText) view.findViewById(R.id.dob_et);
        genderSp = view.findViewById(R.id.gender_sp);
        gender_value = view.findViewById(R.id.gender_value);
        title_tv = view.findViewById(R.id.title_tv);
        insurance_lay = view.findViewById(R.id.insurance_lay);
        cash_tv = view.findViewById(R.id.cash_tv);
        insuranceViewPager = (ViewPager) view.findViewById(R.id.insurance_viewPager);
        pagerIndicator = (LinearLayout) view.findViewById(R.id.pager_indicator);

        setupViewPagerAdapter();

        insurance_lay.setOnClickListener(this);
        insuranceViewPager.setOnClickListener(this);
        cash_tv.setOnClickListener(this);

        dobEt.setOnFocusChangeListener(this);

        genderList = getResources().getStringArray(R.array.gender_list);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, genderList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSp.setAdapter(arrayAdapter);

        addTextWatcher(firstnameEt);
        addTextWatcher(lastnameEt);
        addTextWatcher(dobEt);

        profileCiv.setOnClickListener(this);

        if (getArguments() != null) {
            currentDisplayType = getArguments().getInt(ArgumentKeys.VIEW_TYPE);

            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR, false)) {
                appbarLayout.setVisibility(View.VISIBLE);
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCloseActionInterface.onClose(false);
                    }
                });
                nextTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doCurrentTransaction();
                    }
                });
            }
        }

        updateUI();
    }


    private void setupViewPagerAdapter() {
        insuranceViewPagerAdapter = new InsuranceViewPagerAdapter(getActivity(),
                insuranceLabelList,
                new PickerListener() {
                    @Override
                    public void didSelectedItem(int position) {
                        onClick(insurance_lay);
                    }

                    @Override
                    public void didCancelled() {

                    }
                });
        insuranceViewPager.setAdapter(insuranceViewPagerAdapter);

        insuranceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setUpPagerIndicator(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setUpPagerIndicator(int position) {
        ImageView[] indicators = new ImageView[insuranceLabelList.size()];

        pagerIndicator.removeAllViewsInLayout();

        for (int i = 0; i < insuranceLabelList.size(); i++) {
            indicators[i] = new ImageView(getActivity());
            indicators[i].setImageDrawable(getActivity().getDrawable(R.drawable.circular_unselected_indicator));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);

            pagerIndicator.addView(indicators[i], params);
        }
        if (indicators.length > 0)
            indicators[position].setImageDrawable(getActivity().getDrawable(R.drawable.circular_selected_indicator));
    }

    private void reloadUI() {
        Utils.hideKeyboard(getActivity());
        switch (currentDisplayType) {
            case Constants.SCHEDULE_CREATION_MODE:
                updateAllViews(true);
                onViewChangeInterface.updateNextTitle(getString(R.string.Save));
                updateNextTitle(getString(R.string.Save));
                break;
            case Constants.EDIT_MODE:
                updateAllViews(true);
                onViewChangeInterface.updateNextTitle(getString(R.string.Save));
                firstnameEt.requestFocus();
                firstnameEt.setSelection(firstnameEt.getText().toString().length());
                showOrHideSoftInputWindow(true);
                updateNextTitle(getString(R.string.Save));
                break;
            case Constants.CREATE_MODE:
                updateAllViews(true);
                onViewChangeInterface.updateNextTitle(getString(R.string.next));
                updateNextTitle(getString(R.string.next));
                break;
            case Constants.VIEW_MODE:
                updateAllViews(false);
                onViewChangeInterface.updateNextTitle(getString(R.string.edit));
                updateNextTitle(getString(R.string.edit));
                Utils.hideKeyboardFrom(getActivity(), this.getView());
                break;
        }

        checkAllFields();
    }

    private void updateAllViews(Boolean enabled) {
        Utils.setEditable(firstnameEt, enabled);
        Utils.setEditable(lastnameEt, enabled);

        if (enabled) {
            gender_value.setVisibility(View.GONE);
            genderSp.setVisibility(View.VISIBLE);
        } else {
            gender_value.setVisibility(View.VISIBLE);
            genderSp.setVisibility(View.GONE);
        }
    }

    private void updateUI() {
        if (currentDisplayType != Constants.CREATE_MODE) {
            insurance_lay.setVisibility(View.VISIBLE);
            title_tv.setVisibility(View.GONE);
            insurance_lay.setOnClickListener(this);
        } else {
            title_tv.setVisibility(View.VISIBLE);
            insurance_lay.setVisibility(View.GONE);

            setProfileCiv();
            checkAllFields();
        }
    }

    private void updateUI(WhoAmIApiResponseModel whoAmIApiResponseModel) {
        createUserRequestModel.setUser_data(new CreateUserRequestModel.UserDataBean(whoAmIApiResponseModel));

        try {
            UserDetailBean userDetailBean = (UserDetailBean) whoAmIApiResponseModel.getUser_detail().clone();
            createUserRequestModel.setUser_detail(userDetailBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateUI(createUserRequestModel);
    }

    private void updateUI(CreateUserRequestModel createUserRequestModel) {
        setProfileCiv();

        firstnameEt.setText(createUserRequestModel.getUser_data().getFirst_name());
        lastnameEt.setText(createUserRequestModel.getUser_data().getLast_name());
        dobEt.setText(createUserRequestModel.getUser_data().getDob());

        try {
            String gender = createUserRequestModel.getUser_data().getGenderKey();
            List genders = Arrays.asList(genderList);
            genderSp.setSelection(genders.indexOf(gender), false);
            gender_value.setText(genderSp.getSelectedItem().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateInsurance();
    }

    private void updateInsurance() {

        insurance_lay.setVisibility(View.VISIBLE);

        String primary_front, primary_back, secondary_front = null, secondary_back = null;

        if (createUserRequestModel.isInsurancePresent()) {

            primary_front = createUserRequestModel.getInsurance_front_path();
            primary_back = createUserRequestModel.getInsurance_back_path();

            secondary_front = createUserRequestModel.getSecondary_insurance_front_path();
            secondary_back = createUserRequestModel.getSecondary_insurance_back_path();

            updateInsuranceImages(primary_front, primary_back, secondary_front, secondary_back);

        } else if (createUserRequestModel.getUser_detail().getData().isInsurancePresent()) {

            primary_front = createUserRequestModel.getUser_detail().getData().getInsurance_front();
            primary_back = createUserRequestModel.getUser_detail().getData().getInsurance_back();

            secondary_front = createUserRequestModel.getUser_detail().getData().getSecondary_insurance_front();
            secondary_back = createUserRequestModel.getUser_detail().getData().getSecondary_insurance_back();

            updateInsuranceImages(primary_front, primary_back, secondary_front, secondary_back);

        } else {
            insuranceViewPager.setVisibility(View.GONE);
            pagerIndicator.setVisibility(View.GONE);
            cash_tv.setVisibility(View.VISIBLE);
        }
    }

    private void updateInsuranceImages(String primary_front, String primary_back, @Nullable String secondary_front, @Nullable String secondary_back) {
        List<String> insurancePath = new ArrayList<>();

        insuranceLabelList.clear();
        insuranceLabelList.add(getString(R.string.primary_insurance_front));
        insuranceLabelList.add(getString(R.string.primary_insurance_back));

        insurancePath.add(primary_front);
        insurancePath.add(primary_back);

        if (secondary_front != null && secondary_back != null) {
            insuranceLabelList.add(getString(R.string.secondary_insurance_front));
            insuranceLabelList.add(getString(R.string.secondary_insurance_back));

            insurancePath.add(secondary_front);
            insurancePath.add(secondary_back);
        }

        boolean isAuthRequired = isCashSelected == 0;

        insuranceViewPagerAdapter.setData(insurancePath, insuranceLabelList, !isAuthRequired);
        setUpPagerIndicator(0);

        insuranceViewPager.setVisibility(View.VISIBLE);
        pagerIndicator.setVisibility(View.VISIBLE);
        cash_tv.setVisibility(View.GONE);
    }

    private void updateUserRequestModel() {
        CreateUserRequestModel.UserDataBean userDataBean = new CreateUserRequestModel.UserDataBean(createUserRequestModel.getUser_data().getPhone(), createUserRequestModel.getUser_data().getEmail());
        userDataBean.setFirst_name(firstnameEt.getText().toString());
        userDataBean.setLast_name(lastnameEt.getText().toString());
        userDataBean.setDob(dobEt.getText().toString());
        userDataBean.setGender(Constants.genderList.get(genderSp.getSelectedItemPosition()));

        createUserRequestModel.setUser_data(userDataBean);
        createUserRequestModel.setUser_avatar_path(profileImgPath);
    }

    public void setProfileCiv() {
        if (createUserRequestModel.getUser_data().getUser_avatar() != null || profileImgPath != null) {
            Utils.setImageWithGlide(getContext(), profileCiv, createUserRequestModel.getUser_data().getUser_avatar(), getContext().getDrawable(R.drawable.profile_placeholder), true, true);
            if (profileImgPath != null && !profileImgPath.isEmpty()) {
                profileImg = getBitmpaFromPath(profileImgPath);
                if (profileImg != null)
                    profileCiv.setImageBitmap(profileImg);
            }
        } else {
            if (whoAmi != null)
                Utils.setImageWithGlide(getContext(), profileCiv, whoAmi.getUser_avatar(), getContext().getDrawable(R.drawable.profile_placeholder), true, true);
        }
    }

    private void addTextWatcher(EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkAllFields();
            }
        });

    }

    private void checkAllFields() {
        if (currentDisplayType == Constants.VIEW_MODE) {
            onViewChangeInterface.enableNext(true);
            enableNext(true);
        } else if (!firstnameEt.getText().toString().isEmpty()
                && !lastnameEt.getText().toString().isEmpty()
                && !dobEt.getText().toString().isEmpty()) {
            onViewChangeInterface.enableNext(true);
            enableNext(true);
        } else {
            onViewChangeInterface.enableNext(false);
            enableNext(false);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.dob_et:
                if (currentDisplayType == Constants.VIEW_MODE) {
                    if (hasFocus) {
                        dobEt.clearFocus();
                    }
                    return;
                }

                if (hasFocus) {
                    Utils.hideKeyboard(getActivity());
                    DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.DATE_PICKER_TYPE, TYPE_DOB);
                    datePickerDialogFragment.setArguments(bundle);
                    datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), DatePickerDialogFragment.class.getSimpleName());
                    dobEt.clearFocus();
                }


                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (currentDisplayType == Constants.VIEW_MODE) {
            return;
        }

        switch (v.getId()) {
            case R.id.profile_civ:
                CameraUtil.showImageSelectionAlert(getActivity());
                break;
            case R.id.insurance_lay:
            case R.id.insurance_viewPager:
            case R.id.cash_tv:
                Bundle bundle = new Bundle();
                String primaryFront = null, primaryBack = null, secondaryFront = null, secondaryBack = null;

                if (createUserRequestModel.isInsurancePresent()) {
                    bundle = new Bundle();
                    primaryFront = createUserRequestModel.getInsurance_front_path();
                    primaryBack = createUserRequestModel.getInsurance_back_path();
                    secondaryFront = createUserRequestModel.getSecondary_insurance_front_path();
                    secondaryBack = createUserRequestModel.getSecondary_insurance_back_path();

                } else if (createUserRequestModel.getUser_detail() != null &&
                        createUserRequestModel.getUser_detail().getData() != null) {
                    bundle = new Bundle();
                    primaryFront = createUserRequestModel.getUser_detail().getData().getInsurance_front();
                    primaryBack = createUserRequestModel.getUser_detail().getData().getInsurance_back();
                    secondaryFront = createUserRequestModel.getUser_detail().getData().getSecondary_insurance_front();
                    secondaryBack = createUserRequestModel.getUser_detail().getData().getSecondary_insurance_back();
                }

                bundle.putString(ArgumentKeys.INSURANCE_FRONT, primaryFront);
                bundle.putString(ArgumentKeys.INSURANCE_BACK, primaryBack);
                bundle.putString(ArgumentKeys.SECONDARY_INSURANCE_FRONT, secondaryFront);
                bundle.putString(ArgumentKeys.SECONDARY_INSURANCE_BACK, secondaryBack);
                bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);

                onActionCompleteInterface.onCompletionResult(RequestID.REQUEST_INSURANCE_CHANGE, true, bundle);
//                PatientChoosePaymentFragment patientChoosePaymentFragment = new PatientChoosePaymentFragment();
//                patientChoosePaymentFragment.setArguments(bundle);
//                ((ShowSubFragmentInterface) getActivity()).onShowFragment(patientChoosePaymentFragment);
                break;
        }
    }

    @Override
    public void doCurrentTransaction() {
        switch (currentDisplayType) {
            case Constants.SCHEDULE_CREATION_MODE:
            case Constants.EDIT_MODE:
                onViewChangeInterface.enableNext(false);
                enableNext(false);
                updateUserRequestModel();

                if (isPrimaryDeleted || isSecondaryDeleted) {
                    updateProfile = true;
                    UpdateProfileModel updateProfileModel = new ViewModelProvider(this).get(UpdateProfileModel.class);
                    updateProfileModel.deleteInsurance(isPrimaryDeleted, isSecondaryDeleted);
                }

                if (createUserRequestModel.getUser_detail() != null &&
                        createUserRequestModel.getUser_detail().getData() != null) {

                    if (createUserRequestModel.getUser_detail().getData().isInsurancePresent()) {
                        if (createUserRequestModel.getUser_detail().getData().getInsurance_front().equals(createUserRequestModel.getInsurance_front_path())) {
                            createUserRequestModel.setInsurance_front_path(null);
                        }
                        if (createUserRequestModel.getUser_detail().getData().getInsurance_back().equals(createUserRequestModel.getInsurance_back_path())) {
                            createUserRequestModel.setInsurance_back_path(null);
                        }
                    }

                    if (createUserRequestModel.getUser_detail().getData().isSecondaryInsurancePresent()) {
                        if (createUserRequestModel.getUser_detail().getData().getSecondary_insurance_front().equals(createUserRequestModel.getSecondary_insurance_front_path())) {
                            createUserRequestModel.setSecondary_insurance_front_path(null);
                        }
                        if (createUserRequestModel.getUser_detail().getData().getSecondary_insurance_back().equals(createUserRequestModel.getSecondary_insurance_back_path())) {
                            createUserRequestModel.setSecondary_insurance_back_path(null);
                        }
                    }
                }

                if (!updateProfile)
                    updateProfileModel.updatePatient(createUserRequestModel, null);

                break;
            case Constants.VIEW_MODE:
                currentDisplayType = Constants.EDIT_MODE;
                Bundle bundle = getArguments();
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putInt(ArgumentKeys.VIEW_TYPE, Constants.EDIT_MODE);
                setArguments(bundle);
                reloadUI();
                break;
            case Constants.CREATE_MODE:
                updateUserRequestModel();
                bundle = new Bundle();
                bundle.putString(ArgumentKeys.HEADER, getResources().getString(R.string.terms_and_conditions));
                bundle.putString(ArgumentKeys.PAGEHINT, getResources().getString(R.string.terms_and_conditions_info));
                bundle.putString(ArgumentKeys.URL, getResources().getString(R.string.terms_and_conditions_url));

                onActionCompleteInterface.onCompletionResult(RequestID.PROFILE_UPDATED, true, bundle);
                break;
        }
    }


    @Override
    public void onImageReceived(String imagePath) {
        profileImgPath = imagePath;
        setProfileCiv();
    }


    private void updatedProfile() {
        onActionCompleteInterface.onCompletionResult(RequestID.PROFILE_UPDATED, true, null);
    }

    @Override
    public void didReceiveIntent(Bundle bundle, String type) {
        Log.d("PatientRegistration", "didReceiveIntent:" + type);
        switch (type) {
            case RequestID.INSURANCE_CHANGE_RESULT:
                if (bundle != null) {
                    if (bundle.getBoolean(ArgumentKeys.CASH_SELECTED)) {
                        isCashSelected = 1;
                        Log.d("PatientRegistration", "cash selected:");
                        createUserRequestModel.setInsurance_back_path(null);
                        createUserRequestModel.setInsurance_front_path(null);
                        createUserRequestModel.setSecondary_insurance_front_path(null);
                        createUserRequestModel.setSecondary_insurance_back_path(null);

                        createUserRequestModel.getUser_detail().getData().setInsurance_front(null);
                        createUserRequestModel.getUser_detail().getData().setInsurance_back(null);
                        createUserRequestModel.getUser_detail().getData().setSecondary_insurance_front(null);
                        createUserRequestModel.getUser_detail().getData().setSecondary_insurance_back(null);

                        isPrimaryDeleted = true;
                        isSecondaryDeleted = true;
                    } else {
                        isCashSelected = 0;
                        Log.d("PatientRegistration", "path");
                        createUserRequestModel.setInsurance_front_path(bundle.getString(ArgumentKeys.INSURANCE_FRONT));
                        createUserRequestModel.setInsurance_back_path(bundle.getString(ArgumentKeys.INSURANCE_BACK));
                        createUserRequestModel.setSecondary_insurance_front_path(bundle.getString(ArgumentKeys.SECONDARY_INSURANCE_FRONT));
                        createUserRequestModel.setSecondary_insurance_back_path(bundle.getString(ArgumentKeys.SECONDARY_INSURANCE_BACK));

                        isPrimaryDeleted = bundle.getBoolean(ArgumentKeys.IS_PRIMARY_DELETED, false);
                        isSecondaryDeleted = bundle.getBoolean(ArgumentKeys.IS_SECONDARY_DELETED, false);
                    }
                }
                updateInsurance();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        updateUserRequestModel();
        Utils.hideKeyboard(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.DATE_PICKER_INTENT));

        if (currentDisplayType == Constants.CREATE_MODE) {
            onViewChangeInterface.updateTitle(getString(R.string.profile));
            updateTitle(getString(R.string.profile));
        } else {
            onViewChangeInterface.updateTitle(UserDetailPreferenceManager.getUserDisplayName());
            updateTitle(UserDetailPreferenceManager.getUserDisplayName());
        }

        onViewChangeInterface.hideOrShowNext(true);
        hideOrShowNext(true);

        reloadUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        createUserRequestModel.setInsurance_front_path(null);
        createUserRequestModel.setInsurance_back_path(null);
        createUserRequestModel.setSecondary_insurance_front_path(null);
        createUserRequestModel.setSecondary_insurance_back_path(null);

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dateBroadcastReceiver);
    }


    @Override
    public void hideOrShowNext(boolean show) {
        if (show) {
            nextTv.setVisibility(View.VISIBLE);
        } else {
            nextTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideOrShowClose(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowToolbarTile(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowBackIv(boolean hideOrShow) {

    }

    @Override
    public void attachObserver(BaseApiViewModel baseApiViewModel) {

    }

    @Override
    public void enableNext(boolean enable) {
        nextTv.setEnabled(enable);

        if (enable) {
            nextTv.setAlpha(1);
        } else {
            nextTv.setAlpha(0.5f);
        }
    }

    @Override
    public void updateTitle(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    public void hideOrShowOtherOption(boolean hideOrShow) {

    }

    @Override
    public void updateNextTitle(String nextTitle) {
        nextTv.setText(nextTitle);
    }
}
