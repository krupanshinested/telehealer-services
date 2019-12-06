package com.thealer.telehealer.views.signup.medicalAssistant;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.thealer.telehealer.apilayer.models.commonResponseModel.DataBean;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserDetailBean;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 15,October,2018
 */
public class MedicalAssistantDetailFragment extends BaseFragment implements View.OnClickListener, DoCurrentTransactionInterface,
        CameraInterface, OnViewChangeInterface {

    private CircleImageView profileCiv;
    private ImageView cameraIv;
    private TextInputLayout firstnameTil;
    private EditText firstnameEt;
    private TextInputLayout lastnameTil;
    private EditText lastnameEt;
    private TextView titleTv;
    private LinearLayout title_lay, gender_lay;
    private Spinner titleSpinner;
    private CardView certificate_card;
    private TextInputLayout degreeTil;
    private EditText degreeEt;
    private TextView genderTv;
    private Spinner genderSp;

    //not for Registration
    private ImageView certificate_iv;
    private TextView certificate_tv;
    private TextView gender_value, title_value;

    private String certificateImagePath;
    private String profileImgPath;
    private OnViewChangeInterface onViewChangeInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private String[] genderList, titleList;
    private int imagePickedFor;

    private CreateUserRequestModel createUserRequestModel;
    private UpdateProfileModel updateProfileModel;
    private WhoAmIApiViewModel whoAmIApiViewModel;
    private OnCloseActionInterface onCloseActionInterface;

    private WhoAmIApiResponseModel whoAmi;
    private int currentDisplayType = Constants.CREATE_MODE;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_assistant_detail, container, false);

        if (savedInstanceState != null) {
            profileImgPath = savedInstanceState.getString("img_path");
            certificateImagePath = savedInstanceState.getString("certificateImagePath");
        }

        createUserRequestModel = new ViewModelProvider(getActivity()).get(CreateUserRequestModel.class);

        initView(view);
        onViewChangeInterface.hideOrShowNext(true);
        hideOrShowNext(true);

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

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            profileImgPath = savedInstanceState.getString("img_path");
            certificateImagePath = savedInstanceState.getString("certificateImagePath");
        }

        if (savedInstanceState == null && getArguments() != null) {
            currentDisplayType = getArguments().getInt(ArgumentKeys.VIEW_TYPE, Constants.CREATE_MODE);
        } else if (savedInstanceState != null) {
            currentDisplayType = savedInstanceState.getInt(ArgumentKeys.VIEW_TYPE, Constants.CREATE_MODE);
        } else {
            currentDisplayType = Constants.CREATE_MODE;
        }

        addObservers();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (currentDisplayType == Constants.CREATE_MODE) {
            onViewChangeInterface.updateTitle(getString(R.string.profile));
            updateTitle(getString(R.string.profile));
        } else {
            onViewChangeInterface.updateTitle(UserDetailPreferenceManager.getUserDisplayName());
            updateTitle(UserDetailPreferenceManager.getUserDisplayName());
        }

        reloadUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideKeyboard(getActivity());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (profileImgPath != null)
            outState.putString("img_path", profileImgPath);

        if (certificateImagePath != null) {
            outState.putString("certificateImagePath", certificateImagePath);
        }

        outState.putInt(ArgumentKeys.VIEW_TYPE, currentDisplayType);
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        profileCiv = (CircleImageView) view.findViewById(R.id.profile_civ);
        cameraIv = (ImageView) view.findViewById(R.id.camera_iv);
        firstnameTil = (TextInputLayout) view.findViewById(R.id.firstname_til);
        firstnameEt = (EditText) view.findViewById(R.id.firstname_et);
        lastnameTil = (TextInputLayout) view.findViewById(R.id.lastname_til);
        lastnameEt = (EditText) view.findViewById(R.id.lastname_et);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        titleSpinner = (Spinner) view.findViewById(R.id.title_spinner);
        degreeTil = (TextInputLayout) view.findViewById(R.id.degree_til);
        degreeEt = (EditText) view.findViewById(R.id.degree_et);
        genderTv = (TextView) view.findViewById(R.id.gender_tv);
        genderSp = (Spinner) view.findViewById(R.id.gender_sp);
        title_lay = view.findViewById(R.id.title_lay);
        gender_lay = view.findViewById(R.id.gender_lay);
        certificate_card = view.findViewById(R.id.certificate_card);

        certificate_iv = (ImageView) view.findViewById(R.id.certificate_iv);
        certificate_tv = (TextView) view.findViewById(R.id.certificate_tv);

        gender_value = view.findViewById(R.id.gender_value);
        title_value = view.findViewById(R.id.title_value);

        profileCiv.setOnClickListener(this);
        certificate_iv.setOnClickListener(this);

        addTextWatcher(firstnameEt);
        addTextWatcher(lastnameEt);
        addTextWatcher(degreeEt);

        String[] genderList = getActivity().getResources().getStringArray(R.array.gender_list);
        this.genderList = genderList;
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, genderList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSp.setAdapter(arrayAdapter);

        String[] titleList = getActivity().getResources().getStringArray(R.array.assistant_title_list);
        this.titleList = titleList;
        ArrayAdapter titleAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, titleList);
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(titleAdapter);

        if (getArguments() != null && getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR, false)) {
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
        updateView();
    }

    private void addObservers() {
        updateProfileModel = new ViewModelProvider(this).get(UpdateProfileModel.class);

        updateProfileModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null) {

                    whoAmIApiViewModel.checkWhoAmI();

                    onViewChangeInterface.enableNext(true);
                    enableNext(true);
                    currentDisplayType = Constants.VIEW_MODE;
                    reloadUI();

                    updateUserRequestModel();
                }

            }
        });

        whoAmIApiViewModel = new ViewModelProvider(getActivity()).get(WhoAmIApiViewModel.class);

        whoAmIApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                            whoAmi = whoAmIApiResponseModel;
                            updateUI(whoAmi);
                            UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);

                            if (profileImgPath != null) {
                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(getString(R.string.profile_picture_updated)));
                            }

                            onViewChangeInterface.updateTitle(UserDetailPreferenceManager.getUserDisplayName());
                            updateTitle(UserDetailPreferenceManager.getUserDisplayName());
                        }
                    }
                });

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).attachObserver(updateProfileModel);
            ((BaseActivity) getActivity()).attachObserver(whoAmIApiViewModel);
        }
    }

    private void updateUI(WhoAmIApiResponseModel whoAmIApiResponseModel) {
        createUserRequestModel.setUser_data(new CreateUserRequestModel.UserDataBean(whoAmIApiResponseModel));

        try {
            UserDetailBean userDetailBean = (UserDetailBean) whoAmIApiResponseModel.getUser_detail().clone();
            createUserRequestModel.setUser_detail(userDetailBean);
        } catch (Exception e) {
            e.printStackTrace();
            createUserRequestModel.setUser_detail(whoAmIApiResponseModel.getUser_detail());
        }

        updateUI(createUserRequestModel);
    }

    private void updateUI(CreateUserRequestModel createUserRequestModel) {
        firstnameEt.setText(createUserRequestModel.getUser_data().getFirst_name());
        lastnameEt.setText(createUserRequestModel.getUser_data().getLast_name());

        try {
            String title = createUserRequestModel.getUser_detail().getData().getTitle();
            List titles = Arrays.asList(titleList);
            titleSpinner.setSelection(titles.indexOf(title), false);
            title_value.setText(titleSpinner.getSelectedItem().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String gender = createUserRequestModel.getUser_data().getGenderKey();
            List genders = Arrays.asList(genderList);
            genderSp.setSelection(genders.indexOf(gender), false);
            gender_value.setText(genderSp.getSelectedItem().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        degreeEt.setText(createUserRequestModel.getUser_detail().getData().getDegree());

        setProfileCiv();
        setCertficate();
        checkAllFields();
    }

    private void updateView() {
        if (currentDisplayType != Constants.CREATE_MODE) {
            certificate_iv.setVisibility(View.VISIBLE);
            certificate_tv.setVisibility(View.VISIBLE);
            certificate_card.setVisibility(View.VISIBLE);
        } else {
            certificate_iv.setVisibility(View.GONE);
            certificate_tv.setVisibility(View.GONE);
            certificate_card.setVisibility(View.GONE);

            checkAllFields();
            setProfileCiv();
        }
    }

    private void reloadUI() {
        Utils.hideKeyboard(getActivity());
        switch (currentDisplayType) {
            case Constants.EDIT_MODE:
                updateAllViews(true);
                onViewChangeInterface.updateNextTitle(getString(R.string.Save));
                updateNextTitle(getString(R.string.Save));
                firstnameEt.requestFocus();
                firstnameEt.setSelection(firstnameEt.getText().toString().length());
                showOrHideSoftInputWindow(true);
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
        Utils.setEditable(degreeEt, enabled);

        if (enabled) {
            gender_value.setVisibility(View.GONE);
            gender_lay.setVisibility(View.VISIBLE);

            title_value.setVisibility(View.GONE);
            title_lay.setVisibility(View.VISIBLE);
        } else {
            gender_value.setVisibility(View.VISIBLE);
            gender_lay.setVisibility(View.GONE);

            title_value.setVisibility(View.VISIBLE);
            title_lay.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
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
                && !degreeEt.getText().toString().isEmpty()) {
            onViewChangeInterface.enableNext(true);
            enableNext(true);
        } else {
            enableNext(false);
            onViewChangeInterface.enableNext(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (currentDisplayType == Constants.VIEW_MODE) {
            return;
        }

        switch (v.getId()) {
            case R.id.profile_civ:
                imagePickedFor = v.getId();
                CameraUtil.showImageSelectionAlert(getActivity());
                break;
            case R.id.certificate_iv:
                imagePickedFor = v.getId();
                CameraUtil.showImageSelectionAlert(getActivity());
                break;
        }
    }

    public void setProfileCiv() {
        if (profileImgPath != null) {
            Bitmap profileImg = getBitmpaFromPath(profileImgPath);
            if (profileImg != null)
                profileCiv.setImageBitmap(profileImg);
        } else if (createUserRequestModel.getUser_data().getUser_avatar() != null) {
            Utils.setImageWithGlide(getContext(), profileCiv, createUserRequestModel.getUser_data().getUser_avatar(), getContext().getDrawable(R.drawable.profile_placeholder), true, true);
        }
    }

    public void setCertficate() {
        if (certificateImagePath != null) {
            Bitmap certificate = getBitmpaFromPath(certificateImagePath);
            if (certificate != null)
                certificate_iv.setImageBitmap(certificate);
        } else if (createUserRequestModel.getUser_detail().getData().getCertification() != null) {
            Utils.setImageWithGlide(getContext(), certificate_iv, createUserRequestModel.getUser_detail().getData().getCertification(), getContext().getDrawable(R.drawable.placeholder_certificate), true, true);
        }
    }

    @Override
    public void doCurrentTransaction() {
        switch (currentDisplayType) {
            case Constants.EDIT_MODE:
                onViewChangeInterface.enableNext(false);
                enableNext(false);
                updateProfileModel.updateMedicalAssistant(createUserRequestModel());
                break;
            case Constants.VIEW_MODE:
                currentDisplayType = Constants.EDIT_MODE;
                reloadUI();
                break;
            case Constants.CREATE_MODE:
                createUserRequestModel();
                onActionCompleteInterface.onCompletionResult(null, true, null);
                break;
        }
    }

    private CreateUserRequestModel createUserRequestModel() {
        CreateUserRequestModel.UserDataBean userDataBean = new CreateUserRequestModel.UserDataBean(createUserRequestModel.getUser_data().getPhone(), createUserRequestModel.getUser_data().getEmail());
        userDataBean.setFirst_name(firstnameEt.getText().toString());
        userDataBean.setLast_name(lastnameEt.getText().toString());
        userDataBean.setGender(Constants.genderList.get(genderSp.getSelectedItemPosition()));

        DataBean dataBean = new DataBean();
        dataBean.setDegree(degreeEt.getText().toString());
        dataBean.setTitle(titleSpinner.getSelectedItem().toString());

        UserDetailBean userDetailBean = new UserDetailBean(dataBean, null);

        createUserRequestModel.setUser_data(userDataBean);
        createUserRequestModel.setUser_detail(userDetailBean);
        createUserRequestModel.setUser_avatar_path(profileImgPath);

        if (certificateImagePath != null) {
            createUserRequestModel.setCertification_path(certificateImagePath);
        }

        return createUserRequestModel;
    }

    private void updateUserRequestModel() {
        onActionCompleteInterface.onCompletionResult(RequestID.PROFILE_UPDATED, true, null);
    }

    @Override
    public void onImageReceived(String imagePath) {
        switch (imagePickedFor) {
            case R.id.profile_civ:
                profileImgPath = imagePath;
                setProfileCiv();
                break;
            case R.id.certificate_iv:
                certificateImagePath = imagePath;
                setCertficate();
                break;
        }

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
