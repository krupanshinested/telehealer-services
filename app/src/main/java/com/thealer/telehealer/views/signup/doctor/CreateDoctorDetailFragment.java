package com.thealer.telehealer.views.signup.doctor;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
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

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.UpdateProfile.UpdateProfileModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.apilayer.models.createuser.PracticesBean;
import com.thealer.telehealer.apilayer.models.createuser.SpecialtiesBean;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraInterface;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CurrentModeInterface;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.home.DoctorOnBoardingActivity;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 25,February,2019
 */
public class CreateDoctorDetailFragment extends BaseFragment implements View.OnClickListener, CurrentModeInterface, CameraInterface, DoCurrentTransactionInterface {

    private CircleImageView profileCiv;
    private ImageView cameraIv;
    private TextInputLayout firstnameTil;
    private EditText firstnameEt;
    private TextInputLayout lastnameTil;
    private EditText lastnameEt;
    private TextInputLayout titleTil;
    private EditText titleEt;
    private LinearLayout specialityLl;
    private Spinner specialitySp;
    private TextView specialistTv;
    private TextInputLayout bioTil;
    private EditText bioEt;
    private LinearLayout genderLl;
    private TextView genderTv;
    private Spinner genderSp;
    private TextView genderValue;
    private View genderView;
    private TextInputLayout websiteTil;
    private EditText websiteEt;
    private TextInputLayout addAddressTil;
    private EditText addAddressEt;
    private TextInputLayout practiceTil;
    private EditText practiceEt;
    private RecyclerView officePhoneRv;
    private LinearLayout licenseLl;
    private TextView licenseHintTv;
    private RecyclerView licenseRv;
    private TextInputLayout addLicenseTil;
    private EditText addLicenseEt;
    private TextInputLayout npiTil;
    private EditText npiEt;
    private TextInputLayout liabilityInsuranceTil;
    private EditText liabilityEt;
    private ConstraintLayout driverLicense;
    private TextView driverLicenseTv;
    private CardView licenseCard;
    private ImageView driverLicenseIv;
    private ConstraintLayout certificate;
    private TextView certificateTv;
    private CardView certificateCard;
    private ImageView certificateIv;

    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private GetDoctorsApiResponseModel.DataBean doctorDetailModel;
    private CreateUserRequestModel createUserRequestModel;
    private OfficePhoneListAdapter officePhoneListAdapter;
    private DoctorLicenseListAdapter doctorLicenseListAdapter;
    private UpdateProfileModel updateProfileModel;
    private WhoAmIApiViewModel whoAmIApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private WhoAmIApiResponseModel whoAmi;

    private boolean isCreateManually;
    private String searchKey = "";
    private int practiceId = 0;
    private boolean isNewPractice = true;
    private int currentDisplayType = Constants.CREATE_MODE;
    private String doctorImagePath;
    private String[] genderArray;
    private String[] specialityArray;
    private int currentGalleryCallingId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);
        updateProfileModel = ViewModelProviders.of(this).get(UpdateProfileModel.class);
        whoAmIApiViewModel = ViewModelProviders.of(this).get(WhoAmIApiViewModel.class);

        attachObserverInterface.attachObserver(updateProfileModel);
        attachObserverInterface.attachObserver(whoAmIApiViewModel);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            doctorImagePath = savedInstanceState.getString(Constants.PICTURE_PATH);
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

    private void addObservers() {

        updateProfileModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null) {

                    whoAmIApiViewModel.checkWhoAmI();

                    onViewChangeInterface.enableNext(true);
                    currentDisplayType = Constants.VIEW_MODE;
                    reloadUI();

                    updatedProfile();
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
                            updateUI(whoAmIApiResponseModel);
                            UserDetailPreferenceManager.insertUserDetail(whoAmIApiResponseModel);

                            if (doctorImagePath != null) {
                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(getString(R.string.profile_picture_updated)));
                            }

                            onViewChangeInterface.updateTitle(UserDetailPreferenceManager.getUserDisplayName());

                            if (whoAmIApiResponseModel.getStatus().equals(Constants.ONBOARDING_PENDING)) {
                                startActivity(new Intent(getActivity(), DoctorOnBoardingActivity.class));
                            }


                        }
                    }
                });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.PICTURE_PATH, doctorImagePath);
        outState.putInt(ArgumentKeys.VIEW_TYPE, currentDisplayType);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_detail, container, false);

        onViewChangeInterface.hideOrShowNext(true);
        onViewChangeInterface.enableNext(false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        profileCiv = (CircleImageView) view.findViewById(R.id.profile_civ);
        cameraIv = (ImageView) view.findViewById(R.id.camera_iv);
        firstnameTil = (TextInputLayout) view.findViewById(R.id.firstname_til);
        firstnameEt = (EditText) view.findViewById(R.id.firstname_et);
        lastnameTil = (TextInputLayout) view.findViewById(R.id.lastname_til);
        lastnameEt = (EditText) view.findViewById(R.id.lastname_et);
        titleTil = (TextInputLayout) view.findViewById(R.id.title_til);
        titleEt = (EditText) view.findViewById(R.id.title_et);
        specialityLl = (LinearLayout) view.findViewById(R.id.speciality_ll);
        specialitySp = (Spinner) view.findViewById(R.id.speciality_sp);
        specialistTv = (TextView) view.findViewById(R.id.specialist_tv);
        bioTil = (TextInputLayout) view.findViewById(R.id.bio_til);
        bioEt = (EditText) view.findViewById(R.id.bio_et);
        genderLl = (LinearLayout) view.findViewById(R.id.gender_ll);
        genderTv = (TextView) view.findViewById(R.id.gender_tv);
        genderSp = (Spinner) view.findViewById(R.id.gender_sp);
        genderValue = (TextView) view.findViewById(R.id.gender_value);
        genderView = (View) view.findViewById(R.id.gender_view);
        websiteTil = (TextInputLayout) view.findViewById(R.id.website_til);
        websiteEt = (EditText) view.findViewById(R.id.website_et);
        addAddressTil = (TextInputLayout) view.findViewById(R.id.add_address_til);
        addAddressEt = (EditText) view.findViewById(R.id.add_address_et);
        practiceTil = (TextInputLayout) view.findViewById(R.id.practice_til);
        practiceEt = (EditText) view.findViewById(R.id.practice_et);
        officePhoneRv = (RecyclerView) view.findViewById(R.id.office_phone_rv);
        licenseLl = (LinearLayout) view.findViewById(R.id.license_ll);
        licenseHintTv = (TextView) view.findViewById(R.id.license_hint_tv);
        licenseRv = (RecyclerView) view.findViewById(R.id.license_rv);
        addLicenseTil = (TextInputLayout) view.findViewById(R.id.add_license_til);
        addLicenseEt = (EditText) view.findViewById(R.id.add_license_et);
        npiTil = (TextInputLayout) view.findViewById(R.id.npi_til);
        npiEt = (EditText) view.findViewById(R.id.npi_et);
        liabilityInsuranceTil = (TextInputLayout) view.findViewById(R.id.liability_insurance_til);
        liabilityEt = (EditText) view.findViewById(R.id.liability_et);
        driverLicense = (ConstraintLayout) view.findViewById(R.id.driver_license);
        driverLicenseTv = (TextView) view.findViewById(R.id.driver_license_tv);
        licenseCard = (CardView) view.findViewById(R.id.license_card);
        driverLicenseIv = (ImageView) view.findViewById(R.id.driver_license_iv);
        certificate = (ConstraintLayout) view.findViewById(R.id.certificate);
        certificateTv = (TextView) view.findViewById(R.id.certificate_tv);
        certificateCard = (CardView) view.findViewById(R.id.certificate_card);
        certificateIv = (ImageView) view.findViewById(R.id.certificate_iv);

        bioEt.setOnClickListener(this);
        addAddressEt.setOnClickListener(this);
        practiceEt.setOnClickListener(this);
        addLicenseEt.setOnClickListener(this);
        profileCiv.setOnClickListener(this);
        driverLicenseIv.setOnClickListener(this);
        certificateIv.setOnClickListener(this);

        addTextWatcher(firstnameEt, firstnameTil);
        addTextWatcher(lastnameEt, lastnameTil);
        addTextWatcher(titleEt, titleTil);
        addTextWatcher(bioEt, bioTil);
        addTextWatcher(addAddressEt, addAddressTil);
        addTextWatcher(addLicenseEt, addLicenseTil);
        addTextWatcher(npiEt, npiTil);
        addTextWatcher(liabilityEt, liabilityInsuranceTil);

        specialityArray = getActivity().getResources().getStringArray(R.array.doctor_speciality_list);
        genderArray = getActivity().getResources().getStringArray(R.array.gender_list);

        ArrayAdapter specialityAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, specialityArray);
        specialityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialitySp.setAdapter(specialityAdapter);

        ArrayAdapter genderAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, genderArray);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSp.setAdapter(genderAdapter);


        if (currentDisplayType != Constants.CREATE_MODE) {
            certificate.setVisibility(View.VISIBLE);
            driverLicense.setVisibility(View.VISIBLE);
            addAddressTil.setVisibility(View.GONE);
        } else {
            certificate.setVisibility(View.GONE);
            driverLicense.setVisibility(View.GONE);

            if (createUserRequestModel != null &&
                    createUserRequestModel.getUser_detail().getData().getSpecialties() != null &&
                    createUserRequestModel.getUser_detail().getData().getSpecialties().size() > 0)
                specialitySp.setSelection(getSpecialityPosition(createUserRequestModel.getUser_detail().getData().getSpecialties().get(0).getName()));
        }

        if (getArguments() != null) {
            isCreateManually = getArguments().getBoolean(Constants.IS_CREATE_MANUALLY);

            if (isCreateManually) {

                searchKey = getArguments().getString(Constants.SEARCH_KEY);
                firstnameEt.setText(searchKey);

            } else {
                isNewPractice = getArguments().getBoolean(Constants.IS_NEW_PRACTICE);

                if (!isNewPractice)
                    practiceId = getArguments().getInt(Constants.PRACTICE_ID);

                doctorDetailModel = (GetDoctorsApiResponseModel.DataBean) getArguments().getSerializable(Constants.USER_DETAIL);

                if (currentDisplayType == Constants.CREATE_MODE) {
                    if (doctorDetailModel != null) {
                        if (!isCreateManually) {
                            updateUserRequestModel();
                        } else {
                            createUserRequestModel.getUser_data().setFirst_name(searchKey);
                            setData();
                        }
                    }
                }
            }
        }

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

        validateDetails();
    }

    private void updateUI(WhoAmIApiResponseModel whoAmIApiResponseModel) {
        createUserRequestModel.setUser_data(new CreateUserRequestModel.UserDataBean(whoAmIApiResponseModel));
        createUserRequestModel.setUser_detail(whoAmIApiResponseModel.getUser_detail());

        updateUI(createUserRequestModel);
    }

    private void updateUI(CreateUserRequestModel createUserRequestModel) {
        firstnameEt.setText(createUserRequestModel.getUser_data().getFirst_name());
        lastnameEt.setText(createUserRequestModel.getUser_data().getLast_name());
        titleEt.setText(createUserRequestModel.getUser_detail().getData().getTitle());
        bioEt.setText(createUserRequestModel.getUser_detail().getData().getBio());
        websiteEt.setText(createUserRequestModel.getUser_detail().getData().getWebsite());
        npiEt.setText(createUserRequestModel.getUser_detail().getData().getNpi());
        liabilityEt.setText(createUserRequestModel.getUser_detail().getData().getLiability());

        try {
            String title = createUserRequestModel.getUser_detail().getData().getSpecialties().get(0).getName();
            List speciality = Arrays.asList(specialityArray);
            specialitySp.setSelection(speciality.indexOf(title), false);
            specialistTv.setText(specialitySp.getSelectedItem().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String gender = createUserRequestModel.getUser_data().getGender();
            List genders = Arrays.asList(genderArray);
            genderSp.setSelection(genders.indexOf(gender), false);
            genderValue.setText(genderSp.getSelectedItem().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setProfileImage();
        setPractices();
        setPhoneList();
        setLicenseList();
        setCertificate();
        setLicense();

        validateDetails();
        checkFields();
    }

    private void reloadUI() {
        switch (currentDisplayType) {
            case Constants.EDIT_MODE:
                updateAllViews(true);
                onViewChangeInterface.updateNextTitle(getString(R.string.Save));
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
        validateDetails();

        if (doctorLicenseListAdapter != null) {
            doctorLicenseListAdapter.notifyDataSetChanged();
        }

        if (officePhoneListAdapter != null) {
            officePhoneListAdapter.notifyDataSetChanged();
        }

    }

    private void updateAllViews(Boolean enabled) {
        Utils.setEditable(firstnameEt, enabled);
        Utils.setEditable(lastnameEt, enabled);
        Utils.setEditable(titleEt, enabled);

        Utils.setEditable(npiEt, enabled);
        Utils.setEditable(liabilityEt, enabled);
        Utils.setEditable(websiteEt, enabled);

        if (enabled) {
            genderValue.setVisibility(View.GONE);
            genderSp.setVisibility(View.VISIBLE);

            specialistTv.setVisibility(View.GONE);
            specialitySp.setVisibility(View.VISIBLE);

            liabilityEt.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            genderValue.setVisibility(View.VISIBLE);
            genderSp.setVisibility(View.GONE);

            specialistTv.setVisibility(View.VISIBLE);
            specialitySp.setVisibility(View.GONE);
        }
    }

    private void updateUserRequestModel() {

        createUserRequestModel.getUser_data().setAppt_length(15);
        createUserRequestModel.getUser_data().setFirst_name(doctorDetailModel.getProfile().getFirst_name());
        createUserRequestModel.getUser_data().setLast_name(doctorDetailModel.getProfile().getLast_name());
        createUserRequestModel.getUser_data().setGender(doctorDetailModel.getProfile().getGender());

        createUserRequestModel.getUser_detail().getData().setNpi(doctorDetailModel.getNpi());
        createUserRequestModel.getUser_detail().getData().setTitle(doctorDetailModel.getProfile().getTitle());

        createUserRequestModel.getUser_detail().getData().setBio(doctorDetailModel.getProfile().getBio());
        createUserRequestModel.getUser_detail().getData().setUid(doctorDetailModel.getUid());
        createUserRequestModel.getUser_detail().getData().setImage_url(doctorDetailModel.getProfile().getImage_url());

        List<LicensesBean> licensesBeanList = new ArrayList<>();
        for (int i = 0; i < doctorDetailModel.getLicenses().size(); i++) {
            licensesBeanList.add(new LicensesBean(doctorDetailModel.getLicenses().get(i).getState(),
                    doctorDetailModel.getLicenses().get(i).getNumber(), doctorDetailModel.getLicenses().get(i).getEnd_date()));
        }
        createUserRequestModel.getUser_detail().getData().setLicenses(licensesBeanList);

        List<SpecialtiesBean> specialtiesBeanList = new ArrayList<>();
        for (int i = 0; i < doctorDetailModel.getSpecialties().size(); i++) {
            specialtiesBeanList.add(new SpecialtiesBean(doctorDetailModel.getSpecialties().get(i).getActors(),
                    doctorDetailModel.getSpecialties().get(i).getDescription(),
                    doctorDetailModel.getSpecialties().get(i).getCategory(),
                    doctorDetailModel.getSpecialties().get(i).getUid(),
                    doctorDetailModel.getSpecialties().get(i).getName(),
                    doctorDetailModel.getSpecialties().get(i).getActor()));
        }
        createUserRequestModel.getUser_detail().getData().setSpecialties(specialtiesBeanList);

        if (!isNewPractice) {
            List<PracticesBean> practicesBeanList = new ArrayList<>();
            for (int i = 0; i < doctorDetailModel.getPractices().size(); i++) {
                practicesBeanList.add(new PracticesBean(doctorDetailModel.getPractices().get(i).getName(),
                        doctorDetailModel.getPractices().get(i).getWebsite(),
                        doctorDetailModel.getPractices().get(i).getVisit_address(),
                        doctorDetailModel.getPractices().get(i).getPhones()));
            }
            createUserRequestModel.getUser_detail().getData().setPractices(practicesBeanList);
        } else {
//            List<PracticesBean> practicesBeanList = new ArrayList<>();
//            practicesBeanList.add(new PracticesBean(null, null,
//                    null,
//                    new ArrayList<>()));
//            createUserRequestModel.getUser_detail().getData().setPractices(practicesBeanList);
        }

        setData();

        checkFields();

    }

    public void checkFields() {
        if (firstnameEt.getText().toString().isEmpty()) {
            setError(firstnameEt);
        }
        if (lastnameEt.getText().toString().isEmpty()) {
            setError(lastnameEt);
        }
        if (titleEt.getText().toString().isEmpty()) {
            setError(titleEt);
        }
        if (bioEt.getText().toString().isEmpty()) {
            setError(bioEt);
        }
        if (npiEt.getText().toString().isEmpty()) {
            setError(npiEt);
        }
        if (liabilityEt.getText().toString().isEmpty()) {
            setError(liabilityEt);
        }
        if (createUserRequestModel.getUser_detail().getData().getPractices().size() == 0) {
            setError(addAddressEt);
        }

        if (createUserRequestModel.getUser_detail().getData().getLicenses().size() == 0) {
            setError(addLicenseEt);
        }

        if (!isCreateManually && isNewPractice) {
            if (createUserRequestModel.getUser_detail().getData().getPractices() == null ||
                    createUserRequestModel.getUser_detail().getData().getPractices().isEmpty() ||
                    createUserRequestModel.getUser_detail().getData().getPractices().get(0).getVisit_address() == null) {
                setError(addAddressEt);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setData() {

        setProfileImage();

        firstnameEt.setText(createUserRequestModel.getUser_data().getFirst_name());
        lastnameEt.setText(createUserRequestModel.getUser_data().getLast_name());
        titleEt.setText(createUserRequestModel.getUser_detail().getData().getTitle());
        bioEt.setText(createUserRequestModel.getUser_detail().getData().getBio());
        genderSp.setSelection(getGenderPosition(createUserRequestModel.getUser_data().getGender()));
        npiEt.setText(createUserRequestModel.getUser_detail().getData().getNpi());
        liabilityEt.setText(createUserRequestModel.getUser_detail().getData().getLiability());

        if (createUserRequestModel != null &&
                createUserRequestModel.getUser_detail().getData().getPractices().size() > 0) {
            websiteEt.setText(createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getWebsite());
        }

        setPractices();

        setPhoneList();

        setLicenseList();

    }

    private void setLicenseList() {

        if (createUserRequestModel.getUser_detail().getData().getLicenses().size() > 0) {
            licenseLl.setVisibility(View.VISIBLE);
        } else {
            licenseLl.setVisibility(View.GONE);
        }

        licenseRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        doctorLicenseListAdapter = new DoctorLicenseListAdapter(getActivity(), this);
        licenseRv.setAdapter(doctorLicenseListAdapter);
    }

    private void setPhoneList() {
        officePhoneRv.setLayoutManager(new LinearLayoutManager(getContext()));
        officePhoneListAdapter = new OfficePhoneListAdapter(getActivity(), practiceId, isNewPractice, this);
        officePhoneRv.setAdapter(officePhoneListAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void setPractices() {

        Log.e("aswin", "setPractices: " + new Gson().toJson(createUserRequestModel.getUser_detail().getData().getPractices()) + " " + practiceId);
        if (createUserRequestModel != null &&
                !createUserRequestModel.getUser_detail().getData().getPractices().isEmpty() &&
                createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address() != null) {

            practiceTil.setVisibility(View.VISIBLE);
            addAddressTil.setVisibility(View.GONE);

            String name = (createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getName() != null) ? createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getName() : "";
            String street = (createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getStreet() != null) ? createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getStreet() : "";
            String street2 = (createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getStreet2() != null) ? createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getStreet2() : "";
            String city = (createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getCity() != null) ? createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getCity() : "";
            String state = (createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getState() != null) ? createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getState() : "";
            String zip = (createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getZip() != null) ? createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getVisit_address().getZip() : "";

            practiceEt.setText(name + "\n" +
                    street + "\n" +
                    street2 + "\n" +
                    city + "\n" +
                    state + "\n" +
                    zip);

            Log.e("aswin", "setPractices: " + practiceEt.getText().toString());
        } else {
            practiceTil.setVisibility(View.GONE);
            addAddressTil.setVisibility(View.VISIBLE);
        }
    }


    private int getSpecialityPosition(String specialist) {
        for (int i = 0; i < specialityArray.length; i++) {
            if (specialityArray[i].equalsIgnoreCase(specialist))
                return i;
        }
        return 0;
    }

    private int getGenderPosition(String gender) {
        for (int i = 0; i < genderArray.length; i++) {
            if (genderArray[i].equalsIgnoreCase(gender))
                return i;
        }
        return 0;
    }

    private void setProfileImage() {
        if (doctorImagePath != null && !doctorImagePath.isEmpty()) {
            profileCiv.setImageBitmap(getBitmpaFromPath(doctorImagePath));
        } else if (createUserRequestModel != null && createUserRequestModel.getUser_data().getUser_avatar() != null && !createUserRequestModel.getUser_data().getUser_avatar().isEmpty()) {
            Utils.setImageWithGlide(getActivity().getApplicationContext(), profileCiv, createUserRequestModel.getUser_data().getUser_avatar(), getContext().getDrawable(R.drawable.profile_placeholder), true);
        } else if (createUserRequestModel != null &&
                createUserRequestModel.getUser_detail() != null &&
                createUserRequestModel.getUser_detail().getData().getImage_url() != null &&
                !createUserRequestModel.getUser_detail().getData().getImage_url().isEmpty()) {
            Utils.setImageWithGlide(getActivity().getApplicationContext(), profileCiv, createUserRequestModel.getUser_detail().getData().getImage_url(), getContext().getDrawable(R.drawable.profile_placeholder), false);
        }
    }

    private void addTextWatcher(EditText editText, TextInputLayout textInputLayout) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (editText.getText().toString().isEmpty() &&
                        !hasFocus) {
                    setError(editText);
                    validateDetails();
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateDetails();
            }
        });
    }

    private void setError(EditText editText) {
        switch (editText.getId()) {
            case R.id.firstname_et:
                firstnameTil.setError(getString(R.string.firstname_empty_error));
                break;
            case R.id.lastname_et:
                lastnameTil.setError(getString(R.string.lastname_empty_error));
                break;
            case R.id.title_et:
                titleTil.setError(getString(R.string.title_empty_error));
                break;
            case R.id.bio_et:
                bioTil.setError(getString(R.string.bio_empty_error));
                break;
            case R.id.add_address_et:
                addAddressTil.setError(getString(R.string.address_empty_error));
                break;
            case R.id.add_license_et:
                addLicenseTil.setError(getString(R.string.license_empty_error));
                break;
            case R.id.npi_et:
                npiTil.setError(getString(R.string.npi_empty_error));
                break;
            case R.id.liability_et:
                liabilityInsuranceTil.setError(getString(R.string.liability_empty_error));
                break;
        }
    }

    public void validateDetails() {
        if (currentDisplayType == Constants.VIEW_MODE) {
            onViewChangeInterface.enableNext(true);
        } else if (!firstnameEt.getText().toString().isEmpty() &&
                !lastnameEt.getText().toString().isEmpty() &&
                !titleEt.getText().toString().isEmpty() &&
                !bioEt.getText().toString().isEmpty() &&
                createUserRequestModel.getUser_detail().getData().getPractices().size() > 0 &&
                createUserRequestModel.getUser_detail().getData().getLicenses().size() > 0 &&
                hasValidLicense() &&
                !npiEt.getText().toString().isEmpty() &&
                !liabilityEt.getText().toString().isEmpty()) {

            onViewChangeInterface.enableNext(true);
        } else {
            onViewChangeInterface.enableNext(false);
        }
    }

    private boolean hasValidLicense() {
        boolean isValidLicense = true;

        for (Boolean isValid :
                createUserRequestModel.getHasValidLicensesList()) {
            if (!isValid) {
                isValidLicense = false;
                break;
            }
        }
        return isValidLicense;
    }

    @Override
    public void onClick(View v) {
        if (currentDisplayType == Constants.VIEW_MODE) {
            return;
        }

        switch (v.getId()) {
            case R.id.bio_et:
                DoctorBioBottomSheetFragment doctorBioBottomSheetFragment = new DoctorBioBottomSheetFragment();

                doctorBioBottomSheetFragment.setTargetFragment(this, RequestID.REQ_BIO);
                doctorBioBottomSheetFragment.show(getFragmentManager(), doctorBioBottomSheetFragment.getClass().getSimpleName());
                break;
            case R.id.add_address_et:
                DoctorPracticesBottomSheetFragment addPracticeBottomSheetFragment = new DoctorPracticesBottomSheetFragment();

                Bundle newPracticeBundle = new Bundle();
                newPracticeBundle.putBoolean(Constants.IS_NEW_PRACTICE, true);
                addPracticeBottomSheetFragment.setArguments(newPracticeBundle);
                addPracticeBottomSheetFragment.setTargetFragment(this, RequestID.REQ_PRACTICE);
                addPracticeBottomSheetFragment.show(getFragmentManager(), addPracticeBottomSheetFragment.getClass().getSimpleName());
                break;
            case R.id.practice_et:
                DoctorPracticesBottomSheetFragment doctorPracticesBottomSheetFragment = new DoctorPracticesBottomSheetFragment();
                if (!isNewPractice) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.PRACTICE_ID, practiceId);
                    doctorPracticesBottomSheetFragment.setArguments(bundle);
                }

                doctorPracticesBottomSheetFragment.setTargetFragment(this, RequestID.REQ_PRACTICE);
                doctorPracticesBottomSheetFragment.show(getFragmentManager(), doctorPracticesBottomSheetFragment.getClass().getSimpleName());
                break;
            case R.id.add_license_et:
                DoctorLicenseBottomSheetFragment doctorLicenseBottomSheetFragment = new DoctorLicenseBottomSheetFragment();
                doctorLicenseBottomSheetFragment.setTargetFragment(this, RequestID.REQ_LICENSE);
                doctorLicenseBottomSheetFragment.show(getFragmentManager(), doctorLicenseBottomSheetFragment.getClass().getSimpleName());
                break;
            case R.id.profile_civ:
            case R.id.driver_license_iv:
            case R.id.certificate_iv:
                currentGalleryCallingId = v.getId();
                CameraUtil.showImageSelectionAlert(getActivity());
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("aswin", "onActivityResult: " + requestCode + " " + resultCode);
        switch (requestCode) {
            case RequestID.REQ_BIO:
                updateBio();
                break;
            case RequestID.REQ_PRACTICE:
                setPractices();
                if (createUserRequestModel.getUser_detail().getData().getPractices().size() == 0) {
                    addAddressTil.setError(getString(R.string.address_empty_error));
                } else {
                    addAddressTil.setErrorEnabled(false);
                }
                break;
            case RequestID.REQ_LICENSE:
                setLicenseList();
                if (createUserRequestModel.getUser_detail().getData().getLicenses().size() == 0) {
                    addLicenseTil.setError(getString(R.string.license_empty_error));
                } else {
                    addLicenseTil.setErrorEnabled(false);
                }
                break;
        }
        validateDetails();
    }

    private void updateBio() {
        bioEt.setText(createUserRequestModel.getUser_detail().getData().getBio());

        if (bioEt.getText().toString().isEmpty()) {
            bioTil.setError(getString(R.string.bio_empty_error));
        }
    }

    @Override
    public int getCurrentMode() {
        return currentDisplayType;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isCreateManually && currentDisplayType == Constants.CREATE_MODE) {
            checkFields();
        }

        if (currentDisplayType == Constants.CREATE_MODE) {
            onViewChangeInterface.updateTitle(getString(R.string.profile));
        } else {
            onViewChangeInterface.updateTitle(UserDetailPreferenceManager.getUserDisplayName());
        }

        reloadUI();
    }

    @Override
    public void onDestroy() {
        createUserRequestModel.clearData();
        super.onDestroy();
    }

    @Override
    public void onImageReceived(String imagePath) {
        switch (currentGalleryCallingId) {
            case R.id.driver_license_iv:
                createUserRequestModel.setDoctor_driving_license_path(imagePath);
                setLicense();
                break;
            case R.id.certificate_iv:
                createUserRequestModel.setDoctor_certificate_path(imagePath);
                setCertificate();
                break;
            case R.id.profile_civ:
                doctorImagePath = imagePath;
                createUserRequestModel.setUser_avatar_path(imagePath);
                setProfileImage();
                break;
        }
    }

    private void setLicense() {
        if (createUserRequestModel != null && createUserRequestModel.getDoctor_driving_license_path() != null && !createUserRequestModel.getDoctor_driving_license_path().isEmpty()) {
            driverLicenseIv.setImageBitmap(getBitmpaFromPath(createUserRequestModel.getDoctor_driving_license_path()));
        } else if (createUserRequestModel != null && createUserRequestModel.getUser_detail().getData().getDriver_license() != null) {
            Utils.setImageWithGlide(getActivity().getApplicationContext(), driverLicenseIv, createUserRequestModel.getUser_detail().getData().getDriver_license(), getContext().getDrawable(R.drawable.placeholder_certificate), true);
        }
    }

    private void setCertificate() {
        if (createUserRequestModel != null && createUserRequestModel.getDoctor_certificate_path() != null && !createUserRequestModel.getDoctor_certificate_path().isEmpty()) {
            certificateIv.setImageBitmap(getBitmpaFromPath(createUserRequestModel.getDoctor_certificate_path()));
            certificateIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (createUserRequestModel != null && createUserRequestModel.getUser_detail().getData().getDiploma_certificate() != null) {
            Utils.setImageWithGlide(getActivity().getApplicationContext(), certificateIv, createUserRequestModel.getUser_detail().getData().getDiploma_certificate(), getContext().getDrawable(R.drawable.placeholder_certificate), true);
            certificateIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void updatedProfile() {
        onActionCompleteInterface.onCompletionResult(RequestID.PROFILE_UPDATED, true, null);
    }

    @Override
    public void doCurrentTransaction() {
        switch (currentDisplayType) {
            case Constants.EDIT_MODE:
                createUserRequestModel();
                if (needToPutDoctorInOnboarding()) {
                    createUserRequestModel.getUser_data().setStatus(Constants.ONBOARDING_PENDING);
                }
                updateProfileModel.updateDoctor(createUserRequestModel);
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

    private boolean needToPutDoctorInOnboarding() {
        if (createUserRequestModel.getDoctor_driving_license_path() != null || createUserRequestModel.getDoctor_certificate_path() != null) {
            return true;
        } else if (!UserDetailPreferenceManager.getNpi().equals(createUserRequestModel.getUser_detail().getData().getNpi())) {
            return true;
        } else if (UserDetailPreferenceManager.getLicenses().size() != createUserRequestModel.getUser_detail().getData().getLicenses().size()) {
            return true;
        } else {
            Boolean needToPost = false;

            for (LicensesBean license : createUserRequestModel.getUser_detail().getData().getLicenses()) {
                for (LicensesBean currentLicense : UserDetailPreferenceManager.getLicenses()) {
                    if (!license.isEqual(currentLicense)) {
                        needToPost = true;
                        break;
                    }
                }
            }

            return needToPost;
        }
    }

    private void createUserRequestModel() {
        createUserRequestModel.setUser_avatar_path(doctorImagePath);
        createUserRequestModel.getUser_data().setFirst_name(firstnameEt.getText().toString());
        createUserRequestModel.getUser_data().setLast_name(lastnameEt.getText().toString());
        createUserRequestModel.getUser_data().setGender(genderSp.getSelectedItem().toString());

        createUserRequestModel.getUser_detail().getData().setTitle(titleEt.getText().toString());
        createUserRequestModel.getUser_detail().getData().setNpi(npiEt.getText().toString());
        createUserRequestModel.getUser_detail().getData().setLiability(liabilityEt.getText().toString());

        List<SpecialtiesBean> specialtiesBeans = createUserRequestModel.getUser_detail().getData().getSpecialties();
        if (specialtiesBeans.size() > 0) {
            specialtiesBeans.get(0).setName(specialitySp.getSelectedItem().toString());
            createUserRequestModel.getUser_detail().getData().setSpecialties(specialtiesBeans);
        }


        createUserRequestModel.getUser_detail().getData().setWebsite(websiteEt.getText().toString());

    }
}
