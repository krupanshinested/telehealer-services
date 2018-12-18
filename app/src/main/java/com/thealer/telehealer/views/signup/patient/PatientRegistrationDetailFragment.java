package com.thealer.telehealer.views.signup.patient;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
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
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DateBroadcastReceiver;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.settings.Interface.BundleReceiver;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thealer.telehealer.common.Constants.TYPE_DOB;

/**
 * Created by Aswin on 11,October,2018
 */

public class PatientRegistrationDetailFragment extends BaseFragment implements
        View.OnFocusChangeListener, View.OnClickListener,
        DoCurrentTransactionInterface, CameraInterface, BundleReceiver {

    private CircleImageView profileCiv;
    private EditText firstnameEt;
    private EditText lastnameEt;
    private EditText dobEt;
    private Spinner genderSp;
    private TextView title_tv;

    //not for Registration
    private LinearLayout insurance_lay;
    private ImageView insurance_front_iv, insurance_back_iv;
    private TextView cash_tv, gender_value;
    private HorizontalScrollView insurance_image;

    private String[] genderList;
    private Bitmap profileImg = null;
    private String profileImgPath;
    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            profileImgPath = savedInstanceState.getString(getString(R.string.image_path));
        }
        addObservers();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (profileImgPath != null)
            outState.putString(getString(R.string.image_path), profileImgPath);

        outState.putInt(ArgumentKeys.VIEW_TYPE, currentDisplayType);
        outState.putInt("isCashSelected", isCashSelected);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_detail, container, false);

        onViewChangeInterface.hideOrShowNext(true);
        onViewChangeInterface.enableNext(false);

        createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);

        Bundle bundle = getArguments();

        if (savedInstanceState == null && bundle != null) {
            currentDisplayType = bundle.getInt(ArgumentKeys.VIEW_TYPE);
        } else if (savedInstanceState != null) {
            currentDisplayType = savedInstanceState.getInt(ArgumentKeys.VIEW_TYPE);
        }

        initView(view);

        checkAllFields();

        Log.e("aswin", "onCreateView: " + currentDisplayType);
        if (currentDisplayType != Constants.CREATE_MODE) {
            if (whoAmi == null) {
                whoAmIApiViewModel.checkWhoAmI();
            } else {
                if (createUserRequestModel.getUser_data().getFirst_name() != null) {
                    updateUI(createUserRequestModel);
                } else {
                    updateUI(whoAmi);
                }
            }
        }

        if (savedInstanceState != null) {
            isCashSelected = savedInstanceState.getInt("isCashSelected", -1);
        }

        return view;
    }

    private void initView(View view) {
        profileCiv = (CircleImageView) view.findViewById(R.id.profile_civ);
        firstnameEt = (EditText) view.findViewById(R.id.firstname_et);
        lastnameEt = (EditText) view.findViewById(R.id.lastname_et);
        dobEt = (EditText) view.findViewById(R.id.dob_et);
        genderSp = view.findViewById(R.id.gender_sp);
        gender_value = view.findViewById(R.id.gender_value);
        title_tv = view.findViewById(R.id.title_tv);

        insurance_lay = view.findViewById(R.id.insurance_lay);
        insurance_front_iv = view.findViewById(R.id.insurance_front_iv);
        insurance_back_iv = view.findViewById(R.id.insurance_back_iv);
        cash_tv = view.findViewById(R.id.cash_tv);
        insurance_image = view.findViewById(R.id.insurance_image);

        insurance_front_iv.setOnClickListener(this);
        insurance_back_iv.setOnClickListener(this);
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

        updateUI();
    }

    private void reloadUI() {
        switch (currentDisplayType) {
            case Constants.SCHEDULE_CREATION_MODE:
                updateAllViews(true);
                onViewChangeInterface.updateNextTitle(getString(R.string.Save));
                break;
            case Constants.EDIT_MODE:
                updateAllViews(true);
                onViewChangeInterface.updateNextTitle(getString(R.string.update));
                break;
            case Constants.CREATE_MODE:
                updateAllViews(true);
                onViewChangeInterface.updateNextTitle(getString(R.string.next));
                break;
            case Constants.VIEW_MODE:
                updateAllViews(false);
                onViewChangeInterface.updateNextTitle(getString(R.string.edit));
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
        Log.e("aswin", "updateUI: " + currentDisplayType);
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
        Log.d("PatientRegistration", "updateUI");
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
        Log.d("PatientRegistration", "updateInsurance");

        insurance_lay.setVisibility(View.VISIBLE);
        if (createUserRequestModel.getInsurance_front_path() != null &&
                createUserRequestModel.getInsurance_back_path() != null) {
            Bitmap front = getBitmpaFromPath(createUserRequestModel.getInsurance_front_path());
            Bitmap back = getBitmpaFromPath(createUserRequestModel.getInsurance_back_path());
            if (front != null) {
                insurance_front_iv.setImageBitmap(front);
            }

            if (back != null)
                insurance_back_iv.setImageBitmap(back);

            insurance_image.setVisibility(View.VISIBLE);
            cash_tv.setVisibility(View.GONE);
        } else if (createUserRequestModel.getUser_detail().getData().isInsurancePresent()) {

            Utils.setImageWithGlide(getContext(), insurance_front_iv, createUserRequestModel.getUser_detail().getData().getInsurance_front(), getActivity().getDrawable(R.drawable.placeholder_insurance), true);
            Utils.setImageWithGlide(getContext(), insurance_back_iv, createUserRequestModel.getUser_detail().getData().getInsurance_back(), getActivity().getDrawable(R.drawable.placeholder_insurance), true);

            insurance_image.setVisibility(View.VISIBLE);
            cash_tv.setVisibility(View.GONE);
        } else {
            insurance_image.setVisibility(View.GONE);
            cash_tv.setVisibility(View.VISIBLE);
        }
    }

    private void updateUserRequestModel() {
        CreateUserRequestModel.UserDataBean userDataBean = new CreateUserRequestModel.UserDataBean();
        userDataBean.setFirst_name(firstnameEt.getText().toString());
        userDataBean.setLast_name(lastnameEt.getText().toString());
        userDataBean.setDob(dobEt.getText().toString());
        userDataBean.setGender(genderSp.getSelectedItem().toString());

        createUserRequestModel.setUser_data(userDataBean);
        createUserRequestModel.setUser_avatar_path(profileImgPath);
    }

    public void setProfileCiv() {
        if (profileImgPath != null && !profileImgPath.isEmpty()) {
            profileImg = getBitmpaFromPath(profileImgPath);
            if (profileImg != null)
                profileCiv.setImageBitmap(profileImg);
        } else if (createUserRequestModel.getUser_data().getUser_avatar() != null) {
            Utils.setImageWithGlide(getContext(), profileCiv, createUserRequestModel.getUser_data().getUser_avatar(), getContext().getDrawable(R.drawable.profile_placeholder), true);
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
        } else if (!firstnameEt.getText().toString().isEmpty()
                && !lastnameEt.getText().toString().isEmpty()
                && !dobEt.getText().toString().isEmpty()) {
            onViewChangeInterface.enableNext(true);
        } else
            onViewChangeInterface.enableNext(false);
    }

    private void addObservers() {
        updateProfileModel = ViewModelProviders.of(this).get(UpdateProfileModel.class);

        updateProfileModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null) {

                    if (currentDisplayType == Constants.SCHEDULE_CREATION_MODE) {
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else {
                        whoAmIApiViewModel.checkWhoAmI();

                        onViewChangeInterface.enableNext(true);
                        currentDisplayType = Constants.VIEW_MODE;
                        reloadUI();

                        updatedProfile();
                    }
                }

            }
        });

        whoAmIApiViewModel = ViewModelProviders.of(getActivity()).get(WhoAmIApiViewModel.class);

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                            whoAmi = whoAmIApiResponseModel;
                            updateUI(whoAmIApiResponseModel);
                            UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);

                            if (profileImgPath != null) {
                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(getString(R.string.profile_picture_updated)));
                            }

                            onViewChangeInterface.updateTitle(UserDetailPreferenceManager.getUserDisplayName());
                        }
                    }
                });

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).attachObserver(updateProfileModel);
            ((BaseActivity) getActivity()).attachObserver(whoAmIApiViewModel);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.dob_et:
                if (currentDisplayType == Constants.VIEW_MODE) {
                    return;
                }

                if (hasFocus) {
                    DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.DATE_PICKER_TYPE, TYPE_DOB);
                    datePickerDialogFragment.setArguments(bundle);
                    datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), DatePickerDialogFragment.class.getSimpleName());
                }

                dobEt.clearFocus();
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
                CameraUtil.with(getActivity()).showImageSelectionAlert();
                break;
            case R.id.cash_tv:
            case R.id.insurance_back_iv:
            case R.id.insurance_front_iv:
            case R.id.insurance_lay:
                onActionCompleteInterface.onCompletionResult(RequestID.REQUEST_INSURANCE_CHANGE, true, null);
                break;
        }
    }

    @Override
    public void doCurrentTransaction() {
        switch (currentDisplayType) {
            case Constants.SCHEDULE_CREATION_MODE:
            case Constants.EDIT_MODE:
                onViewChangeInterface.enableNext(false);
                updateUserRequestModel();

                if (isCashSelected == 1) {
                    Log.e("aswin", "doCurrentTransaction: " + new Gson().toJson(createUserRequestModel));
                    UpdateProfileModel updateProfileModel = ViewModelProviders.of(this).get(UpdateProfileModel.class);
                    updateProfileModel.deleteInsurance();
                }
                updateProfileModel.updatePatient(createUserRequestModel);

                break;
            case Constants.VIEW_MODE:
                currentDisplayType = Constants.EDIT_MODE;
                reloadUI();
                break;
            case Constants.CREATE_MODE:
                updateUserRequestModel();
                onActionCompleteInterface.onCompletionResult(RequestID.PROFILE_UPDATED, true, null);
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

                        createUserRequestModel.getUser_detail().getData().setInsurance_front(null);
                        createUserRequestModel.getUser_detail().getData().setInsurance_back(null);
                    } else {
                        isCashSelected = 0;
                        Log.d("PatientRegistration", "path");
                        createUserRequestModel.setInsurance_front_path(bundle.getString(ArgumentKeys.INSURANCE_FRONT));
                        createUserRequestModel.setInsurance_back_path(bundle.getString(ArgumentKeys.INSURANCE_BACK));
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
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.DATE_PICKER_INTENT));

        if (currentDisplayType == Constants.CREATE_MODE) {
            onViewChangeInterface.updateTitle(getString(R.string.profile));
        } else {
            onViewChangeInterface.updateTitle(UserDetailPreferenceManager.getUserDisplayName());
        }

        onViewChangeInterface.hideOrShowNext(true);

        reloadUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dateBroadcastReceiver);
    }

}
